package de.team33.service.images.main;

import de.team33.patterns.io.adrastea.FileEntry;
import de.team33.service.images.core.AliasMap;
import de.team33.service.images.core.Direction;
import de.team33.service.images.core.Locator;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Comparator;
import java.util.Optional;

public class Request {

    private final AliasMap.Entry aliasEntry;
    private final Locator locator;

    public Request(final AliasMap aliasMap, final HttpServletRequest request, final String alias) {
        this.aliasEntry = aliasMap.get(alias);
        this.locator = Locator.by(Util.CONTROLLER_ROOT, aliasEntry)
                              .setResourceUri(request.getRequestURI())
                              .setRequestUrl(request.getRequestURL().toString())
                              .build();
    }

    public final AliasMap.Entry aliasEntry() {
        return aliasEntry;
    }

    public final Locator locator() {
        return locator;
    }

    public final Comparator<FileEntry> order() {
        final Direction direction = Optional.ofNullable(aliasEntry.direction())
                                            .orElse(Direction.ASC);
        return Optional.ofNullable(aliasEntry.order())
                       .map(direction::map)
                       .orElse(null);
    }
}
