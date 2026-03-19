package dev.rafex.ether.config;

import java.util.Optional;

@FunctionalInterface
public interface SecretProvider {

	Optional<String> resolve(String name);
}
