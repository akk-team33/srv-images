package de.team33.service.images.main;

import de.team33.patterns.decision.thyone.Choices;
import de.team33.service.images.core.AliasMap;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {

    private final AliasMap aliasMap;

    public Controller(final Properties properties) {
        this.aliasMap = properties.getEntries().stream()
                                  .map(AliasMap.Entry::normalize)
                                  .collect(AliasMap::builder, AliasMap.Builder::put, AliasMap.Builder::putAll)
                                  .build();
    }

    @GetMapping(value = "/", produces = MediaType.TEXT_HTML_VALUE)
    public Resource getRoot() {
        return new ClassPathResource("index.html", Controller.class);
    }

    @GetMapping(value = "/{name}", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<?> getByName(final HttpServletRequest httpRequest, @PathVariable("name") final String name) {
        final var byNameFunction = Choices.serial(RequestByName::isFavicon);
        final var request = new RequestByName(aliasMap, httpRequest, name);
        return ResponseEntity.notFound().build();
    }

    @GetMapping(value = "/{alias}/**", produces = MediaType.TEXT_PLAIN_VALUE)
    public String getByAlias(final HttpServletRequest request, @PathVariable("alias") final String alias) {
        return "Controller.getByAlias(): '%s' - url: %s".formatted(alias, request.getRequestURL());
    }
}
