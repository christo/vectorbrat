package com.chromosundrift.vectorbrat.swing;

/**
 * Model for UI options.
 */
public class DisplayController {
    private Mode mode;
    private Runnable repaintDisplay;

    public enum Mode {
        DEBUG("Path Plan"), DISPLAY("Vector Display"), SIMULATOR("Simulator");

        private final String uiLabel;

        Mode(String uiLabel) {
            this.uiLabel = uiLabel;
        }

        public String getUiLabel() {
            return uiLabel;
        }
    }

    public DisplayController(Mode mode) {
        this.mode = mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
        if (mode != Mode.SIMULATOR && repaintDisplay != null) {
            repaintDisplay.run();
        }
    }

    public Mode getMode() {
        return this.mode;
    }

    public void setRepaintDisplay(Runnable repaintDisplay) {
        // WART: fugly hack
        this.repaintDisplay = repaintDisplay;
    }
}
