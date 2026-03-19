package dev.rafex.ether.config;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public final class EtherConfig {

	private final List<ConfigSource> sources;

	private EtherConfig(final List<ConfigSource> sources) {
		this.sources = List.copyOf(sources);
	}

	public static EtherConfig of(final ConfigSource... sources) {
		return of(List.of(sources));
	}

	public static EtherConfig of(final List<ConfigSource> sources) {
		Objects.requireNonNull(sources, "sources");
		return new EtherConfig(new ArrayList<>(sources));
	}

	public List<ConfigSource> sources() {
		return sources;
	}

	public Optional<String> get(final String key) {
		for (final var source : sources) {
			final var value = source.get(key);
			if (value.isPresent()) {
				return value;
			}
		}
		return Optional.empty();
	}

	public String require(final String key) {
		return get(key).orElseThrow(() -> new IllegalArgumentException("Missing config key: " + key));
	}

	public Map<String, String> snapshot() {
		final var merged = new LinkedHashMap<String, String>();
		for (final var source : sources) {
			for (final var entry : source.entries().entrySet()) {
				merged.putIfAbsent(entry.getKey(), entry.getValue());
			}
		}
		return Map.copyOf(merged);
	}

	public <T extends Record> T bind(final Class<T> recordType) {
		return ConfigBinder.bind(this, recordType);
	}

	public <T extends Record> T bind(final String prefix, final Class<T> recordType) {
		return ConfigBinder.bind(this, prefix, recordType);
	}

	public <T extends Record> T bindValidated(final Class<T> recordType) {
		return ConfigBinder.bindValidated(this, recordType);
	}

	public <T extends Record> T bindValidated(final String prefix, final Class<T> recordType) {
		return ConfigBinder.bindValidated(this, prefix, recordType);
	}
}
