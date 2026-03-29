package de.team33.service.images.core;

import de.team33.patterns.io.adrastea.FileEntry;

import java.nio.file.Path;
import java.util.Comparator;

import static java.util.Comparator.comparing;

public enum EntryOrder implements Comparator<FileEntry> {

    PATH(comparing(FileEntry::path, Statics.PATH_ORDER)),
    NAME(comparing(FileEntry::name, Statics.STRING_ORDER).thenComparing(PATH)),
    UPDATE(comparing(FileEntry::lastModified).thenComparing(PATH));

    private final Comparator<FileEntry> backing;

    EntryOrder(final Comparator<FileEntry> backing) {
        this.backing = backing;
    }

    @Override
    public int compare(final FileEntry left, final FileEntry right) {
        return backing.compare(left, right);
    }

    private static class Statics {

        private static final Comparator<String> IGNORE_CASE = String::compareToIgnoreCase;
        private static final Comparator<String> RESPECT_CASE = String::compareTo;
        private static final Comparator<String> STRING_ORDER = IGNORE_CASE.thenComparing(RESPECT_CASE);
        private static final Comparator<Path> PATH_ORDER = comparing(Path::toString, STRING_ORDER);
    }
}
