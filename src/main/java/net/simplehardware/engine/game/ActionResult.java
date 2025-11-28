package net.simplehardware.engine.game;

/**
 * Represents the result of an action
 */
public class ActionResult {
    private final boolean success;
    private final String details;

    public ActionResult(boolean success, String details) {
        this.success = success;
        this.details = details;
    }

    public static ActionResult ok(String details) {
        return new ActionResult(true, details);
    }

    public static ActionResult fail(String reason) {
        return new ActionResult(false, reason);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getDetails() {
        return details;
    }

    @Override
    public String toString() {
        return (success ? "OK" : "NOK") + (details != null && !details.isEmpty() ? " " + details : "");
    }
}
