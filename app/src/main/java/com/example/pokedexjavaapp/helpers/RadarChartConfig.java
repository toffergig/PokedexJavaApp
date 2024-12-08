// File: RadarChartConfig.java
// Package: com.example.pokedexjavaapp.helpers

package com.example.pokedexjavaapp.helpers;

/**
 * Configuration class for customizing RadarChart behavior.
 */
public class RadarChartConfig {
    private boolean showLegend;
    private boolean drawLabels;

    /**
     * Constructor to initialize RadarChartConfig with desired settings.
     *
     * @param showLegend  Whether to display the chart legend.
     * @param drawLabels  Whether to draw Y-axis labels.
     */
    public RadarChartConfig(boolean showLegend, boolean drawLabels) {
        this.showLegend = showLegend;
        this.drawLabels = drawLabels;
    }

    /**
     * Returns whether the legend should be displayed.
     *
     * @return True if legend is to be shown, false otherwise.
     */
    public boolean isShowLegend() {
        return showLegend;
    }

    /**
     * Sets whether the legend should be displayed.
     *
     * @param showLegend True to show legend, false to hide.
     */
    public void setShowLegend(boolean showLegend) {
        this.showLegend = showLegend;
    }

    /**
     * Returns whether Y-axis labels should be drawn.
     *
     * @return True if labels are to be drawn, false otherwise.
     */
    public boolean isDrawLabels() {
        return drawLabels;
    }

    /**
     * Sets whether Y-axis labels should be drawn.
     *
     * @param drawLabels True to draw labels, false to hide.
     */
    public void setDrawLabels(boolean drawLabels) {
        this.drawLabels = drawLabels;
    }
}
