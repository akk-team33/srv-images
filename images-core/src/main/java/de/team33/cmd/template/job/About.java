package de.team33.cmd.template.job;

import de.team33.cmd.template.common.Output;
import de.team33.cmd.template.common.Util;
import de.team33.patterns.io.deimos.TextIO;

import java.util.List;

class About {

    static final String EXCERPT = "Get basic info about this application.";

    static Runnable job(final Output out, final List<String> args) {
        return () -> out.printLines(TextIO.read(About.class, "About.txt")
                                          .formatted(Util.cmdLine(args)));
    }
}
