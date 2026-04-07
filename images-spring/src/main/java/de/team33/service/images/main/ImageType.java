package de.team33.service.images.main;

import de.team33.patterns.enums.pan.Values;
import de.team33.patterns.io.adrastea.FileEntry;
import org.springframework.http.MediaType;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

enum ImageType {

    AVIF(MediaType.valueOf("image/avif"), "avif"),
    WebP(MediaType.valueOf("image/webp"), "webp"),
    JPEG(MediaType.IMAGE_JPEG, "jpeg", "jpg", "jpe", "jfif"),
    PNG(MediaType.IMAGE_PNG, "png"),
    SVG(MediaType.valueOf("image/svg+xml"), "svg"),
    GIF(MediaType.IMAGE_GIF, "gif");

    private static final Values<ImageType> VALUES = Values.of(ImageType.class);

    private final MediaType mediaType;
    private final List<String> extensions;

    ImageType(final MediaType mediaType, final String... extensions) {
        this.mediaType = mediaType;
        this.extensions = Stream.of(extensions).map(ext -> "." + ext)
                                .toList();
    }

    static ImageType of(final Path path) {
        return of(path.toString());
    }

    static ImageType of(final String path) {
        return VALUES.findFirst(type -> type.matches(path), null);
    }

    static boolean isMatching(final FileEntry entry) {
        return null != of(entry.path());
    }

    private boolean matches(final String path) {
        final String lowerCase = path.toLowerCase();
        return extensions.stream().anyMatch(lowerCase::endsWith);
    }

    final MediaType mediaType() {
        return mediaType;
    }
}
