package de.team33.service.images.main;

import de.team33.patterns.decision.thyone.Choices;
import de.team33.patterns.files.pluto.FileEntry;
import de.team33.patterns.files.styx.Styx;
import de.team33.patterns.lazy.janus.Features;
import de.team33.service.images.core.AliasMap;
import de.team33.service.images.core.Direction;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

class RequestByAlias extends RequestBase {

    private static final Function<RequestByAlias, ResponseEntity<?>> MAPPING =
            Choices.serial(RequestByAlias::isNoJPG,
                           RequestByAlias::isNothingPNG,
                           RequestByAlias::isShowRPNG,
                           RequestByAlias::isFolderupRPNG,
                           RequestByAlias::isIndexCSS,
                           RequestByAlias::isIndexJS,
                           RequestByAlias::isIndexHTML,
                           RequestByAlias::isIndexJson,
                           RequestByAlias::isShowCSS,
                           RequestByAlias::isShowJS,
                           RequestByAlias::isShowHTML,
                           RequestByAlias::isShowJson,
                           RequestByAlias::isImage)
                   .applying(RequestByAlias::toNoJPG,
                             RequestByAlias::toNothingPNG,
                             RequestByAlias::toShowRPNG,
                             RequestByAlias::toFolderupRPNG,
                             RequestByAlias::toIndexCSS,
                             RequestByAlias::toIndexJS,
                             RequestByAlias::toIndexHTML,
                             RequestByAlias::toIndexJson,
                             RequestByAlias::toShowCSS,
                             RequestByAlias::toShowJS,
                             RequestByAlias::toShowHTML,
                             RequestByAlias::toShowJson,
                             RequestByAlias::toImage,
                             RequestByAlias::notFound);

    private final Features features = new Features();
    private final AliasMap aliasMap;
    private final String alias;

    RequestByAlias(final AliasMap aliasMap, final HttpServletRequest httpRequest, final String alias) {
        super(httpRequest);
        this.aliasMap = aliasMap;
        this.alias = alias;
    }

    private boolean isNoJPG() {
        return uriEndsWith("NO.JPG");
    }

    private boolean isNothingPNG() {
        return uriEndsWith("NOTHING.PNG");
    }

    private boolean isShowCSS() {
        return uriEndsWith("show.css");
    }

    private boolean isShowJS() {
        return uriEndsWith("show.js");
    }

    private boolean isShowHTML() {
        return uriEndsWith("show", "show.htm", "show.html");
    }

    private boolean isShowJson() {
        return uriEndsWith("show.json");
    }

    private boolean isImage() {
        return null != imageType();
    }

    final ResponseEntity<?> toIndexHTML() {
        return classPathResponse(MediaType.TEXT_HTML, "index.html");
    }

    private static boolean hasImages(final FileEntry entry) {
        return Styx.stream(entry)
                   .map(FileEntry::resolved)
                   .filter(FileEntry::isRegularFile)
                   .anyMatch(ImageType::isMatching);
    }

    private ResponseEntity<?> toIndexJson() {
        final FileEntry entry = FileEntry.resolved(resourcePath().getParent());
        if (entry.isDirectory()) {
            // relative ...
            final String target = entry.path().toUri().toString(); // absolute: locator.basePath().toUri().toString();
            final String replacement = "";                         // absolute: locator.serviceUri().toString();
            final List<String> list = Styx.children(entry)
                                          .filter(FileEntry::isDirectory)
                                          .filter(RequestByAlias::hasImages)
                                          .map(FileEntry::path)
                                          .map(Path::toUri)
                                          .map(URI::toString)
                                          .map(s -> s.replace(target, replacement))
                                          .toList();
            return jsonResponse(list);
        }
        return notFound();
    }

    private ResponseEntity<?> toNoJPG() {
        return classPathResponse(MediaType.IMAGE_GIF, "busy.gif");
    }

    private ResponseEntity<?> toNothingPNG() {
        return classPathResponse(MediaType.IMAGE_PNG, "nothing.png");
    }

    private ResponseEntity<?> toShowCSS() {
        return classPathResponse(MediaType.valueOf("text/css"), "show.css");
    }

    private ResponseEntity<?> toShowJS() {
        return classPathResponse(MediaType.valueOf("application/javascript"), "show.js");
    }

    private ResponseEntity<?> toShowHTML() {
        return classPathResponse(MediaType.TEXT_HTML, "show.html");
    }

    private ResponseEntity<?> toShowJson() {
        final FileEntry entry = FileEntry.resolved(resourcePath().getParent());
        if (entry.isDirectory()) {
            // relative ...
            final String target = entry.path().toUri().toString(); // absolute: basePath.toUri().toString();
            final String replacement = "";                         // absolute: serviceUri.toString();
            final var stage = Styx.stream(entry) //.parallel()
                                  .map(FileEntry::resolved)
                                  .filter(FileEntry::isRegularFile)
                                  .filter(ImageType::isMatching);
            //noinspection DataFlowIssue
            final var list = Optional.ofNullable(order())
                                     .map(stage::sorted)
                                     .orElse(stage)
                                     .map(FileEntry::path)
                                     .map(Path::toUri)
                                     .map(URI::toString)
                                     .map(s -> s.replace(target, replacement))
                                     .toList();
            return jsonResponse(list);
        }
        return notFound();
    }

    private ResponseEntity<?> toImage() {
        final Path resourcePath = resourcePath();
        if (Files.isRegularFile(resourcePath)) {
            return ResponseEntity.ok()
                                 .contentType(imageType().mediaType())
                                 .body(new FileSystemResource(resourcePath));
        }
        return notFound();
    }

    @Override
    final ResponseEntity<?> response() {
        return MAPPING.apply(this);
    }

    private AliasMap.Entry entry() {
        return features.get(Key.ENTRY, () -> aliasMap.get(alias));
    }

    private Path resourcePath() {
        return features.get(Key.RESOURCE_PATH,
                            () -> Key.resourcePath(entry().path(),
                                                   Key.relativeUri(httpRequest().getRequestURI(),
                                                                   Key.baseUri(alias).toString())));
    }

    private Comparator<FileEntry> order() {
        return features.get(Key.ORDER, () -> Key.order(entry()));
    }

    private ImageType imageType() {
        return features.get(Key.IMAGE_TYPE, () -> ImageType.of(resourcePath()));
    }

    private interface Key<R> extends Features.Key<R> {

        Key<AliasMap.Entry> ENTRY = proxy("ENTRY");
        Key<Path> RESOURCE_PATH = proxy("RESOURCE_PATH");
        Key<ImageType> IMAGE_TYPE = proxy("IMAGE_TYPE");
        Key<Comparator<FileEntry>> ORDER = proxy("ORDER");

        static <R> Key<R> proxy(final String name) {
            return new Key<>() {

                @Override
                public String toString() {
                    return name;
                }
            };
        }

        static URI baseUri(final String alias) {
            return URI.create(Util.CONTROLLER_ROOT).resolve(alias);
        }

        static URI relativeUri(final String resourceUri, final String baseUri) {
            final int index = Integer.min(baseUri.length() + 1, resourceUri.length());
            return URI.create(resourceUri.substring(index));
        }

        static Path resourcePath(Path basePath, URI relativeUri) {
            return Path.of(basePath.toUri().resolve(relativeUri));
        }

        static Comparator<FileEntry> order(final AliasMap.Entry entry) {
            final Direction direction = Optional.ofNullable(entry.direction())
                                                .orElse(Direction.ASC);
            return Optional.ofNullable(entry.order())
                           .map(direction::map)
                           .orElse(null);
        }
    }
}
