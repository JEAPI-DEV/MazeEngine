package net.simplehardware.engine.viewer.elements;

import java.io.Serial;
import java.io.Serializable;

/**
 * Player log data for a specific turn
 */
public class PlayerLog implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final String stdout;
    private final String stderr;

    public PlayerLog(String stdout, String stderr) {
        this.stdout = stdout != null ? stdout : "";
        this.stderr = stderr != null ? stderr : "";
    }

    public String getStdout() {
        return stdout;
    }

    public String getStderr() {
        return stderr;
    }
}
