package eu.webrobot.cli.sdk;

import org.slf4j.Logger;

import java.nio.file.Path;

/**
 * Runtime context handed to a plugin once at load time via
 * {@link WebroCliPlugin#init(WebroCliContext)}. Bundles the platform services a
 * partner-authored command needs.
 *
 * <p>Typical usage from a plugin's picocli {@code @Command} class:
 * <pre>{@code
 * @Command(name = "deploy")
 * class DeployCommand implements Runnable {
 *     static WebroCliContext ctx;        // populated by plugin.init(...)
 *
 *     @Parameters String bundle;
 *
 *     public void run() {
 *         JsonNode resp = ctx.api().post("/webrobot/api/acme/deploy",
 *             Map.of("bundle", bundle));
 *         ctx.output().table(resp);
 *     }
 * }
 * }</pre>
 *
 * @since 0.2.0
 */
public interface WebroCliContext {

    /** Pre-configured generic HTTP client (auth + base URL already wired). */
    WebroApiClient api();

    /** Read-only view of the user's CLI config file. */
    CliConfig config();

    /** Identity resolved from the API key or JWT. May be unresolved. */
    OrgIdentity org();

    /** Output formatter honoring the global {@code --output} flag. */
    OutputFormatter output();

    /** Structured logger. Use this rather than {@code System.err} for diagnostics. */
    Logger log();

    /**
     * Per-plugin scratch directory under {@code ~/.webrobot/plugins/data/{pluginId}/}.
     * Created lazily on first call. Use for caches, downloaded assets, plugin-local state.
     */
    Path pluginDataDir(String pluginId);

    /**
     * Semver of the host CLI's plugin API. Plugins MAY check this and refuse to
     * register on unsupported versions — but the host already enforces a basic
     * compatibility check, so most plugins won't need to inspect this.
     */
    String cliVersion();
}
