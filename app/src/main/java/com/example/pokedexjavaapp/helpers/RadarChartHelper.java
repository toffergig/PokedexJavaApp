// File: RadarChartHelper.java
// Package: com.example.pokedexjavaapp.helpers

package com.example.pokedexjavaapp.helpers;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.example.pokedexjavaapp.R;
import com.example.pokedexjavaapp.models.PokemonDetails;
import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Manages the RadarChart configurations and data population.
 */
public class RadarChartHelper {

    private static final String TAG = "RadarChartHelper";

    private final Context context;
    private final RadarChart radarChart;
    private final int[] fillColors;
    private final int[] lineColors;

    /**
     * Constructor to initialize RadarChartHelper with context and RadarChart view.
     *
     * @param context    The context from the activity.
     * @param radarChart The RadarChart view to manage.
     */
    public RadarChartHelper(Context context, RadarChart radarChart) {
        this.context = context;
        this.radarChart = radarChart;

        // Initialize colors as per original configurations
        fillColors = new int[]{
                ContextCompat.getColor(context, R.color.blue_fill),   // #85c1e9
                ContextCompat.getColor(context, R.color.green_fill),  // #a2d9ce
                ContextCompat.getColor(context, R.color.red_fill),    // #fadbd8
                // Add more colors if needed
        };

        // Use the same colors for line outlines
        lineColors = fillColors.clone();

        // Configure the overall appearance of the RadarChart
        configureChartAppearance();
    }

    /**
     * Configures the overall appearance of the RadarChart.
     */
    private void configureChartAppearance() {
        radarChart.getDescription().setEnabled(false); // Disable description text
        radarChart.setWebColor(Color.GRAY);
        radarChart.setWebLineWidth(1.5f);
        radarChart.setWebColorInner(Color.LTGRAY);
        radarChart.setWebLineWidthInner(1.5f);
        radarChart.setWebAlpha(100);
        radarChart.animateXY(1000, 1000); // Animate chart loading
    }

    /**
     * Configures the X and Y axes of the RadarChart.
     *
     * @param statLabels List of stat labels.
     * @param yAxisMax   The maximum Y-axis value.
     * @param drawLabels Whether to draw Y-axis labels.
     */
    public void configureChartAxes(List<String> statLabels, float yAxisMax, boolean drawLabels) {
        // Configure XAxis with stat labels
        XAxis xAxis = radarChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(statLabels));
        xAxis.setTextSize(14f);
        xAxis.setTextColor(Color.BLACK);

