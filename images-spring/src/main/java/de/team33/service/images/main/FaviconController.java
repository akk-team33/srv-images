package de.team33.service.images.main;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FaviconController {

    @GetMapping("/favicon.ico")
    public ResponseEntity<ClassPathResource> favicon() {
        return ResponseEntity.ok()
                             .contentType(MediaType.IMAGE_PNG)
                             .body(new ClassPathResource("favicon.png", FaviconController.class));
    }
}
