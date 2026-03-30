package de.team33.service.images.main;

import de.team33.service.images.core.AliasMap.Entry;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@SuppressWarnings("unused")
@Configuration
@ConfigurationProperties(prefix = "images")
public class ImageProperties {

    private List<Entry> entries;

    public List<Entry> getEntries() {
        return entries;
    }

    public void setEntries(List<Entry> entries) {
        this.entries = entries;
    }
}
