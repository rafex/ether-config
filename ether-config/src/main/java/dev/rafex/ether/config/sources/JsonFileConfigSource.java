package dev.rafex.ether.config.sources;

import java.io.IOException;
import java.nio.file.Path;

public final class JsonFileConfigSource extends StructuredConfigSource {

    public JsonFileConfigSource(final Path path) throws IOException {
        super("json-file:" + path.toAbsolutePath(), loadJson(path));
    }
}
