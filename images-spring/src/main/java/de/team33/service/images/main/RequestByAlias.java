package de.team33.service.images.main;

import de.team33.patterns.decision.thyone.Choices;
import de.team33.patterns.io.adrastea.FileEntry;
import de.team33.patterns.io.adrastea.LinkHandling;
import de.team33.patterns.lazy.narvi.Lazy;
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
                           RequestByAlias::isNothingJPG,
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
                             RequestByAlias::toNothingJPG,
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

    //private final Locator locator;
    private final Path basePath;
    private final URI baseUri;
    private final URI relativeUri;
    private final Path resourcePath;
    private final Comparator<FileEntry> order;
    private final Lazy<ImageType> lazyImageType;

    RequestByAlias(final AliasMap aliasMap, final HttpServletRequest httpRequest, final String alias) {
        super(httpRequest);
        final AliasMap.Entry entry = aliasMap.get(alias);
        this.basePath = Path.of(entry.path());
        this.baseUri = URI.create(Util.CONTROLLER_ROOT).resolve(entry.alias());
        this.relativeUri = relativeUri(httpRequest.getRequestURI(), baseUri.toString());
        this.resourcePath = resourcePath(this.basePath, this.relativeUri);
        this.order = order(entry);
        this.lazyImageType = Lazy.init(() -> ImageType.of(resourcePath));
    }

    private static URI relativeUri(final String resourceUri, final String baseUri) {
        final int index = Integer.min(baseUri.length() + 1, resourceUri.length());
        return URI.create(resourceUri.substring(index));
    }

    private static Path resourcePath(Path basePath, URI relativeUri) {
        return Path.of(basePath.toUri().resolve(relativeUri));
    }

    private static Comparator<FileEntry> order(final AliasMap.Entry entry) {
        final Direction direction = Optional.ofNullable(entry.direction())
                                            .orElse(Direction.ASC);
        return Optional.ofNullable(entry.order())
                       .map(direction::map)
                       .orElse(null);
    }

    private boolean isNoJPG() {
        return uriEndsWith("NO.JPG");
    }

    private boolean isNothingJPG() {
        return uriEndsWith("NOTHING.JPG");
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
        return null != lazyImageType.get();
    }

    private ResponseEntity<?> toIndexJson() {
        final FileEntry entry = FileEntry.of(resourcePath.getParent(), LinkHandling.RESOLVE);
        if (entry.isDirectory()) {
            // relative ...
            final String target = entry.path().toUri().toString(); // absolute: locator.basePath().toUri().toString();
            final String replacement = "";                         // absolute: locator.serviceUri().toString();
            final List<String> list = FileEntry.lister(LinkHandling.DISCLOSE)
                                               .list(entry)
                                               .stream()
                                               .filter(FileEntry::isDirectory)
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

    private ResponseEntity<?> toNothingJPG() {
        return classPathResponse(MediaType.IMAGE_JPEG, "nothing.jpg");
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
        final FileEntry entry = FileEntry.of(resourcePath.getParent(), LinkHandling.RESOLVE);
        if (entry.isDirectory()) {
            final FileEntry.Streamer streamer = FileEntry.streamer(LinkHandling.DISCLOSE);
            // relative ...
            final String target = entry.path().toUri().toString(); // absolute: locator.basePath().toUri().toString();
            final String replacement = "";                         // absolute: locator.serviceUri().toString();
            final var stage1 = streamer.stream(entry) //.parallel()
                                       .filter(FileEntry::isRegularFile)
                                       .filter(ImageType::isMatching);
            final var stage2 = (null == order) ? stage1
                                               : stage1.sorted(order);
            final var list = stage2.map(FileEntry::path)
                                   .map(Path::toUri)
                                   .map(URI::toString)
                                   .map(s -> s.replace(target, replacement))
                                   .toList();
            return jsonResponse(list);
        }
        return notFound();
    }

    private ResponseEntity<?> toImage() {
        if (Files.isRegularFile(resourcePath)) {
            return ResponseEntity.ok()
                                 .contentType(lazyImageType.get().mediaType())
                                 .body(new FileSystemResource(resourcePath));
        }
        return notFound();
    }

    @Override
    final ResponseEntity<?> response() {
        return MAPPING.apply(this);
    }
}
