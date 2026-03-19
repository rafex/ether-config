package dev.rafex.ether.config;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

final class StructuredConfigSupport {

	private StructuredConfigSupport() {
	}

	static Map<String, String> flatten(final Map<String, Object> input) {
		final Map<String, String> output = new LinkedHashMap<>();
		flattenInto(output, "", input);
		return Map.copyOf(output);
	}

	@SuppressWarnings("unchecked")
	private static void flattenInto(final Map<String, String> output, final String prefix, final Object value) {
		if (value == null) {
			return;
		}
		if (value instanceof Map<?, ?> map) {
			for (final var entry : map.entrySet()) {
				final String key = prefix.isBlank() ? String.valueOf(entry.getKey()) : prefix + "." + entry.getKey();
				flattenInto(output, key, entry.getValue());
			}
			return;
		}
		if (value instanceof List<?> list) {
			for (int i = 0; i < list.size(); i++) {
				flattenInto(output, prefix + "[" + i + "]", list.get(i));
			}
			return;
		}
		if (value instanceof Enum<?> enumValue) {
			output.put(prefix, enumValue.name());
			return;
		}
		output.put(prefix, String.valueOf(value));
	}
}
