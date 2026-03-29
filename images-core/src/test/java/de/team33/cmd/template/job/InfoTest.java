package de.team33.cmd.template.job;

import de.team33.cmd.template.common.RequestException;
import de.team33.cmd.template.testing.Buffer;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class InfoTest {

    @Test
    void run() throws RequestException {
        final Buffer buffer = new Buffer();
        final Info job = Info.job(buffer, List.of(getClass().getSimpleName(), "info", "."));

        job.run();

        final String result = buffer.toString();
        // System.out.println(result);

        assertTrue(result.contains("Path ....... : "));
        assertTrue(result.contains("type ....... : DIRECTORY"));
        assertTrue(result.contains("size ....... : "));
        assertTrue(result.contains("creation ... : "));
        assertTrue(result.contains("lastAccess . : "));
        assertTrue(result.contains("lastModified : "));
        assertTrue(result.contains("fileKey .... : "));
    }

    @Test
    void run_missing() throws RequestException {
        final Buffer buffer = new Buffer();
        final Info job = Info.job(buffer, List.of(getClass().getSimpleName(), "info", "missing.file"));

        job.run();

        final String result = buffer.toString();
        // System.out.println(result);

        assertTrue(result.contains("Path ....... : "));
        assertTrue(result.contains("missing.file"));
        assertTrue(result.contains("type ....... : MISSING"));
        assertTrue(result.contains("size ....... : 0"));
        assertTrue(result.contains("creation ... : null"));
        assertTrue(result.contains("lastAccess . : null"));
        assertTrue(result.contains("lastModified : null"));
        assertTrue(result.contains("fileKey .... : null"));
    }
}