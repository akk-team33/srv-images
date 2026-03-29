package de.team33.service.images.core;

import java.util.Comparator;
import java.util.function.Function;

public enum Direction {

    ASC(Function.identity()),
    DESC(Comparator::reversed);

    @SuppressWarnings("rawtypes")
    private final Function mapping;

    <T> Direction(final Function<Comparator<T>, Comparator<T>> mapping) {
        this.mapping = mapping;
    }

    @SuppressWarnings("unchecked")
    public final <T> Comparator<T> map(final Comparator<T> comparator) {
        return (Comparator<T>) mapping.apply(comparator);
    }
}
