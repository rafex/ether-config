package dev.rafex.ether.config;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

public final class SystemPropertyConfigSource extends MapConfigSource {

	public SystemPropertyConfigSource() {
		this(System.getProperties());
	}

	public SystemPropertyConfigSource(final Properties properties) {
		super("system-properties", toMap(properties));
	}

	private static Map<String, String> toMap(final Properties properties) {
		final var out = new LinkedHashMap<String, String>();
		for (final var key : properties.stringPropertyNames()) {
			out.put(key, properties.getProperty(key));
		}
		return out;
	}
}
