package de.team33.service.images.main;

import org.springframework.http.MediaType;

import java.util.List;
import java.util.stream.Stream;

public enum ImageType {

    AVIF(MediaType.valueOf("image/avif"), "avif"),
    WebP(MediaType.valueOf("image/webp"), "webp"),
    JPEG(MediaType.IMAGE_JPEG, "jpeg", "jpg", "jpe", "jfif"),
    PNG(MediaType.IMAGE_PNG, "png"),
    SVG(MediaType.valueOf("image/svg+xml"), "svg"),
    GIF(MediaType.IMAGE_GIF, "gif");

    private final MediaType mediaType;
    private final List<String> extensions;

    ImageType(final MediaType mediaType, final String... extensions) {
        this.mediaType = mediaType;
        this.extensions = Stream.of(extensions).map(ext -> "." + ext)
                                .toList();
    }
}
