package de.team33.cmd.template;

import de.team33.cmd.template.common.Output;
import de.team33.cmd.template.common.RequestException;
import de.team33.cmd.template.job.Command;

import java.util.List;

import static de.team33.cmd.template.common.Util.cmdName;

public class Main {

    public static void main(final String... args) {
        job(Output.SYSTEM, List.of(args)).run();
    }

    @SuppressWarnings("SameParameterValue")
    private static Runnable job(final Output out, final List<String> args) {
        try {
            return Command.job(out, args);
        } catch (final RequestException e) {
            return () -> out.printHelp(cmdName(args), e.getMessage());
        }
    }
}
