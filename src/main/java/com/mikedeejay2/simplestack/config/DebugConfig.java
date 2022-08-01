package com.mikedeejay2.simplestack.config;

/**
 * Secret debug options, these options are never saved to disk and always are false on plugin load/reload.
 *
 * @author Mikedeejay2
 */
public class DebugConfig {
    private boolean printTimings;
    private boolean printAction;

    public DebugConfig() {
        this.printTimings = false;
        this.printAction = false;
    }

    public boolean isPrintTimings() {
        return printTimings;
    }

    public void setPrintTimings(boolean printTimings) {
        this.printTimings = printTimings;
    }

    public boolean isPrintAction() {
        return printAction;
    }

    public void setPrintAction(boolean printAction) {
        this.printAction = printAction;
    }
}
