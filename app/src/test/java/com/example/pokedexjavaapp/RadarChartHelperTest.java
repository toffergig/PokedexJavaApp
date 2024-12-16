package com.example.pokedexjavaapp;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;

import com.example.pokedexjavaapp.helpers.RadarChartConfig;
import com.example.pokedexjavaapp.helpers.RadarChartHelper;
import com.example.pokedexjavaapp.models.PokemonDetails;
import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest({Color.class, Log.class}) // Specify your custom Log class
public class RadarChartHelperTest {


    @Mock
    private Context context;

    @Mock
    private RadarChart radarChart;

    private RadarChartHelper radarChartHelper;

    @Before
    public void setUp() {
        context = mock(Context.class);
        radarChart = mock(RadarChart.class);

        // Mock Context resources and colors (only necessary ones)
        Resources resources = mock(Resources.class);
        when(context.getResources()).thenReturn(resources);
        when(ContextCompat.getColor(context, R.color.blue_fill)).thenReturn(Color.BLUE);
        when(ContextCompat.getColor(context, R.color.green_fill)).thenReturn(Color.GREEN);
        when(ContextCompat.getColor(context, R.color.red_fill)).thenReturn(Color.RED);
        com.github.mikephil.charting.components.Description description = mock(com.github.mikephil.charting.components.Description.class);
        when(radarChart.getDescription()).thenReturn(description);
        // Mock RadarChart's XAxis and YAxis
        XAxis xAxis = mock(XAxis.class);
        YAxis yAxis = mock(YAxis.class);
        when(radarChart.getXAxis()).thenReturn(xAxis);
        when(radarChart.getYAxis()).thenReturn(yAxis);
        radarChartHelper = new RadarChartHelper(context, radarChart);
    }

    @Test
    public void testCalculateYAxisMaximum() {
        // Use PokemonDetailsMock to create mock PokemonDetails objects
        PokemonDetails pokemon1 = PokemonDetailsMock.createMockPokemonDetails();
        PokemonDetails pokemon2 = PokemonDetailsMock.createMockPokemonDetails();

        List<PokemonDetails> pokemonDetailsList = Arrays.asList(pokemon1, pokemon2);

        float expectedYAxisMax = 35f; // Expected maximum stat value based on the mock
        float actualYAxisMax = radarChartHelper.calculateYAxisMaximum(pokemonDetailsList);

        assertEquals(expectedYAxisMax, actualYAxisMax, 0.001f); // Assert with a small delta
    }

    @Test
    public void testConfigureChartAxes() {
        // Prepare test data
        List<String> statLabels = Arrays.asList("HP", "Attack", "Defense", "Sp. Atk", "Sp. Def", "Speed");
        float yAxisMax = 100f;
        boolean drawLabels = true;

        // Call the method under test
        radarChartHelper.configureChartAxes(statLabels, yAxisMax, drawLabels);

        // Verify interactions with RadarChart
        XAxis xAxis = radarChart.getXAxis();
        verify(xAxis).setValueFormatter(isA(IndexAxisValueFormatter.class));
        verify(xAxis).setTextSize(14f);
        verify(xAxis).setTextColor(Color.BLACK);

        YAxis yAxis = radarChart.getYAxis();
        verify(yAxis).setLabelCount(5, true);
        verify(yAxis).setTextSize(16f);
        verify(yAxis).setAxisMinimum(0f);
        verify(yAxis).setAxisMaximum(yAxisMax);
        verify(yAxis).setDrawLabels(drawLabels);
        verify(yAxis).setGranularity(5f);
        verify(yAxis).setGranularityEnabled(true);
    }

    @Test
    public void testCustomizeLegend_showLegendTrue() {
        // Mock RadarChart's Legend
        Legend legend = mock(Legend.class);
        when(radarChart.getLegend()).thenReturn(legend);

        // Call the method with showLegend = true
        radarChartHelper.customizeLegend(true);

        // Verify interactions with Legend
        verify(legend).setEnabled(true);
        verify(legend).setForm(Legend.LegendForm.CIRCLE);
        verify(legend).setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        verify(legend).setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        verify(legend).setOrientation(Legend.LegendOrientation.HORIZONTAL);
        verify(legend).setDrawInside(false);
        verify(legend).setTextSize(18f);
        verify(legend).setTextColor(Color.BLACK);
        verify(legend).setFormSize(10f);
        verify(legend).setXEntrySpace(10f);
        verify(legend).setYEntrySpace(5f);
    }

    @Test
    public void testCustomizeLegend_showLegendFalse() {
        // Mock RadarChart's Legend
        Legend legend = mock(Legend.class);
        when(radarChart.getLegend()).thenReturn(legend);

        // Call the method with showLegend = false
        radarChartHelper.customizeLegend(false);

        // Verify interactions with Legend
        verify(legend).setEnabled(false);
        verify(legend, never()).setForm(any(Legend.LegendForm.class)); // or any other method inside the if block
    }

}