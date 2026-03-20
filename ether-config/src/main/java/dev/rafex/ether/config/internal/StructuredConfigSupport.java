package dev.rafex.ether.config.internal;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class StructuredConfigSupport {

    private StructuredConfigSupport() {
    }

    public static Map<String, String> flatten(final Map<String, Object> input) {
        final Map<String, String> output = new LinkedHashMap<>();
        flattenInto(output, "", input);
        return Map.copyOf(output);
    }

    @SuppressWarnings("unchecked")
    private static void flattenInto(final Map<String, String> output, final String prefix, final Object value) {
        switch (value) {
        case null -> {
            return;
        }
        case final Map<?, ?> map -> {
            for (final var entry : map.entrySet()) {
                final var key = prefix.isBlank() ? String.valueOf(entry.getKey()) : prefix + "." + entry.getKey();
                flattenInto(output, key, entry.getValue());
            }
            return;
        }
        case final List<?> list -> {
            for (var i = 0; i < list.size(); i++) {
                flattenInto(output, prefix + "[" + i + "]", list.get(i));
            }
            return;
        }
        case final Enum<?> enumValue -> {
            output.put(prefix, enumValue.name());
            return;
        }
        default -> {
        }
        }
        output.put(prefix, String.valueOf(value));
    }
}
