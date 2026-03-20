package dev.rafex.ether.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;

import org.junit.jupiter.api.Test;

import dev.rafex.ether.config.sources.ReloadableConfigSource;

class ReloadableConfigSourceTest {

    @Test
    void shouldReloadPropertiesWhenFileChanges() throws Exception {
        final Path file = Files.createTempFile("ether-config", ".properties");
        Files.writeString(file, "host=before\n");

        try (ReloadableConfigSource source = ReloadableConfigSource.of(file,
                ReloadableConfigSource.Format.PROPERTIES)) {
            assertEquals("before", source.get("host").orElseThrow());
            Files.writeString(file, "host=after\n");
            awaitValue(source, "host", "after");
        }
    }

    private static void awaitValue(final ReloadableConfigSource source, final String key, final String expected)
            throws InterruptedException {
        final long deadline = System.nanoTime() + Duration.ofSeconds(3).toNanos();
        while (System.nanoTime() < deadline) {
            if (expected.equals(source.get(key).orElse(null))) {
                return;
            }
            Thread.sleep(50L);
        }
        assertEquals(expected, source.get(key).orElse(null));
    }
}
