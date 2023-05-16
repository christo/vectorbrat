package com.chromosundrift.vectorbrat.swing;

public class DisplayController {
    private boolean drawPathPlan;
    private Runnable repaintDisplay;

    public DisplayController(boolean drawPathPlan) {
        this.drawPathPlan = drawPathPlan;
    }

    public boolean isDrawPathPlan() {
        return drawPathPlan;
    }

    public void setDrawPathPlan(boolean drawPathPlan) {
        this.drawPathPlan = drawPathPlan;
        if (repaintDisplay != null) {
            repaintDisplay.run();
        }
    }

    public void setRepaintDisplay(Runnable repaintDisplay) {
        this.repaintDisplay = repaintDisplay;
    }
}
