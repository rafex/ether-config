package dev.rafex.ether.config.sources;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class MapConfigSource implements ConfigSource {

    private final String name;
    private final Map<String, String> values;

    public MapConfigSource(final String name, final Map<String, String> values) {
        this.name = Objects.requireNonNull(name, "name");
        this.values = Map.copyOf(new LinkedHashMap<>(Objects.requireNonNull(values, "values")));
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public Optional<String> get(final String key) {
        return Optional.ofNullable(values.get(key));
    }

    @Override
    public Map<String, String> entries() {
        return values;
    }
}
