package de.team33.service.images.main;

import de.team33.patterns.io.adrastea.FileEntry;
import de.team33.patterns.io.adrastea.LinkHandling;
import de.team33.service.images.core.Locator;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.nio.file.Path;
import java.util.Comparator;

import static java.util.stream.Collectors.joining;

class RspShowJson extends Responder {

    private final Locator locator;
    private final Comparator<FileEntry> order;

    RspShowJson(final Request request) {
        super(request);
        locator = request.locator();
        order = request.order();
    }

    @Override
    ResponseEntity<?> response() {
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
        return notFound();
    }
}
