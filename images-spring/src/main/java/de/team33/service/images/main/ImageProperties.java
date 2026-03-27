package de.team33.service.images.main;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;
import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "images")
public class ImageProperties {

    private List<ImageEntry> entries;

    public List<ImageEntry> getEntries() {
        return entries;
    }

    public void setEntries(List<ImageEntry> entries) {
        this.entries = entries;
    }

    public static class ImageEntry {
        private String alias;
        private Path path;

        public String getAlias() {
            return alias;
        }

        public void setAlias(String alias) {
            this.alias = alias;
        }

        public Path getPath() {
            return path;
        }

        public void setPath(Path path) {
            this.path = path;
        }
    }
}
