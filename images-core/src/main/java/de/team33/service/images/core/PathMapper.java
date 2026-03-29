package de.team33.service.images.core;

import java.net.URI;
import java.nio.file.Path;

public class PathMapper {

    private final Path basePath;
    private final URI relativeUri;
    private final Path resourcePath;
    private final URI serviceUri;

    private PathMapper(final Builder builder) {
        basePath = builder.basePath;
        relativeUri = relativeUri(builder.resourceUri, builder.baseUri.toString());
        resourcePath = resourcePath(basePath, relativeUri);
        serviceUri = URI.create(builder.requestUrl.replace(relativeUri.toString(), ""));
    }

    private static URI relativeUri(final String resourceUri, final String baseUri) {
        return URI.create(resourceUri.substring(baseUri.length() + 1));
    }

    private static Path resourcePath(final Path basePath, final URI relativeUri) {
        return Path.of(basePath.toUri().resolve(relativeUri));
    }

    public static Builder mapping(final String controllerRoot, final AliasMap.Entry alias) {
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

        private final String alias;
        private final Path basePath;
        private final URI baseUri;
        private String resourceUri;
        private String requestUrl;

        private Builder(final String controllerRoot, final AliasMap.Entry alias) {
            this.alias = alias.name();
            this.basePath = alias.path();
            this.baseUri = URI.create(controllerRoot).resolve(this.alias);
        }

        public final PathMapper build() {
            return new PathMapper(this);
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
