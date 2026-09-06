package de.team33.service.images;

public record Config(Short port, Entry[] entries) {

    public record Entry() {}
}
