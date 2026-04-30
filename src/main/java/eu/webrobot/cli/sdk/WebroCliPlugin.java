package eu.webrobot.cli.sdk;

import java.util.List;

/**
 * Extension point for adding Picocli subcommands to the WebRobot CLI.
 *
 * Implement this interface in your plugin JAR, annotate the concrete class with
 * {@code @Command}, and register it via ServiceLoader:
 *
 * <pre>
 * META-INF/services/eu.webrobot.cli.sdk.WebroCliPlugin
 *   → com.mycompany.myplugin.cli.MyPluginCli
 * </pre>
 *
 * Place your plugin JAR in {@code ~/.webrobot/plugins/} — the CLI discovers and
 * loads it automatically at startup.
 */
public interface WebroCliPlugin {

    /** Unique identifier for this plugin (matches pluginId in manifest.json). */
    String pluginId();

    /** One-line description shown in the CLI help footer. */
    String description();

    /**
     * Picocli {@code @Command}-annotated classes contributed by this plugin.
     * Each class is registered as a top-level subcommand of {@code webrobot}.
     */
    List<Class<?>> commands();
}
