package de.team33.service.images.main;

import java.nio.file.Path;

public class ImageAlias {

    private final String alias;
    private final Path path;

    public ImageAlias(String alias, Path path) {
        this.alias = alias;
        this.path = path;
    }

    public String getAlias() {
        return alias;
    }

    public Path getPath() {
        return path;
    }
}
