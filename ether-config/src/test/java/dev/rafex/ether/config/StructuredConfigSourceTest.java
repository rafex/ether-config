package dev.rafex.ether.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Files;

import org.junit.jupiter.api.Test;

class StructuredConfigSourceTest {

	@Test
	void shouldLoadJsonYamlAndToml() throws Exception {
		final var json = Files.createTempFile("ether-config", ".json");
		Files.writeString(json, """
				{"server":{"host":"json.local","ports":[8080,8081]}}
				""");
		assertEquals("json.local", new JsonFileConfigSource(json).get("server.host").orElseThrow());
		assertEquals("8081", new JsonFileConfigSource(json).get("server.ports[1]").orElseThrow());

		final var yaml = Files.createTempFile("ether-config", ".yaml");
		Files.writeString(yaml, """
				server:
				  host: yaml.local
				  ports:
				    - 8080
				    - 8081
				""");
		assertEquals("yaml.local", new YamlFileConfigSource(yaml).get("server.host").orElseThrow());
		assertEquals("8080", new YamlFileConfigSource(yaml).get("server.ports[0]").orElseThrow());

		final var toml = Files.createTempFile("ether-config", ".toml");
		Files.writeString(toml, """
				[server]
				host = "toml.local"
				ports = [8080, 8081]
				""");
		assertEquals("toml.local", new TomlFileConfigSource(toml).get("server.host").orElseThrow());
		assertEquals("8081", new TomlFileConfigSource(toml).get("server.ports[1]").orElseThrow());
	}
}
