package dev.rafex.ether.config.exceptions;

import java.util.List;
import java.util.stream.Collectors;

import dev.rafex.ether.config.validation.ConfigViolation;

public final class ConfigValidationException extends IllegalArgumentException {

    private static final long serialVersionUID = -1646122826327444468L;

    private final List<ConfigViolation> violations;

    public ConfigValidationException(final List<ConfigViolation> violations) {
        super(violations.stream().map(violation -> violation.path() + ": " + violation.message())
                .collect(Collectors.joining("; ")));
        this.violations = List.copyOf(violations);
    }

    public List<ConfigViolation> violations() {
        return violations;
    }
}
