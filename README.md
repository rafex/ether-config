# ether-config

Typed configuration utilities for Ether.

## Scope

- Ordered config sources with explicit precedence
- Support for environment variables, system properties, maps and `.properties` files
- Support for JSON, YAML and TOML file sources
- Secret-backed config source abstraction
- Binding to immutable Java `record` types
- Nested records, indexed lists and keyed maps
- Prefix-based binding with dot notation namespaces
- Declarative validation annotations for records
- Dynamic file reload via `WatchService`

## Features

- `ConfigSource`: common contract for all sources
- `EtherConfig`: layered lookup with deterministic precedence
- `ConfigBinder`: typed binding for records, nested records, `List<T>` and `Map<String, T>`
- `ConfigPrefix` / `ConfigAlias`: bind records to namespaced keys and legacy aliases
- `ConfigValidator`: lightweight validation layer with `@Required`, `@NotBlank`, `@Min`, `@Max`, `@Pattern`, `@Size` and `@Valid`
- `JsonFileConfigSource`, `YamlFileConfigSource`, `TomlFileConfigSource`: structured file loaders flattened to config keys
- `ReloadableConfigSource`: live reload for file-backed configuration
- `EnvironmentConfigSource`: resolves both raw env vars and dot-notation aliases like `SERVER_PORT -> server.port`

## Maven

```xml
<dependency>
  <groupId>dev.rafex.ether.config</groupId>
  <artifactId>ether-config</artifactId>
  <version>1.0.0-SNAPSHOT</version>
</dependency>
```
