package de.team33.cmd.template.testing;

import de.team33.cmd.template.common.Output;

public class Buffer implements Output {

    private final StringBuffer backing = new StringBuffer();

    @Override
    public final void printf(final String format, final Object... args) {
        backing.append(format.formatted(args));
    }

    @Override
    public final String toString() {
        return backing.toString();
    }
}
