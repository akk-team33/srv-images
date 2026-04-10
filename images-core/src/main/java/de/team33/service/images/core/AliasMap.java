package de.team33.service.images.core;

import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Stream;

import static java.util.Collections.unmodifiableMap;

public class AliasMap {

    private static final Path NULL = Path.of("NULL").toAbsolutePath().normalize();

    private final Map<String, Entry> backing;

    private AliasMap(final Builder builder) {
        this.backing = unmodifiableMap(new TreeMap<>(builder.backing));
    }

    public static Builder builder() {
        return new Builder();
    }

    public final Entry get(final String alias) {
        return Optional.ofNullable(backing.get(alias))
                       .orElseGet(() -> new Entry(alias, NULL, null, null));
    }

    public final Stream<Entry> stream() {
        return backing.values().stream();
    }

    public record Entry(String alias, Path path, EntryOrder order, Direction direction) {
    }

    public static class Builder {

        private final Map<String, Entry> backing = new TreeMap<>();

        public final void put(final Entry entry) {
            backing.put(entry.alias(), entry);
        }

        public final void putAll(final AliasMap.Builder other) {
            backing.putAll(other.backing);
        }

        public final AliasMap build() {
            return new AliasMap(this);
        }
    }
}
