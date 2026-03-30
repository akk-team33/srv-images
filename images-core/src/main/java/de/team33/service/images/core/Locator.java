package de.team33.service.images.core;

import java.net.URI;
import java.nio.file.Path;

public class Locator {

    private final Path basePath;
    private final URI relativeUri;
    private final Path resourcePath;
    private final URI serviceUri;

    private Locator(final Builder builder) {
        basePath = builder.basePath;
        relativeUri = relativeUri(builder.resourceUri, builder.baseUri.toString());
        resourcePath = resourcePath(basePath, relativeUri);
        serviceUri = URI.create(builder.requestUrl.replace(relativeUri.toString(), ""));
    }

    private static URI relativeUri(final String resourceUri, final String baseUri) {
        final int index = Integer.min(baseUri.length() + 1, resourceUri.length());
        return URI.create(resourceUri.substring(index));
    }

    private static Path resourcePath(final Path basePath, final URI relativeUri) {
        return Path.of(basePath.toUri().resolve(relativeUri));
    }

    public static Builder by(final String controllerRoot, final AliasMap.Entry alias) {
        return new Builder(controllerRoot, alias);
    }

    public final Path basePath() {
        return basePath;
    }

    public final URI relativeUri() {
        return relativeUri;
    }

    public final Path resourcePath() {
        return resourcePath;
    }

    public final URI serviceUri() {
        return serviceUri;
    }

    public static class Builder {

        private final Path basePath;
        private final URI baseUri;
        private String resourceUri;
        private String requestUrl;

        private Builder(final String controllerRoot, final AliasMap.Entry alias) {
            this.basePath = alias.path();
            this.baseUri = URI.create(controllerRoot).resolve(alias.name());
        }

        public final Locator build() {
            return new Locator(this);
        }

        public final Builder setResourceUri(final String uri) {
            this.resourceUri = uri;
            return this;
        }

        public final Builder setRequestUrl(final String url) {
            this.requestUrl = url;
            return this;
        }
    }
}
