package eu.webrobot.cli.sdk;

import java.util.Collections;
import java.util.Map;

/**
 * Thrown by {@link WebroApiClient} when an HTTP call fails. Subclasses give
 * callers a way to disambiguate common cases without inspecting the status code.
 *
 * @since 0.2.0
 */
public class WebroApiException extends RuntimeException {

    private final int statusCode;
    private final String errorBody;
    private final Map<String, String> headers;
    private final String requestId;

    public WebroApiException(int statusCode, String errorBody,
                             Map<String, String> headers, String requestId, String message) {
        super(message);
        this.statusCode = statusCode;
        this.errorBody  = errorBody;
        this.headers    = headers == null ? Collections.emptyMap() : headers;
        this.requestId  = requestId;
    }

    public WebroApiException(int statusCode, String errorBody,
                             Map<String, String> headers, String requestId,
                             String message, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
        this.errorBody  = errorBody;
        this.headers    = headers == null ? Collections.emptyMap() : headers;
        this.requestId  = requestId;
    }

    public int statusCode()                { return statusCode; }
    public String errorBody()              { return errorBody; }
    public Map<String, String> headers()   { return headers; }
    public String requestId()              { return requestId; }

    /** 401 — caller is unauthenticated. */
    public static class Auth extends WebroApiException {
        public Auth(int sc, String body, Map<String, String> h, String rid, String msg) {
            super(sc, body, h, rid, msg);
        }
    }

    /** 403 — caller is authenticated but not permitted. */
    public static class Forbidden extends WebroApiException {
        public Forbidden(int sc, String body, Map<String, String> h, String rid, String msg) {
            super(sc, body, h, rid, msg);
        }
    }

    /** 404 — path or resource not found. */
    public static class NotFound extends WebroApiException {
        public NotFound(int sc, String body, Map<String, String> h, String rid, String msg) {
            super(sc, body, h, rid, msg);
        }
    }

    /** 400 / 422 — request rejected for invalid payload. */
    public static class Validation extends WebroApiException {
        public Validation(int sc, String body, Map<String, String> h, String rid, String msg) {
            super(sc, body, h, rid, msg);
        }
    }

    /** 5xx after the configured retry budget is exhausted. */
    public static class Server extends WebroApiException {
        public Server(int sc, String body, Map<String, String> h, String rid, String msg) {
            super(sc, body, h, rid, msg);
        }
    }
}
