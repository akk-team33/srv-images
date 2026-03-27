package de.team33.cmd.template.common;

import de.team33.patterns.io.deimos.TextIO;

@FunctionalInterface
public interface Output {

    @SuppressWarnings("Convert2MethodRef")
    Output SYSTEM = (format, args) -> System.out.printf(format, args);

    void printf(String format, Object... args);

    default Output printLines(final String text) {
        text.lines()
            .forEach(line -> printf("%s%n", line));
        printf("%n");
        return this;
    }

    default Output printHead(final String cmdName) {
        printf("%s%n", TextIO.read(Output.class, "Head.txt").formatted(cmdName));
        return this;
    }

    default Output printHelp(final String cmdName, final String text) {
        return printHead(cmdName).printLines(text);
    }
}
