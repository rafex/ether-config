package dev.rafex.ether.config.sources;

import java.util.Map;
import java.util.Optional;

public interface ConfigSource {

    String name();

    Optional<String> get(String key);

    default Map<String, String> entries() {
        return Map.of();
    }
}
