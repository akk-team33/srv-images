package de.team33.service.images.main;

import org.springframework.http.MediaType;

class RspShowCSS extends RspClassPath {

    RspShowCSS(final Request request) {
        super(request, "show.css", MediaType.valueOf("text/css"));
    }
}
