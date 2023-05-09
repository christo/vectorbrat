package com.chromosundrift.vectorbrat.swing;

public class DisplayController {
    private boolean drawPathPlan;

    public DisplayController(boolean drawPathPlan) {
        this.drawPathPlan = drawPathPlan;
    }


    public boolean isDrawPathPlan() {
        return drawPathPlan;
    }

    public void setDrawPathPlan(boolean drawPathPlan) {
        this.drawPathPlan = drawPathPlan;
    }
}
