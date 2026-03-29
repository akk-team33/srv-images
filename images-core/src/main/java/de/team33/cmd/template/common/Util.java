package de.team33.cmd.template.common;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public final class Util {

    private Util() {
    }

    public static String cmdLine(final List<String> args) {
        return String.join(" ", args);
    }

    public static String cmdName(final List<String> args) {
        final Optional<Path> cmdPath = args.stream().findFirst().map(Path::of);
        return cmdPath.filter(Path::isAbsolute)
                      .map(Path::getFileName)
                      .map(Path::toString)
                      .orElseGet(() -> cmdPath.map(Path::toString)
                                              .orElse("[n/a]"));
    }
}
