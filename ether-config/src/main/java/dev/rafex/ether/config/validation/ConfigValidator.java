package dev.rafex.ether.config.validation;

import java.lang.reflect.RecordComponent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.PatternSyntaxException;

import dev.rafex.ether.config.exceptions.ConfigValidationException;

public final class ConfigValidator {

    private ConfigValidator() {
    }

    public static void validate(final Object instance) {
        Objects.requireNonNull(instance, "instance");
        final List<ConfigViolation> violations = new ArrayList<>();
        validateInstance(instance, "", violations);
        if (!violations.isEmpty()) {
            throw new ConfigValidationException(violations);
        }
    }

    private static void validateInstance(final Object instance, final String path,
            final List<ConfigViolation> violations) {
        if (instance == null || !instance.getClass().isRecord()) {
            return;
        }

        for (final RecordComponent component : instance.getClass().getRecordComponents()) {
            final var componentPath = path.isBlank() ? component.getName() : path + "." + component.getName();
            final Object value;
            try {
                final var accessor = component.getAccessor();
                accessor.setAccessible(true);
                value = accessor.invoke(instance);
            } catch (final ReflectiveOperationException e) {
                throw new IllegalStateException("Unable to validate component " + componentPath, e);
            }

            if (component.isAnnotationPresent(Required.class) && value == null) {
                violations.add(new ConfigViolation(componentPath, "is required"));
                continue;
            }
            if (value == null) {
                continue;
            }

            final var notBlank = component.getAnnotation(NotBlank.class);
            if (notBlank != null && value instanceof final String stringValue && stringValue.isBlank()) {
                violations.add(new ConfigViolation(componentPath, "must not be blank"));
            }

            final var min = component.getAnnotation(Min.class);
            if (min != null && value instanceof final Number number && number.longValue() < min.value()) {
                violations.add(new ConfigViolation(componentPath, "must be >= " + min.value()));
            }

            final var max = component.getAnnotation(Max.class);
            if (max != null && value instanceof final Number number && number.longValue() > max.value()) {
                violations.add(new ConfigViolation(componentPath, "must be <= " + max.value()));
            }

            final var pattern = component.getAnnotation(Pattern.class);
            if (pattern != null && value instanceof final String stringValue) {
                try {
                    if (!java.util.regex.Pattern.compile(pattern.value()).matcher(stringValue).matches()) {
                        violations.add(new ConfigViolation(componentPath, "must match " + pattern.value()));
                    }
                } catch (final PatternSyntaxException e) {
                    throw new IllegalArgumentException("Invalid regex on " + componentPath + ": " + pattern.value(), e);
                }
            }

            final var size = component.getAnnotation(Size.class);
            if (size != null) {
                final var actualSize = sizeOf(value);
                if (actualSize >= 0 && (actualSize < size.min() || actualSize > size.max())) {
                    violations.add(new ConfigViolation(componentPath,
                            "size must be between " + size.min() + " and " + size.max()));
                }
            }

            if (component.isAnnotationPresent(Valid.class)) {
                validateNested(value, componentPath, violations);
            }
        }
    }

    private static void validateNested(final Object value, final String path, final List<ConfigViolation> violations) {
        if (value == null) {
            return;
        }
        if (value.getClass().isRecord()) {
            validateInstance(value, path, violations);
            return;
        }
        if (value instanceof final Collection<?> collection) {
            var index = 0;
            for (final Object element : collection) {
                validateNested(element, path + "[" + index + "]", violations);
                index++;
            }
            return;
        }
        if (value instanceof final Map<?, ?> map) {
            for (final var entry : map.entrySet()) {
                validateNested(entry.getValue(), path + "." + entry.getKey(), violations);
            }
        }
    }

    private static int sizeOf(final Object value) {
        return switch (value) {
        case final String stringValue -> stringValue.length();
        case final Collection<?> collection -> collection.size();
        case final Map<?, ?> map -> map.size();
        case null, default -> -1;
        };
    }
}
