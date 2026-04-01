package de.team33.service.images.main;

import de.team33.patterns.io.adrastea.FileEntry;
import de.team33.patterns.lazy.narvi.Lazy;
import de.team33.service.images.core.AliasMap;
import de.team33.service.images.core.Direction;
import de.team33.service.images.core.Locator;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Stream;

class Request {

    private final AliasMap.Entry aliasEntry;
    private final Locator locator;
    private final Lazy<ImageType> lazyImageType;

    Request(final AliasMap aliasMap, final HttpServletRequest request, final String alias) {
        this.aliasEntry = aliasMap.get(alias);
        this.locator = Locator.by(Util.CONTROLLER_ROOT, aliasEntry)
                              .setResourceUri(request.getRequestURI())
                              .setRequestUrl(request.getRequestURL().toString())
                              .build();
        this.lazyImageType = Lazy.init(() -> ImageType.of(locator.resourcePath()));
    }

    final Locator locator() {
        return locator;
    }

    final Comparator<FileEntry> order() {
        final Direction direction = Optional.ofNullable(aliasEntry.direction())
                                            .orElse(Direction.ASC);
        return Optional.ofNullable(aliasEntry.order())
                       .map(direction::map)
                       .orElse(null);
    }

    final ImageType imageType() {
        return lazyImageType.get();
    }

    final boolean isInconsistent() {
        return !locator.resourcePath().startsWith(locator.basePath());
    }

    final boolean isShowHTML() {
        return Stream.of("show", "show.htm", "show.html")
                     .anyMatch(locator.resourcePath()::endsWith);
    }

    final boolean isShowJS() {
        return locator.resourcePath().endsWith("show.js");
    }

    final boolean isShowCSS() {
        return locator.resourcePath().endsWith("show.css");
    }

    final boolean isShowJson() {
        return locator.resourcePath().endsWith("index.json");
    }

    final boolean isImage() {
        return null != imageType();
    }
}
