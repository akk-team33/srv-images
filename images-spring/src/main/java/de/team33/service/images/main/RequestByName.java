package de.team33.service.images.main;

import de.team33.patterns.decision.thyone.Choices;
import de.team33.service.images.core.AliasMap;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.function.Function;
import java.util.stream.Stream;

class RequestByName extends RequestBase {

    private static final Function<RequestByName, ResponseEntity<? extends Object>> MAPPING =
            Choices.serial(RequestByName::isFavicon,
                           RequestByName::isAbout,
                           RequestByName::isIndexCSS,
                           RequestByName::isIndexJS,
                           RequestByName::isIndexHTML,
                           RequestByName::isIndexJson)
                   .applying(RequestByName::toFavIcon,
                             RequestByName::toAbout,
                             RequestByName::toIndexCSS,
                             RequestByName::toIndexJS,
                             RequestByName::toIndexHTML,
                             RequestByName::toIndexJson,
                             RequestByName::toNotFound);

    private final AliasMap aliasMap;
    private final String name;

    RequestByName(final AliasMap aliasMap, final HttpServletRequest httpRequest, final String name) {
        super(httpRequest);
        this.aliasMap = aliasMap;
        this.name = name;
    }

    private boolean isFavicon() {
        return "favicon.ico".equals(name);
    }

    private boolean isAbout() {
        return Stream.of("about", "about.htm", "about.html")
                     .anyMatch(about -> about.equalsIgnoreCase(name));
    }

    private ResponseEntity<?> toFavIcon() {
        return ResponseEntity.ok()
                             .contentType(MediaType.IMAGE_PNG)
                             .body(new ClassPathResource("favicon.png", RequestByName.class));
    }

    private ResponseEntity<?> toAbout() {
        return classPathResponse(MediaType.TEXT_HTML, "about.html");
    }

    private ResponseEntity<?> toIndexJson() {
        return jsonResponse(aliasMap.stream()
                                    .map(AliasMap.Entry::alias)
                                    .map(alias -> alias + '/')
                                    .toList());
    }

    @Override
    final ResponseEntity<?> response() {
        return MAPPING.apply(this);
    }
}
