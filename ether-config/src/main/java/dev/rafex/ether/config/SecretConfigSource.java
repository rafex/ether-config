package dev.rafex.ether.config;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public final class SecretConfigSource implements ConfigSource {

	private final String name;
	private final SecretProvider secretProvider;
	private final Map<String, String> keyToSecretName;

	public SecretConfigSource(final String name, final SecretProvider secretProvider,
			final Map<String, String> keyToSecretName) {
		this.name = Objects.requireNonNull(name, "name");
		this.secretProvider = Objects.requireNonNull(secretProvider, "secretProvider");
		this.keyToSecretName = Map.copyOf(Objects.requireNonNull(keyToSecretName, "keyToSecretName"));
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public Optional<String> get(final String key) {
		final var secretName = keyToSecretName.get(key);
		if (secretName == null || secretName.isBlank()) {
			return Optional.empty();
		}
		return secretProvider.resolve(secretName);
	}
}
