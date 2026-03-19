package dev.rafex.ether.config;

import java.io.IOException;
import java.nio.file.Path;

public final class YamlFileConfigSource extends StructuredConfigSource {

	public YamlFileConfigSource(final Path path) throws IOException {
		super("yaml-file:" + path.toAbsolutePath(), loadYaml(path));
	}
}
