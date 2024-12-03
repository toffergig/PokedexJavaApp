package com.example.pokedexjavaapp;


import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pokedexjavaapp.api.PokemonApiService;
import com.example.pokedexjavaapp.api.RetrofitClient;
import com.example.pokedexjavaapp.models.PokemonDetails;
import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PokemonDetailsActivity extends AppCompatActivity {

    private static final String TAG = "PokemonDetailsActivity";

    private TextView pokemonNameTextView;
    private ImageView pokemonImageView;
    private TextView pokemonIdTextView;

    // TextViews for base stats
    private TextView hpTextView;
    private TextView attackTextView;
    private TextView defenseTextView;
    private TextView specialAttackTextView;
    private TextView specialDefenseTextView;
    private TextView speedTextView;

    private ProgressBar progressBar;
    private RadarChart radarChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pokemon_details_activity);

        // Initialize views
        pokemonNameTextView = findViewById(R.id.pokemon_detail_name);
        pokemonImageView = findViewById(R.id.pokemon_detail_image);
        pokemonIdTextView = findViewById(R.id.pokemon_detail_id);
//
//        hpTextView = findViewById(R.id.hp_stat);
//        attackTextView = findViewById(R.id.attack_stat);
//        defenseTextView = findViewById(R.id.defense_stat);
//        specialAttackTextView = findViewById(R.id.special_attack_stat);
//        specialDefenseTextView = findViewById(R.id.special_defense_stat);
//        speedTextView = findViewById(R.id.speed_stat);
//
//        progressBar = findViewById(R.id.progress_bar);
        radarChart = findViewById(R.id.radar_chart);

        // Get data from Intent
        int pokemonId = getIntent().getIntExtra("pokemon_id", -1);

        if (pokemonId != -1) {
            fetchPokemonDetails(pokemonId);
        } else {
            Log.e(TAG, "Invalid Pokémon ID");
        }
    }

    private void fetchPokemonDetails(int pokemonId) {
      //  showLoadingIndicator(true);

        PokemonApiService apiService = RetrofitClient.getPokemonApiService();

        apiService.getPokemonDetails(pokemonId).enqueue(new Callback<PokemonDetails>() {
            @Override
            public void onResponse(@NonNull Call<PokemonDetails> call, @NonNull Response<PokemonDetails> response) {
               // showLoadingIndicator(false);
                if (response.isSuccessful() && response.body() != null) {
                    displayPokemonDetails(response.body());
                } else {
                    Log.e(TAG, "Failed to get Pokémon details");
                }
            }

            @Override
            public void onFailure(@NonNull Call<PokemonDetails> call, @NonNull Throwable t) {
              //  showLoadingIndicator(false);
                Log.e(TAG, "API call failed: " + t.getMessage());
            }
        });
    }

    private void displayPokemonDetails(PokemonDetails details) {
        String formattedId = String.format(Locale.getDefault(), "%03d", details.getId());
        pokemonIdTextView.setText(formattedId);
        pokemonNameTextView.setText(details.getName());

        // Load the image
        String imageUrl = details.getSprites().getFrontDefault();
        Picasso.get().load(imageUrl).into(pokemonImageView);
        setupRadarChart(details);

        // Display base stats
//        for (PokemonDetails.Stat stat : details.getStats()) {
//            String statName = stat.getStat().getName();
//            int baseStat = stat.getBaseStat();
//
//            switch (statName) {
//                case "hp":
//                    hpTextView.setText(String.valueOf(baseStat));
//                    break;
//                case "attack":
//                    attackTextView.setText(String.valueOf(baseStat));
//                    break;
//                case "defense":
//                    defenseTextView.setText(String.valueOf(baseStat));
//                    break;
//                case "special-attack":
//                    specialAttackTextView.setText(String.valueOf(baseStat));
//                    break;
//                case "special-defense":
//                    specialDefenseTextView.setText(String.valueOf(baseStat));
//                    break;
//                case "speed":
//                    speedTextView.setText(String.valueOf(baseStat));
//                    break;
//                default:
//                    break;
//            }
//        }
    }
    private void setupRadarChart(PokemonDetails details) {
        configureChartAppearance();
        RadarData radarData = createRadarData(details);
        radarChart.setData(radarData);
        configureChartAxes(radarData);
        radarChart.invalidate();
    }

    private void configureChartAppearance() {
        radarChart.getDescription().setEnabled(false);
        radarChart.animateXY(1000, 1000);
    }

    private RadarData createRadarData(PokemonDetails details) {
        List<RadarEntry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        String[] formattedLabels = new String[details.getStats().size()];

        int i = 0; // Counter for formattedLabels array
        for (PokemonDetails.Stat stat : details.getStats()) {
            String statName = stat.getStat().getName();
            int baseStat = stat.getBaseStat();

            Log.d(TAG, "Stat name: " + statName + ", Base Stat: " + baseStat);

            entries.add(new RadarEntry(baseStat));
            labels.add(statName); // Keep original labels for RadarData

            // Format the label for display
            formattedLabels[i++] = statName.substring(0, 1).toUpperCase() + statName.substring(1);
        }

        RadarDataSet dataSet = new RadarDataSet(entries,"");
        dataSet.setColor(Color.BLUE);
        dataSet.setLineWidth(2f);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(8.5f);
        dataSet.setFillColor(Color.BLUE);
        dataSet.setDrawFilled(true);

        RadarData radarData = new RadarData(dataSet);
        radarData.setLabels(formattedLabels); //Original Labels for Data use

        // Set the formatted labels on the X-axis


        return radarData;
    }

    private void configureChartAxes(RadarData radarData) {
        XAxis xAxis = radarChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(radarData.getLabels()));
        xAxis.setTextSize(10f);          // Adjust text size if needed
        xAxis.setYOffset(90f);         // Move labels closer
        //xAxis.setPosition(XAxis.XAxisPosition.BOTTOM_INSIDE);
        YAxis yAxis = radarChart.getYAxis();
        yAxis.setDrawLabels(false);
    }

//    private void showLoadingIndicator(boolean show) {
//        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
//    }
}
