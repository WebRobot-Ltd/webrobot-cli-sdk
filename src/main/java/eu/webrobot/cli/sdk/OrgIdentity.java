package eu.webrobot.cli.sdk;

import java.util.Set;

/**
 * Identity context resolved from the user's API key or JWT.
 *
 * <p>Use {@link #organizationId()} for all multi-tenancy scoping in API calls —
 * never trust org IDs supplied via flags or stdin.
 *
 * <p>{@link #isResolved()} is {@code false} for invocations against unauthenticated
 * paths (e.g. {@code webrobot --version}, {@code webrobot config init}).
 *
 * @since 0.2.0
 */
public interface OrgIdentity {

    String organizationId();

    String userId();

    Set<String> roles();

    boolean isResolved();
}
