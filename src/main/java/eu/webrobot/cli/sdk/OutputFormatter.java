package eu.webrobot.cli.sdk;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Renders command output in the format the user requested via the global
 * {@code --output} flag. Plugins should always print through this rather than
 * {@code System.out} so {@code --output table|json|yaml} works uniformly across
 * core and partner commands.
 *
 * @since 0.2.0
 */
public interface OutputFormatter {

    /**
     * Pretty-print as a table. {@code rows} is expected to be a JSON array of
     * objects with consistent keys; the formatter picks columns from the first
     * row and aligns them.
     */
    void table(JsonNode rows);

    /** Pretty-print JSON with 2-space indentation. */
    void json(JsonNode value);

    /** Pretty-print YAML. */
    void yaml(JsonNode value);

    /** Raw line — bypasses format selection. Useful for prompts and progress. */
    void println(String line);

    /** Active format ({@code "table"}, {@code "json"}, {@code "yaml"}). */
    String format();
}
