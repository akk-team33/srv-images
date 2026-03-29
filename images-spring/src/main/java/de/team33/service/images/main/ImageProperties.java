package de.team33.service.images.main;

import de.team33.service.images.core.Direction;
import de.team33.service.images.core.EntryOrder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;
import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "images")
public class ImageProperties {

    private List<Entry> entries;
    private EntryOrder order = EntryOrder.PATH;
    private Direction direction = Direction.ASC;

    public List<Entry> getEntries() {
        return entries;
    }

    public void setEntries(List<Entry> entries) {
        this.entries = entries;
    }

    public EntryOrder getOrder() {
        return order;
    }

    public void setOrder(final EntryOrder order) {
        this.order = order;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(final Direction direction) {
        this.direction = direction;
    }

    public record Entry(String alias, Path path) {
    }
}
