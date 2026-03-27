package de.team33.service.images.main;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.HandlerMapping;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/images")
public class ImageController {

    private final Map<String, Path> aliasMap = new HashMap<>();
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "jpe");

    public ImageController(ImageProperties properties) {
        for (ImageProperties.ImageEntry entry : properties.getEntries()) {
            aliasMap.put(entry.getAlias(), entry.getPath().toAbsolutePath().normalize());
        }
    }

    @GetMapping("/{alias}/**")
    public ResponseEntity<Resource> getImage(@PathVariable("alias") String alias,
                                             HttpServletRequest request) {

        Path baseDir = aliasMap.get(alias);
        if (baseDir == null) {
            return ResponseEntity.notFound().build();
        }

        // Pfad hinter /{alias}/ ermitteln
        String pathWithinHandler = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        String bestMatchPattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        String restOfPath = new AntPathMatcher().extractPathWithinPattern(bestMatchPattern, pathWithinHandler);

        Path filePath = baseDir.resolve(restOfPath).normalize();

        // Path Traversal verhindern
        if (!filePath.startsWith(baseDir)) {
            return ResponseEntity.notFound().build();
        }

        // Nur erlaubte Endungen
        String filename = filePath.getFileName().toString().toLowerCase();
        boolean allowed = ALLOWED_EXTENSIONS.stream().anyMatch(filename::endsWith);
        if (!allowed) {
            return ResponseEntity.notFound().build();
        }

        try {
            if (!Files.exists(filePath) || !Files.isRegularFile(filePath)) {
                return ResponseEntity.notFound().build();
            }

            Resource resource = new UrlResource(filePath.toUri());
            return ResponseEntity.ok()
                                 .contentType(MediaType.IMAGE_JPEG)
                                 .body(resource);

        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
