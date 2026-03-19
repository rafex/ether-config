package dev.rafex.ether.config;

import java.util.List;
import java.util.stream.Collectors;

public final class ConfigValidationException extends IllegalArgumentException {

	private final List<ConfigViolation> violations;

	public ConfigValidationException(final List<ConfigViolation> violations) {
		super(violations.stream()
				.map(violation -> violation.path() + ": " + violation.message())
				.collect(Collectors.joining("; ")));
		this.violations = List.copyOf(violations);
	}

	public List<ConfigViolation> violations() {
		return violations;
	}
}
