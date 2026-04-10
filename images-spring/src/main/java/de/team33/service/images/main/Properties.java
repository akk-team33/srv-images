package de.team33.service.images.main;

import de.team33.service.images.core.AliasMap;
import de.team33.service.images.core.Direction;
import de.team33.service.images.core.EntryOrder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;
import java.util.List;

@SuppressWarnings("unused")
@Configuration
@ConfigurationProperties(prefix = "images")
public class Properties {

    private List<Entry> entries;

    public List<Entry> getEntries() {
        return entries;
    }

    public void setEntries(List<Entry> entries) {
        this.entries = entries;
    }

    public record Entry(String alias, String path, EntryOrder order, Direction direction) {

        public AliasMap.Entry toAliasMapEntry() {
            return new AliasMap.Entry(alias, Path.of(path).toAbsolutePath().normalize(), order, direction);
        }
    }
}
