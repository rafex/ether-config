package dev.rafex.ether.config;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.dataformat.toml.TomlMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

abstract class StructuredConfigSource extends MapConfigSource {

	private static final TypeReference<Map<String, Object>> MAP_TYPE = new TypeReference<>() {
	};

	StructuredConfigSource(final String name, final Map<String, String> values) {
		super(name, values);
	}

	static Map<String, String> loadJson(final Path path) throws IOException {
		return load(path, mapper());
	}

	static Map<String, String> loadYaml(final Path path) throws IOException {
		return load(path, YAMLMapper.builder().addModule(new JavaTimeModule()).build());
	}

	static Map<String, String> loadToml(final Path path) throws IOException {
		return load(path, TomlMapper.builder().addModule(new JavaTimeModule()).build());
	}

	private static Map<String, String> load(final Path path, final ObjectMapper mapper) throws IOException {
		final Map<String, Object> tree = mapper.readValue(path.toFile(), MAP_TYPE);
		return StructuredConfigSupport.flatten(tree);
	}

	private static ObjectMapper mapper() {
		return new ObjectMapper()
				.registerModule(new JavaTimeModule())
				.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
	}
}
