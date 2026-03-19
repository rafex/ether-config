package dev.rafex.ether.config;

import java.lang.reflect.RecordComponent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.PatternSyntaxException;

import dev.rafex.ether.config.validation.Max;
import dev.rafex.ether.config.validation.Min;
import dev.rafex.ether.config.validation.NotBlank;
import dev.rafex.ether.config.validation.Pattern;
import dev.rafex.ether.config.validation.Required;
import dev.rafex.ether.config.validation.Size;
import dev.rafex.ether.config.validation.Valid;

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

	private static void validateInstance(final Object instance, final String path, final List<ConfigViolation> violations) {
		if (instance == null || !instance.getClass().isRecord()) {
			return;
		}

		for (final RecordComponent component : instance.getClass().getRecordComponents()) {
			final String componentPath = path.isBlank() ? component.getName() : path + "." + component.getName();
			final Object value;
			try {
				value = component.getAccessor().invoke(instance);
			} catch (ReflectiveOperationException e) {
				throw new IllegalStateException("Unable to validate component " + componentPath, e);
			}

			if (component.isAnnotationPresent(Required.class) && value == null) {
				violations.add(new ConfigViolation(componentPath, "is required"));
				continue;
			}
			if (value == null) {
				continue;
			}

			final NotBlank notBlank = component.getAnnotation(NotBlank.class);
			if (notBlank != null && value instanceof String stringValue && stringValue.isBlank()) {
				violations.add(new ConfigViolation(componentPath, "must not be blank"));
			}

			final Min min = component.getAnnotation(Min.class);
			if (min != null && value instanceof Number number && number.longValue() < min.value()) {
				violations.add(new ConfigViolation(componentPath, "must be >= " + min.value()));
			}

			final Max max = component.getAnnotation(Max.class);
			if (max != null && value instanceof Number number && number.longValue() > max.value()) {
				violations.add(new ConfigViolation(componentPath, "must be <= " + max.value()));
			}

			final Pattern pattern = component.getAnnotation(Pattern.class);
			if (pattern != null && value instanceof String stringValue) {
				try {
					if (!java.util.regex.Pattern.compile(pattern.value()).matcher(stringValue).matches()) {
						violations.add(new ConfigViolation(componentPath, "must match " + pattern.value()));
					}
				} catch (PatternSyntaxException e) {
					throw new IllegalArgumentException("Invalid regex on " + componentPath + ": " + pattern.value(), e);
				}
			}

			final Size size = component.getAnnotation(Size.class);
			if (size != null) {
				final int actualSize = sizeOf(value);
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
		if (value instanceof Collection<?> collection) {
			int index = 0;
			for (final Object element : collection) {
				validateNested(element, path + "[" + index + "]", violations);
				index++;
			}
			return;
		}
		if (value instanceof Map<?, ?> map) {
			for (final var entry : map.entrySet()) {
				validateNested(entry.getValue(), path + "." + entry.getKey(), violations);
			}
		}
	}

	private static int sizeOf(final Object value) {
		if (value instanceof String stringValue) {
			return stringValue.length();
		}
		if (value instanceof Collection<?> collection) {
			return collection.size();
		}
		if (value instanceof Map<?, ?> map) {
			return map.size();
		}
		return -1;
	}
}
