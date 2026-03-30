package de.team33.service.images.main;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AboutController {

    @GetMapping("/about")
    public ResponseEntity<ClassPathResource> about() {
        return ResponseEntity.ok()
                             .contentType(MediaType.TEXT_HTML)
                             .body(new ClassPathResource("about.html", AboutController.class));
    }
}
