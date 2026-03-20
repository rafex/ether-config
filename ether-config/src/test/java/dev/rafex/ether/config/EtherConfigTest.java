package dev.rafex.ether.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;

import org.junit.jupiter.api.Test;

import dev.rafex.ether.config.sources.MapConfigSource;

class EtherConfigTest {

    @Test
    void shouldResolveValuesBySourcePrecedence() {
        final var config = EtherConfig.of(new MapConfigSource("env", Map.of("PORT", "8080")),
                new MapConfigSource("defaults", Map.of("PORT", "9090", "HOST", "localhost")));

        assertEquals("8080", config.require("PORT"));
        assertEquals("localhost", config.require("HOST"));
    }

    @Test
    void snapshotShouldExposeMergedValues() {
        final var config = EtherConfig.of(new MapConfigSource("first", Map.of("A", "1")),
                new MapConfigSource("second", Map.of("A", "2", "B", "3")));

        assertEquals(Map.of("A", "1", "B", "3"), config.snapshot());
    }
}
