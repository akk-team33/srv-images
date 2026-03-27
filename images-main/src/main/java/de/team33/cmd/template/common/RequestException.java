package de.team33.cmd.template.common;

import de.team33.patterns.io.deimos.TextIO;

public class RequestException extends Exception {

    public RequestException(final String message) {
        super(message);
    }

    public static RequestException read(final Class<?> referringClass, final String resourceName) {
        return new RequestException(TextIO.read(referringClass, resourceName));
    }

    public static RequestException format(final Class<?> referringClass,
                                          final String resourceName,
                                          final Object... args) {
        return new RequestException(String.format(TextIO.read(referringClass, resourceName), args));
    }
}
