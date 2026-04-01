package de.team33.service.images.main;

import de.team33.patterns.decision.thyone.Choices;
import de.team33.service.images.core.AliasMap;
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

import static java.lang.System.Logger.Level.INFO;
import static java.lang.System.Logger.Level.WARNING;

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
    public ResponseEntity<?> favicon() {
        return classPathResponse("favicon.png", MediaType.IMAGE_PNG);
    }

    @GetMapping("/**/NO.JPG")
    public ResponseEntity<?> getNoJPG() {
        return classPathResponse("busy.gif", MediaType.IMAGE_GIF);
    }

    @GetMapping("/**/NOTHING.JPG")
    public ResponseEntity<?> getNothingJPG() {
        return classPathResponse("nothing.jpg", MediaType.IMAGE_JPEG);
    }

    @GetMapping("/{alias}/**")
    public ResponseEntity<?> get(final HttpServletRequest request,
                                 @PathVariable("alias") final String alias) {
        final var choices = Choices.serial(Request::isInconsistent,
                                           Request::isShowHTML,
                                           Request::isShowJS,
                                           Request::isShowCSS,
                                           Request::isShowJson,
                                           Request::isImage)
                                   .applying(rq -> badRequest(rq.locator().requestUri()),
                                             rq -> classPathResponse("show.html",
                                                                     MediaType.TEXT_HTML),
                                             rq -> classPathResponse("show.js",
                                                                     MediaType.valueOf("application/javascript")),
                                             rq -> classPathResponse("show.css",
                                                                     MediaType.valueOf("text/css")),
                                             rq -> new RspShowJson(rq).response(),
                                             rq -> new RspImage(rq).response(),
                                             rq -> notFound(rq.locator().requestUri()));
        final var rq = new Request(aliasMap, request, alias);
        return choices.apply(rq);
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
