package eu.webrobot.cli.sdk;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Generic HTTP client over the WebRobot REST API. Knows no specific endpoints —
 * partner CLI plugins call any path with arbitrary parameters.
 *
 * <p>Auth (API key / JWT), base URL, retries, JSON serde and structured error
 * mapping are handled by the host CLI. Partner plugins use it directly.
 *
 * <h3>Path templating</h3>
 * Path placeholders in the form {@code {name}} are substituted from the
 * {@code queryParams} map before the request is sent; matching keys are
 * removed from the query string. Example:
 * <pre>{@code
 * client.get("/webrobot/api/projects/{id}/jobs", Map.of("id", "42", "limit", "100"));
 * // → GET /webrobot/api/projects/42/jobs?limit=100
 * }</pre>
 *
 * @since 0.2.0
 */
public interface WebroApiClient {

    JsonNode get(String path, Map<String, Object> queryParams);

    JsonNode post(String path, Object body);

    JsonNode put(String path, Object body);

    JsonNode patch(String path, Object body);

    JsonNode delete(String path);

    /**
     * Streaming GET for SSE / NDJSON endpoints (e.g. job log follow).
     * The returned stream lazily emits one JsonNode per server-sent event or
     * newline-delimited JSON object. Closing the stream cancels the request.
     */
    Stream<JsonNode> stream(String path, Map<String, Object> queryParams);

    /** Typed sugar — Jackson maps the response body to the given class. */
    <T> T get(String path, Map<String, Object> queryParams, Class<T> type);

    /** Typed sugar for endpoints returning a JSON array. */
    <T> List<T> getList(String path, Map<String, Object> queryParams, Class<T> type);
}