        // Configure YAxis with min and max values
        YAxis yAxis = radarChart.getYAxis();
        yAxis.setLabelCount(5, true);
        yAxis.setTextSize(16f);
        yAxis.setAxisMinimum(0f); // Ensuring min is not negative
        yAxis.setAxisMaximum(yAxisMax);
        yAxis.setDrawLabels(drawLabels); // Conditionally show Y-axis labels
        yAxis.setGranularity(5f);  // Interval between ticks
        yAxis.setGranularityEnabled(true); // Enforce granularity
    }

    /**
     * Customizes the legend of the RadarChart.
     *
     * @param showLegend Whether to display the legend.
     */
    public void customizeLegend(boolean showLegend) {
        Legend legend = radarChart.getLegend();

        legend.setEnabled(showLegend);
        if (showLegend) {
            legend.setForm(Legend.LegendForm.CIRCLE); // Options: CIRCLE, SQUARE, LINE, etc.
            legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
            legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
            legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
            legend.setDrawInside(false); // Draw legend outside the chart

            legend.setTextSize(18f); // Set desired text size in SP (e.g., 18sp)
            legend.setTextColor(Color.BLACK);
            legend.setFormSize(10f); // Adjust the size of the form (shape) in the legend
            legend.setXEntrySpace(10f); // Horizontal space between entries
            legend.setYEntrySpace(5f);  // Vertical space between entries
        }
    }

    /**
     * Builds and sets the RadarChart data.
     *
     * @param pokemonDetailsList List of fetched Pokémon details.
     * @param config             Configuration settings for the RadarChart.
     */
    public void buildRadarChart(List<PokemonDetails> pokemonDetailsList, RadarChartConfig config) {
        if (pokemonDetailsList == null || pokemonDetailsList.isEmpty()) {
            Log.e(TAG, "PokemonDetails list is null or empty.");
            return;
        }

        // Define stat labels
        List<String> statLabels = Arrays.asList("HP", "Attack", "Defense", "Sp. Atk", "Sp. Def", "Speed");

        // Calculate Y-axis bounds based on all Pokémon stats
        float yAxisMax = calculateYAxisMaximum(pokemonDetailsList);

        // Configure axes with calculated bounds and label settings
        configureChartAxes(statLabels, yAxisMax, config.isDrawLabels());

        // Customize the legend based on configuration
        customizeLegend(config.isShowLegend());

        // Prepare RadarData
        RadarData radarData = new RadarData();

        // Determine the number of colors available
        int colorCount = fillColors.length;

        // Iterate through each PokemonDetails to create datasets
        for (int i = 0; i < pokemonDetailsList.size(); i++) {
            PokemonDetails details = pokemonDetailsList.get(i);
            List<RadarEntry> entries = convertStatsToEntries(details);
            RadarDataSet dataSet = new RadarDataSet(entries, capitalize(details.getName()));

            // Assign colors based on the dataset index
            if (i < colorCount) {
                dataSet.setColor(lineColors[i]);
                dataSet.setFillColor(fillColors[i]);
            } else {
                // If there are more Pokémon than predefined colors, cycle through colors
                int colorIndex = i % colorCount;
                dataSet.setColor(lineColors[colorIndex]);
                dataSet.setFillColor(fillColors[colorIndex]);
            }

            dataSet.setDrawFilled(true);
            dataSet.setFillAlpha(60);
            dataSet.setLineWidth(2f);
            dataSet.setDrawValues(false); // Disable label values
//            dataSet.setValueTextSize(12f);
            dataSet.setValueTextColor(Color.BLACK);

            // Enable drawing of icons (markers)
            dataSet.setDrawIcons(true);

            // Generate a tinted drawable for the current dataset
            Drawable circleMarker = getTintedDrawable(dataSet.getColor());
            if (circleMarker != null) {
                // Assign the tinted drawable to each entry in the dataset
                for (RadarEntry entry : entries) {
                    entry.setIcon(circleMarker);
                }
            } else {
                Log.e(TAG, "Failed to create tinted marker for dataset: " + details.getName());
            }

            radarData.addDataSet(dataSet);
        }

        // Set data to radar chart
        radarChart.setData(radarData);
        radarChart.invalidate(); // Refresh chart
    }

    /**
     * Converts a Pokémon's base stats into RadarEntries.
     *
     * @param details The PokémonDetails object containing stats.
     * @return A list of RadarEntry objects representing the stats.
     */
    public List<RadarEntry> convertStatsToEntries(PokemonDetails details) {
        List<RadarEntry> entries = new ArrayList<>();
        if (details.getStats() == null || details.getStats().size() < 6) {
            // If stats are missing or incomplete, log an error and fill with zeros
//            Log.e(TAG, "Incomplete stats for Pokémon: " + details.getName());
            for (int i = 0; i < 6; i++) {
                entries.add(new RadarEntry(0f));
            }
            return entries;
        }

        // Assuming stats are ordered as HP, Attack, Defense, Sp. Atk, Sp. Def, Speed
        for (PokemonDetails.Stat stat : details.getStats()) {
            float statValue = (float) stat.getBaseStat();
            entries.add(new RadarEntry(statValue));
        }

        return entries;
    }

    /**
     * Creates a tinted drawable based on the provided color.
     *
     * @param color The color to tint the drawable.
     * @return A tinted Drawable, or null if the base drawable is not found.
     */
    public Drawable getTintedDrawable(int color) {
        // Obtain the base drawable
        Drawable baseDrawable = ContextCompat.getDrawable(context, R.drawable.circle_marker);
        if (baseDrawable == null) {
            Log.e(TAG, "circle_marker drawable not found.");
            return null;
        }

        // Create a mutable copy of the drawable to avoid modifying the original
        Drawable tintedDrawable = baseDrawable.mutate();

        // Apply the color tint using PorterDuff mode
        tintedDrawable.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));

        return tintedDrawable;
    }

    /**
     * Calculates the Y-axis maximum based on the highest base stat among all selected Pokémon.
     *
     * @param pokemonDetailsList List of fetched Pokémon details.
     * @return The maximum Y-axis value.
     */
    public float calculateYAxisMaximum(List<PokemonDetails> pokemonDetailsList) {
        float maxStat = 0f;
        for (PokemonDetails details : pokemonDetailsList) {
            if (details.getStats() != null) {
                for (PokemonDetails.Stat stat : details.getStats()) {
                    if (stat.getBaseStat() > maxStat) {
                        maxStat = (float) stat.getBaseStat();
                    }
                }
            }
        }
        return maxStat; // Add 20% margin for better visualization
    }

    /**
     * Capitalizes the first letter of a given string.
     *
     * @param text The input string.
     * @return The capitalized string.
     */
    public String capitalize(String text) {
        if (text == null || text.isEmpty()) return "";
        return text.substring(0, 1).toUpperCase(Locale.ROOT) + text.substring(1);
    }
}
