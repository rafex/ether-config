package dev.rafex.ether.config;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public final class ReloadableConfigSource implements ConfigSource, AutoCloseable {

	public enum Format {
		PROPERTIES,
		JSON,
		YAML,
		TOML
	}

	private final String name;
	private final Path path;
	private final Format format;
	private final AtomicReference<Map<String, String>> values;
	private final AtomicBoolean running;
	private final WatchService watchService;
	private final Thread watcherThread;

	private ReloadableConfigSource(final Path path, final Format format) throws IOException {
		this.path = Objects.requireNonNull(path, "path");
		this.format = Objects.requireNonNull(format, "format");
		this.name = "reloadable:" + format.name().toLowerCase() + ":" + path.toAbsolutePath();
		this.values = new AtomicReference<>(load(path, format));
		this.running = new AtomicBoolean(true);
		this.watchService = path.getParent().getFileSystem().newWatchService();
		path.getParent().register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY);
		this.watcherThread = Thread.ofPlatform().name("ether-config-reload-" + path.getFileName()).start(this::watchLoop);
	}

	public static ReloadableConfigSource of(final Path path, final Format format) throws IOException {
		return new ReloadableConfigSource(path, format);
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public Optional<String> get(final String key) {
		return Optional.ofNullable(values.get().get(key));
	}

	@Override
	public Map<String, String> entries() {
		return values.get();
	}

	public void reloadNow() throws IOException {
		values.set(load(path, format));
	}

	@Override
	public void close() throws IOException {
		running.set(false);
		watcherThread.interrupt();
		watchService.close();
	}

	private void watchLoop() {
		while (running.get()) {
			try {
				final WatchKey key = watchService.take();
				for (final WatchEvent<?> event : key.pollEvents()) {
					final Path changed = (Path) event.context();
					if (path.getFileName().equals(changed)) {
						try {
							reloadNow();
						} catch (IOException ignored) {
							// Keep serving the previous snapshot until a valid reload is available.
						}
					}
				}
				key.reset();
			} catch (InterruptedException ignored) {
				Thread.currentThread().interrupt();
				return;
			}
		}
	}

	private static Map<String, String> load(final Path path, final Format format) throws IOException {
		return switch (format) {
			case PROPERTIES -> new PropertiesFileConfigSource(path).entries();
			case JSON -> StructuredConfigSource.loadJson(path);
			case YAML -> StructuredConfigSource.loadYaml(path);
			case TOML -> StructuredConfigSource.loadToml(path);
		};
	}
}
