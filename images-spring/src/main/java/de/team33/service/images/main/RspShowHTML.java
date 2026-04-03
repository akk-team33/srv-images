package de.team33.service.images.main;

import org.springframework.http.MediaType;

class RspShowHTML extends RspClassPath {

    RspShowHTML(final Request request) {
        super(request, "show.html", MediaType.TEXT_HTML);
    }
}
