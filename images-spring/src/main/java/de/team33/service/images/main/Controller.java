package de.team33.service.images.main;

import de.team33.patterns.io.adrastea.FileEntry;
import de.team33.patterns.io.adrastea.LinkHandling;
import de.team33.service.images.core.AliasMap;
import de.team33.service.images.core.Locator;
import jakarta.annotation.Nonnull;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.MalformedURLException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

import static java.lang.System.Logger.Level.INFO;
import static java.lang.System.Logger.Level.WARNING;
import static java.util.stream.Collectors.joining;

@RestController
@RequestMapping(Util.CONTROLLER_ROOT)
public class Controller {

    private static final System.Logger LOGGER = System.getLogger(Controller.class.getCanonicalName());

    private final AliasMap aliasMap;

    public Controller(Properties properties) {
        this.aliasMap = properties.getEntries().stream()
                                  .map(AliasMap.Entry::normalize)
                                  .collect(AliasMap::builder, AliasMap.Builder::put, AliasMap.Builder::putAll)
                                  .build();
    }

    @Nonnull
    private static ResponseEntity<?> notFound(final Object location) {
        LOGGER.log(INFO, () -> "Not Found: " + location);
        return ResponseEntity.notFound().build();
    }

    @Nonnull
    private static ResponseEntity<?> badRequest(final URI uri) {
        LOGGER.log(WARNING, () -> "Bad Request: " + uri);
        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/favicon.ico")
    public ResponseEntity<ClassPathResource> favicon() {
        return ResponseEntity.ok()
                             .contentType(MediaType.IMAGE_PNG)
                             .body(new ClassPathResource("favicon.png", Controller.class));
    }

    @GetMapping("/{alias}/**")
    public ResponseEntity<?> get(final HttpServletRequest request,
                                 @PathVariable("alias") final String alias) {
        final Request rq = new Request(aliasMap, request, alias);
        final Path resourcePath = rq.locator().resourcePath();
        if (!resourcePath.startsWith(rq.locator().basePath())) {
            return badRequest(rq.locator().requestUri());
        }
        if (resourcePath.endsWith("NO.JPG")) {
            return classPathResponse("busy.gif", MediaType.IMAGE_GIF);
        }
        if (resourcePath.endsWith("NOTHING.JPG")) {
            return classPathResponse("nothing.jpg", MediaType.IMAGE_JPEG);
        }
        final ImageType type = ImageType.of(resourcePath);
        if (null != type) {
            return imageResponse(resourcePath, type.mediaType());
        }
        if (resourcePath.endsWith("index.json")) {
            return jsonResponse(rq.order(), rq.locator());
        }
        if (resourcePath.endsWith("show.html")) {
            return classPathResponse("show.html", MediaType.TEXT_HTML);
        }
        if (resourcePath.endsWith("show.js")) {
            return classPathResponse("show.js", MediaType.valueOf("application/javascript"));
        }
        if (resourcePath.endsWith("show.css")) {
            return classPathResponse("show.css", MediaType.valueOf("text/css"));
        }
        return notFound(rq.locator().requestUri());
    }

    private ResponseEntity<?> jsonResponse(final Comparator<FileEntry> order, final Locator locator) {
        final FileEntry entry = FileEntry.of(locator.resourcePath().getParent(), LinkHandling.RESOLVE);
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
            final var json = stage2.map(FileEntry::path)
                                   .map(Path::toUri)
                                   .map(URI::toString)
                                   .map(s -> s.replace(target, replacement))
                                   .map(s -> '"' + s + '"')
                                   .collect(joining(", ", "[", "]"));
            return ResponseEntity.ok()
                                 .contentType(MediaType.APPLICATION_JSON)
                                 .body(json);
        }
        return notFound(locator.requestUri());
    }

    private ResponseEntity<?> imageResponse(final Path path, final MediaType mediaType) {
        if (Files.isRegularFile(path)) {
            try {
                return ResponseEntity.ok()
                                     .contentType(mediaType)
                                     .body(new UrlResource(path.toUri()));
            } catch (final MalformedURLException e) {
                throw new IllegalArgumentException("Illegal path: %s".formatted(path), e);
            }
        }
        return notFound(path);
    }

    private ResponseEntity<Resource> classPathResponse(final String name, final MediaType mediaType) {
        final ClassPathResource resource = new ClassPathResource(name, Controller.class);
        return ResponseEntity.ok()
                             .contentType(mediaType)
                             .body(resource);
    }
}
