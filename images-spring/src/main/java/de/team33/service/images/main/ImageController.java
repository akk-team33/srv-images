package de.team33.service.images.main;

import de.team33.patterns.io.adrastea.FileEntry;
import de.team33.patterns.io.adrastea.LinkHandling;
import de.team33.service.images.core.AliasMap;
import de.team33.service.images.core.Locator;
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
import java.util.Set;

import static java.util.stream.Collectors.joining;

@RestController
@RequestMapping(Util.IMAGE_CONTROLLER_ROOT)
public class ImageController {

    private static final Set<String> IMAGE_EXTENSIONS = Set.of(".jpg", ".jpeg", ".jpe");

    private final AliasMap aliasMap;

    public ImageController(ImageProperties properties) {
        this.aliasMap = properties.getEntries().stream()
                                  .map(ImageController::toEntry)
                                  .collect(AliasMap::builder, AliasMap.Builder::put, AliasMap.Builder::putAll)
                                  .build();
    }

    private static AliasMap.Entry toEntry(final ImageProperties.ImageEntry imageEntry) {
        return new AliasMap.Entry(imageEntry.getAlias(),
                                  imageEntry.getPath().toAbsolutePath().normalize());
    }

    private static boolean isImage(final String name) {
        final String lowerCase = name.toLowerCase();
        return IMAGE_EXTENSIONS.stream().anyMatch(lowerCase::endsWith);
    }

    private static boolean isImage(final Path path) {
        return isImage(path.getFileName().toString());
    }

    @GetMapping("/{alias}/**")
    public ResponseEntity<?> get(final HttpServletRequest request,
                                 @PathVariable("alias") final String alias) {
        final Locator locator = Locator.by(Util.IMAGE_CONTROLLER_ROOT, aliasMap.get(alias))
                                       .setResourceUri(request.getRequestURI())
                                       .setRequestUrl(request.getRequestURL().toString())
                                       .build();
        final Path resourcePath = locator.resourcePath();
        if (!resourcePath.startsWith(locator.basePath())) {
            return ResponseEntity.badRequest().build();
        }
        if (isImage(resourcePath)) {
            return imageResponse(resourcePath);
        }
        if (resourcePath.endsWith("index.json")) {
            return jsonResponse(locator);
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
        return ResponseEntity.notFound().build();
    }

    private ResponseEntity<String> jsonResponse(final Locator locator) {
        final FileEntry entry = FileEntry.of(locator.resourcePath().getParent(), LinkHandling.RESOLVE);
        if (entry.isDirectory()) {
            final FileEntry.Streamer streamer = FileEntry.streamer(LinkHandling.DISCLOSE);
            final String target = locator.basePath().toUri().toString();
            final String replacement = locator.serviceUri().toString();
            final String json = streamer.stream(entry)
                                        .filter(FileEntry::isRegularFile)
                                        .map(FileEntry::path)
                                        .filter(ImageController::isImage)
                                        .map(Path::toUri)
                                        .map(URI::toString)
                                        .map(s -> s.replace(target, replacement))
                                        .map(s -> '"' + s + '"')
                                        .collect(joining(", ", "[", "]"));
            return ResponseEntity.ok()
                                 .contentType(MediaType.APPLICATION_JSON)
                                 .body(json);
        }
        return ResponseEntity.notFound().build();
    }

    private ResponseEntity<Resource> imageResponse(final Path path) {
        if (Files.isRegularFile(path)) {
            try {
                return ResponseEntity.ok()
                                     .contentType(MediaType.IMAGE_JPEG)
                                     .body(new UrlResource(path.toUri()));
            } catch (final MalformedURLException e) {
                throw new IllegalArgumentException("Illegal path: %s".formatted(path), e);
            }
        }
        return ResponseEntity.notFound().build();
    }

    private ResponseEntity<Resource> classPathResponse(final String name, final MediaType mediaType) {
        final ClassPathResource resource = new ClassPathResource(name, ImageController.class);
        return ResponseEntity.ok()
                             .contentType(mediaType)
                             .body(resource);
    }
}
