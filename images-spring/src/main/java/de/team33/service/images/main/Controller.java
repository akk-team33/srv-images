package de.team33.service.images.main;

import de.team33.patterns.decision.thyone.Choices;
import de.team33.service.images.core.AliasMap;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.function.Function;

@RestController
@RequestMapping(Util.CONTROLLER_ROOT)
public class Controller {

    private static final Function<Request, Responder> RESPONDER_FUNCTION =
            Choices.serial(Request::isInconsistent,
                           Request::isShowHTML,
                           Request::isShowJS,
                           Request::isShowCSS,
                           Request::isShowJson,
                           Request::isImage)
                   .applying(RspBadRequest::new,
                             RspShowHTML::new,
                             RspShowJS::new,
                             RspShowCSS::new,
                             RspShowJson::new,
                             RspImage::new,
                             RspNotFound::new);

    private final AliasMap aliasMap;

    public Controller(Properties properties) {
        this.aliasMap = properties.getEntries().stream()
                                  .map(AliasMap.Entry::normalize)
                                  .collect(AliasMap::builder, AliasMap.Builder::put, AliasMap.Builder::putAll)
                                  .build();
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
        return RESPONDER_FUNCTION.apply(new Request(aliasMap, request, alias))
                                 .response();
    }

    private ResponseEntity<Resource> classPathResponse(final String name, final MediaType mediaType) {
        final ClassPathResource resource = new ClassPathResource(name, Controller.class);
        return ResponseEntity.ok()
                             .contentType(mediaType)
                             .body(resource);
    }
}
