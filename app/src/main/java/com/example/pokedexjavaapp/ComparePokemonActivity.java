// File: ComparePokemonActivity.java
// Package: com.example.pokedexjavaapp

package com.example.pokedexjavaapp;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pokedexjavaapp.api.PokemonApiService;
import com.example.pokedexjavaapp.api.RetrofitClient;
import com.example.pokedexjavaapp.helpers.RadarChartConfig;
import com.example.pokedexjavaapp.helpers.RadarChartHelper;
import com.example.pokedexjavaapp.models.PokemonDetails;
import com.github.mikephil.charting.charts.RadarChart;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Activity to compare multiple Pokémon using a RadarChart.
 */
public class ComparePokemonActivity extends AppCompatActivity {

    private static final String TAG = "ComparePokemonActivity";

    // UI Components
    private RadarChart radarChart;
    private ProgressBar progressBar;

    // API Service
    private PokemonApiService apiService;

    // Selected Pokémon IDs passed from MainActivity
    private ArrayList<Integer> selectedPokemonIds;

    // Store fetched Pokémon details
    private final List<PokemonDetails> fetchedDetails = new ArrayList<>();

    // Track completed API requests
    private int requestsCompleted = 0;

    // RadarChart Helper
    private RadarChartHelper radarChartHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.compare_pokemon_activity);

        initializeViews();
        initializeApiService();
        retrieveSelectedPokemonIds();
        setupActionBar();

        if (selectedPokemonIds == null || selectedPokemonIds.isEmpty()) {
            // No Pokémon selected, show message and exit
            Log.e(TAG, "No Pokémon IDs received for comparison.");
            Toast.makeText(this, "No Pokémon selected for comparison.", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity since there's nothing to compare
            return;
        }

        // Initialize RadarChartHelper with the RadarChart view
        radarChartHelper = new RadarChartHelper(this, radarChart);

        fetchSelectedPokemonDetails();
    }

    /**
     * Setup the app bar with a back button.
     */
    private void setupActionBar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Enable the back button
            getSupportActionBar().setTitle("Compare Pokémon");    // Set ActionBar title
        }
    }

    /**
     * Handle app bar item selection.
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) { // Check if the back button was pressed
            getOnBackPressedDispatcher().onBackPressed(); // Trigger default back navigation
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Initialize UI components by binding them to their XML counterparts.
     */
    private void initializeViews() {
        radarChart = findViewById(R.id.radar_chart);
//        progressBar = findViewById(R.id.progress_bar_compare);
    }

    /**
     * Initialize the API service using Retrofit.
     */
    private void initializeApiService() {
        apiService = RetrofitClient.getPokemonApiService();
    }

    /**
     * Retrieve the list of selected Pokémon IDs passed via the Intent.
     */
    private void retrieveSelectedPokemonIds() {
        // Assuming you're passing an ArrayList<Integer> via Intent with key "selected_pokemon_ids"
        selectedPokemonIds = getIntent().getIntegerArrayListExtra("selected_pokemon_ids");
    }

    /**
     * Fetch details for each selected Pokémon ID asynchronously.
     * Once all requests are completed, proceed to build the radar chart.
     */
    private void fetchSelectedPokemonDetails() {
//        showLoadingIndicator(true);
        for (Integer id : selectedPokemonIds) {
            apiService.getPokemonDetails(id).enqueue(new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<PokemonDetails> call, @NonNull Response<PokemonDetails> response) {
                    requestsCompleted++;
                    if (response.isSuccessful() && response.body() != null) {
                        fetchedDetails.add(response.body());
                        Log.d(TAG, "Fetched details for Pokémon ID: " + id);
                    } else {
                        Log.e(TAG, "Failed to fetch details for Pokémon ID: " + id);
                    }
                    checkIfAllRequestsCompleted();
                }

                @Override
                public void onFailure(@NonNull Call<PokemonDetails> call, @NonNull Throwable t) {
                    requestsCompleted++;
                    Log.e(TAG, "API call failed for Pokémon ID: " + id + ". Error: " + t.getMessage());
                    checkIfAllRequestsCompleted();
                }
            });
        }
    }

    /**
     * Check if all Pokémon detail requests have been completed.
     * If all requests are done, sort the Pokémon and build the radar chart.
     */
    private void checkIfAllRequestsCompleted() {
        if (requestsCompleted == selectedPokemonIds.size()) {
//            showLoadingIndicator(false);
            if (fetchedDetails.isEmpty()) {
                // All requests failed
                Toast.makeText(this, "Failed to fetch any Pokémon details for comparison.", Toast.LENGTH_SHORT).show();
                finish(); // Close the activity since there's no data to display
                return;
            }
            sortPokemonByAverage(); // Sort Pokémon based on average base stats
            buildRadarChart();
        }
    }

    /**
     * Sort the fetchedDetails list in descending order based on average base stats.
     */
    private void sortPokemonByAverage() {
        fetchedDetails.sort((p1, p2) -> {
            float avg1 = calculateAverageBaseStat(p1);
            float avg2 = calculateAverageBaseStat(p2);
            return Float.compare(avg2, avg1); // Descending order
        });


        for (PokemonDetails pokemon : fetchedDetails) {
            Log.d(TAG, "Pokémon: " + pokemon.getName() + ", Average Base Stat: " + calculateAverageBaseStat(pokemon));
        }
    }

    /**
     * Calculate the average base stats for a given Pokémon.
     *
     * @param pokemon The PokémonDetails object.
     * @return The average base stat as a float.
     */
    private float calculateAverageBaseStat(PokemonDetails pokemon) {
        if (pokemon.getStats() == null || pokemon.getStats().isEmpty()) {
            return 0f;
        }

        int sum = 0;
        for (PokemonDetails.Stat stat : pokemon.getStats()) {
            sum += stat.getBaseStat();
        }

        return (float) sum / pokemon.getStats().size();
    }

    /**
     * Builds the RadarChart using the RadarChartHelper.
     */
    private void buildRadarChart() {
        // Create RadarChartConfig based on activity requirements
        // For ComparePokemonActivity, legends and labels are needed
        RadarChartConfig config = new RadarChartConfig(true, true);

        // Build the RadarChart with the provided configuration
        radarChartHelper.buildRadarChart(fetchedDetails, config);
    }

}
