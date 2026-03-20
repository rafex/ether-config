package dev.rafex.ether.config.sources;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public final class EnvironmentConfigSource extends MapConfigSource {

    public EnvironmentConfigSource() {
        this(System.getenv());
    }

    public EnvironmentConfigSource(final Map<String, String> env) {
        super("environment", normalize(env));
    }

    private static Map<String, String> normalize(final Map<String, String> env) {
        final var normalized = new LinkedHashMap<String, String>();
        for (final var entry : env.entrySet()) {
            normalized.put(entry.getKey(), entry.getValue());
            normalized.putIfAbsent(toDotKey(entry.getKey()), entry.getValue());
        }
        return normalized;
    }

    private static String toDotKey(final String key) {
        return key.toLowerCase(Locale.ROOT).replace('_', '.');
    }
}
