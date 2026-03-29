package de.team33.service.images.main;

import de.team33.patterns.io.adrastea.FileEntry;
import de.team33.patterns.io.adrastea.LinkHandling;
import jakarta.servlet.http.HttpServletRequest;
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
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/")
public class ImageController {

    private static final Set<String> IMAGE_EXTENSIONS = Set.of(".jpg", ".jpeg", ".jpe");
    private final Map<String, Path> aliasMap = new HashMap<>();

    public ImageController(ImageProperties properties) {
        for (ImageProperties.ImageEntry entry : properties.getEntries()) {
            aliasMap.put(entry.getAlias(), entry.getPath().toAbsolutePath().normalize());
        }
    }

    private static Path noBasePath() {
        return FileSystems.getDefault().getRootDirectories().iterator().next().resolve("_NIRVANA_");
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

        final Path basePath = Optional.ofNullable(aliasMap.get(alias))
                                      .orElseGet(ImageController::noBasePath);
        final String resourceUri = request.getRequestURI().substring(alias.length() + 2);
        final Path resourcePath = basePath.resolve(resourceUri)
                                          .toAbsolutePath()
                                          .normalize();
        if (!resourcePath.startsWith(basePath)) {
            return ResponseEntity.badRequest().build();
        }
        if (isImage(resourceUri)) {
            return imageResponse(resourcePath);
        }
        if (resourcePath.endsWith("index.json")) {
            return jsonResponse(resourcePath.getParent(), basePath,
                                request.getRequestURL().toString().replace(resourceUri, ""));
        }
        return ResponseEntity.notFound().build();
    }

    private ResponseEntity<String> jsonResponse(final Path path,
                                                final Path basePath,
                                                final String baseUrl) {
        final FileEntry entry = FileEntry.resolved(path);
        if (entry.isDirectory()) {
            final String baseUri = basePath.toUri().toString();
            final String json = FileEntry.streamer(LinkHandling.DISCLOSE)
                                         .stream(entry)
                                         .filter(FileEntry::isRegularFile)
                                         .map(FileEntry::path)
                                         .filter(ImageController::isImage)
                                         //.map(p -> entry.path().relativize(p))
                                         //.map(Path::toString)
                                         .map(Path::toUri)
                                         .map(URI::toString)
                                         .map(s -> s.replace(baseUri, baseUrl))
                                         .map(s -> '"' + s + '"')
                                         .collect(Collectors.joining(", ", "[", "]"));
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
}
