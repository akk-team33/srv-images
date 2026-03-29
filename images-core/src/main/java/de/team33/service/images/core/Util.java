package de.team33.service.images.core;

import java.nio.file.FileSystems;
import java.nio.file.Path;

class Util {

    static Path noBasePath() {
        return FileSystems.getDefault().getRootDirectories().iterator().next().resolve("_NIRVANA_");
    }
}
