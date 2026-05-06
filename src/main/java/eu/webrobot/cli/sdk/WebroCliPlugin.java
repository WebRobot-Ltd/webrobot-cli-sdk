package eu.webrobot.cli.sdk;

import java.util.List;

/**
 * Extension point for adding Picocli subcommands to the WebRobot CLI.
 *
 * <p>Implement this interface in your plugin JAR, annotate the concrete command
 * classes with {@code @Command}, and register the plugin via ServiceLoader:
 *
 * <pre>
 * META-INF/services/eu.webrobot.cli.sdk.WebroCliPlugin
 *   → com.mycompany.myplugin.cli.MyPluginCli
 * </pre>
 *
 * <p>Place your plugin JAR in {@code ~/.webrobot/plugins/} — the CLI discovers
 * and loads it automatically at startup.
 *
 * <h3>v0.1 vs v0.2 plugins</h3>
 * v0.1 plugins implement {@link #pluginId()}, {@link #description()}, and
 * {@link #commands()} only. Those plugins continue to work unchanged.
 *
 * <p>v0.2+ plugins SHOULD additionally override {@link #init(WebroCliContext)} to
 * receive the runtime services (API client, config, output formatter, …) and
 * stash the context for use by their {@code @Command} classes. Without this,
 * commands have no way to talk to the platform.
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

    /**
     * Optional context handoff. Called once by the host CLI immediately after the
     * plugin is loaded via ServiceLoader and before any of its commands are
     * dispatched. Plugins typically retain the context for their command classes
     * to reference at execution time.
     *
     * <p>Default no-op so that v0.1 plugins continue to compile and load against
     * v0.2+ hosts.
     *
     * @since 0.2.0
     */
    default void init(WebroCliContext context) {
        // no-op by default
    }
}
