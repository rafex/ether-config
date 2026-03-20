package dev.rafex.ether.config.sources;

import java.io.IOException;
import java.nio.file.Path;

public final class TomlFileConfigSource extends StructuredConfigSource {

    public TomlFileConfigSource(final Path path) throws IOException {
        super("toml-file:" + path.toAbsolutePath(), loadToml(path));
    }
}
