package de.team33.service.images.main;

import de.team33.service.images.core.AliasMap;
import jakarta.servlet.http.HttpServletRequest;

class RequestByName {

    private final String name;

    RequestByName(final AliasMap aliasMap, final HttpServletRequest httpRequest, final String name) {
        this.name = name;
    }

    final boolean isFavicon() {
        return "favicon.ico".equals(name);
    }
}
