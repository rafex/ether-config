package dev.rafex.ether.config.sources;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

public final class PropertiesFileConfigSource extends MapConfigSource {

    public PropertiesFileConfigSource(final Path path) throws IOException {
        super("properties-file:" + path.toAbsolutePath(), load(path));
    }

    private static Map<String, String> load(final Path path) throws IOException {
        final var properties = new Properties();
        try (var in = Files.newInputStream(path)) {
            properties.load(in);
        }

        final var out = new LinkedHashMap<String, String>();
        for (final var key : properties.stringPropertyNames()) {
            out.put(key, properties.getProperty(key));
        }
        return out;
    }
}
