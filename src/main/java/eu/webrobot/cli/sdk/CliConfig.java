package eu.webrobot.cli.sdk;

import java.nio.file.Path;
import java.util.Optional;

/**
 * Read-only access to the user's CLI configuration ({@code ~/.webrobot/config.cfg}
 * by default). Plugins should NOT mutate the config — use the {@code webrobot config}
 * command for that.
 *
 * @since 0.2.0
 */
public interface CliConfig {

    /** API base URL, e.g. {@code https://api.webrobot.eu}. */
    String apiEndpoint();

    /** Read an arbitrary key from the config file. Empty when missing. */
    Optional<String> get(String key);

    /** Path to the config file backing this instance. */
    Path configFile();
}
