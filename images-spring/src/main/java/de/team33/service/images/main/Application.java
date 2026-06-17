package de.team33.service.images.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

@SpringBootApplication
public class Application {

    public static final String APPLICATION_YML = "application.yml";

    private static Optional<String> get(String[] args, int index) {
        return (args.length > index) ? Optional.of(args[index]) : Optional.empty();
    }

    public static void main(String[] args) throws IOException {
        if (get(args, 0).filter("config"::equals).isPresent()) {
            exportConfig();
        } else {
            SpringApplication.run(Application.class, args);
        }
    }

    @SuppressWarnings("DataFlowIssue")
    private static void exportConfig() throws IOException {
        try (final InputStream stream = Application.class.getResourceAsStream("/" + APPLICATION_YML)) {
            Files.copy(stream, Path.of("application.yml"));
        }
    }
}
