# WebRobot CLI SDK

Public interfaces for extending the [WebRobot CLI](https://github.com/WebRobot-Ltd/webrobot-cli) with partner-authored subcommands.

Plugins are JAR files dropped into `~/.webrobot/plugins/` (or installed via `webrobot cli plugins install`). The CLI loads them at startup via `ServiceLoader` and registers their picocli commands as top-level subcommands.

---

## Maven dependency (via JitPack — no auth)

```xml
<repositories>
  <repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
  </repository>
</repositories>

<dependencies>
  <dependency>
    <groupId>com.github.WebRobot-Ltd</groupId>
    <artifactId>webrobot-cli-sdk</artifactId>
    <version>v0.2.0</version>
    <scope>provided</scope>
  </dependency>

  <!-- The CLI host provides Jackson + SLF4J at runtime; these are compile-time only -->
  <dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
    <version>2.15.2</version>
    <scope>provided</scope>
  </dependency>
  <dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-api</artifactId>
    <version>2.0.9</version>
    <scope>provided</scope>
  </dependency>

  <!-- For your @Command classes -->
  <dependency>
    <groupId>info.picocli</groupId>
    <artifactId>picocli</artifactId>
    <version>4.7.5</version>
    <scope>provided</scope>
  </dependency>
</dependencies>
```

---

## Quick start (v0.2)

### 1. Implement `WebroCliPlugin`

```java
package com.acme.cli;

import eu.webrobot.cli.sdk.*;
import java.util.List;

public class AcmeCliPlugin implements WebroCliPlugin {

    public String pluginId()         { return "acme"; }
    public String description()      { return "Acme partner CLI"; }
    public List<Class<?>> commands() { return List.of(DeployCommand.class, VerifyCommand.class); }

    @Override
    public void init(WebroCliContext ctx) {
        // Stash the context so command classes can use it at execution time
        DeployCommand.context = ctx;
        VerifyCommand.context = ctx;
    }
}
```

### 2. Write your picocli commands

```java
package com.acme.cli;

import com.fasterxml.jackson.databind.JsonNode;
import eu.webrobot.cli.sdk.WebroCliContext;
import picocli.CommandLine.*;
import java.util.Map;

@Command(name = "acme", subcommands = { DeployCommand.class, VerifyCommand.class })
public class AcmeRoot implements Runnable {
    public void run() { CommandLine.usage(this, System.out); }
}

@Command(name = "deploy", description = "Deploy an Acme bundle")
class DeployCommand implements Runnable {
    static WebroCliContext context;

    @Parameters(paramLabel = "BUNDLE", description = "path to bundle.zip")
    String bundle;

    @Option(names = {"-e", "--env"}, defaultValue = "dev")
    String env;

    public void run() {
        JsonNode resp = context.api().post(
            "/webrobot/api/acme/deploy",
            Map.of("bundle", bundle, "env", env));
        context.output().table(resp);
    }
}
```

### 3. Register the plugin via ServiceLoader

`src/main/resources/META-INF/services/eu.webrobot.cli.sdk.WebroCliPlugin`:

```
com.acme.cli.AcmeCliPlugin
```

### 4. Build and install

```bash
mvn package
webrobot cli plugins install ./target/acme-cli-1.0.0.jar
webrobot acme deploy bundle.zip --env prod
```

---

## API surface (v0.2.0)

| Type | Purpose |
|------|---------|
| `WebroCliPlugin` | Plugin entry point (interface implemented by partner) |
| `WebroCliContext` | Runtime services bundle, passed via `init()` |
| `WebroApiClient` | Generic HTTP client (`get`/`post`/`put`/`delete`/`patch`/`stream`) |
| `WebroApiException` | + `Auth`, `Forbidden`, `NotFound`, `Validation`, `Server` subclasses |
| `OutputFormatter` | Table / JSON / YAML rendering honoring `--output` flag |
| `OrgIdentity` | `organizationId()`, `userId()`, `roles()`, `isResolved()` |
| `CliConfig` | Read-only access to `~/.webrobot/config.cfg` |

---

## v0.1 → v0.2 compatibility

`WebroCliPlugin#init(WebroCliContext)` is a `default` method (no-op) — v0.1 plugins continue to compile and load on v0.2 hosts. They simply don't get a context, which means their commands can't talk to the API without bootstrapping their own client. v0.2+ plugins should override `init()` and stash the context.

---

## Path templating in `WebroApiClient`

The `{name}` placeholders in a path are substituted from the `queryParams` map; matching keys are removed from the query string:

```java
client.get("/webrobot/api/projects/{id}/jobs",
           Map.of("id", "42", "limit", "100"));
// → GET /webrobot/api/projects/42/jobs?limit=100
```

---

## Where things run

| | Provided by |
|---|---|
| Interface contracts | This SDK (compiled into your JAR via `provided`) |
| Implementations (HTTP, output, config) | The CLI host (`webrobot-cli`) |
| Picocli engine | The CLI host |
| Partner plugin | Partner JAR in `~/.webrobot/plugins/` |

The CLI host owns auth, retries, error mapping, output formatting, and plugin discovery. Partner plugins focus on business logic.
