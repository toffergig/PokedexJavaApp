package com.example.pokedexjavaapp;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.github.mikephil.charting.interfaces.datasets.IRadarDataSet;
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
    private RadarChart radarChart;
    private ProgressBar progressBar;
    private LinearLayout pokemonTypesLayout; // Added
    private LinearLayout pokemonAbilitiesLayout; // Added

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pokemon_details_activity);
        initializeViews();
        int pokemonId = getIntent().getIntExtra("pokemon_id", -1);
        if (pokemonId != -1) {
            fetchPokemonDetails(pokemonId);
        } else {
            Log.e(TAG, "Invalid Pokémon ID");
        }
    }

    private void initializeViews() {
        pokemonNameTextView = findViewById(R.id.pokemon_detail_name);
        pokemonImageView = findViewById(R.id.pokemon_detail_image);
        pokemonIdTextView = findViewById(R.id.pokemon_detail_id);
        radarChart = findViewById(R.id.radar_chart);
        progressBar = findViewById(R.id.progress_bar);
        pokemonTypesLayout = findViewById(R.id.pokemon_types_layout); // Added
        pokemonAbilitiesLayout = findViewById(R.id.pokemon_abilities_layout); // Added
    }

    private void fetchPokemonDetails(int pokemonId) {
        showLoadingIndicator(true);
        PokemonApiService apiService = RetrofitClient.getPokemonApiService();
        apiService.getPokemonDetails(pokemonId).enqueue(new Callback<PokemonDetails>() {
            @Override
            public void onResponse(@NonNull Call<PokemonDetails> call, @NonNull Response<PokemonDetails> response) {
                showLoadingIndicator(false);
                if (response.isSuccessful() && response.body() != null) {
                    displayPokemonDetails(response.body());
                } else {
                    Log.e(TAG, "Failed to get Pokémon details");
                }
            }

            @Override
            public void onFailure(@NonNull Call<PokemonDetails> call, @NonNull Throwable t) {
                showLoadingIndicator(false);
                Log.e(TAG, "API call failed: " + t.getMessage());
            }
        });
    }

    private void displayPokemonDetails(PokemonDetails details) {
        String formattedId = String.format(Locale.getDefault(), "%03d", details.getId());
        pokemonIdTextView.setText(formattedId);
        pokemonNameTextView.setText(details.getName());
        String imageUrl = details.getSprites().getFrontDefault();
        Picasso.get().load(imageUrl).into(pokemonImageView);
        displayPokemonTypes(details.getTypes());
        displayPokemonAbilities(details.getAbilities()); // Added
        setupRadarChart(details);
    }
    private void displayPokemonAbilities(List<PokemonDetails.AbilityInfo> abilities) {
        // Remove existing ability views but keep the label
        if (pokemonAbilitiesLayout.getChildCount() > 1) {
            pokemonAbilitiesLayout.removeViews(1, pokemonAbilitiesLayout.getChildCount() - 1);
        }

        // Limit to 4 abilities
        int abilitiesToShow = Math.min(abilities.size(), 4);

        for (int i = 0; i < abilitiesToShow; i++) {
            PokemonDetails.AbilityInfo abilityInfo = abilities.get(i);
            String abilityName = abilityInfo.getAbility().getName();

            // Create TextView for the ability
            TextView abilityTextView = new TextView(this);
            abilityTextView.setText(capitalize(abilityName));
            abilityTextView.setTextColor(Color.BLACK);
            abilityTextView.setPadding(8, 4, 8, 4);

            // Optionally, indicate if the ability is hidden
            if (abilityInfo.isHidden()) {
                abilityTextView.setTypeface(null, Typeface.ITALIC);
                abilityTextView.setText(abilityTextView.getText() + " (Hidden)");
            }

            // Set layout parameters with margin
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 4, 0, 4);
            abilityTextView.setLayoutParams(params);

            // Add the TextView to the layout
            pokemonAbilitiesLayout.addView(abilityTextView);
        }
    }
    private void displayPokemonTypes(List<PokemonDetails.TypeInfo> types) {
        // Remove existing type views but keep the label
        if (pokemonTypesLayout.getChildCount() > 1) {
            pokemonTypesLayout.removeViews(1, pokemonTypesLayout.getChildCount() - 1);
        }

        for (PokemonDetails.TypeInfo typeInfo : types) {
            String typeName = typeInfo.getType().getName();

            // Create TextView for the type
            TextView typeTextView = new TextView(this);
            typeTextView.setText(typeName.toUpperCase(Locale.ROOT));
            typeTextView.setTextColor(Color.WHITE);
            typeTextView.setPadding(16, 8, 16, 8);

            // Set background color based on type name
            int bgColor = getTypeColor(typeName);
            typeTextView.setBackgroundColor(bgColor);

            // Set margins for vertical spacing
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(10, 0, 10, 0); // Left, Top, Right, Bottom
            typeTextView.setLayoutParams(params);

            // Add the TextView to the layout
            pokemonTypesLayout.addView(typeTextView);
        }
    }

    private int getTypeColor(String typeName) {
        int colorResourceId = getResources().getIdentifier(typeName, "color", getPackageName());
        if (colorResourceId != 0) {
            return getResources().getColor(colorResourceId);
        } else {
            // Default color if type not found
            return getResources().getColor(R.color.default_card_background);
        }
    }
    private String capitalize(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }
    private void showLoadingIndicator(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }
    private void setupRadarChart(PokemonDetails details) {
        configureChartAppearance();
        RadarData radarData = createRadarData(details);
        configureChartAxes(radarData);
        radarChart.setData(radarData);
        radarChart.invalidate();
    }

    private void configureChartAppearance() {
        radarChart.getDescription().setEnabled(false);
        radarChart.setWebColor(Color.GRAY);
        radarChart.setWebLineWidth(1f);
        radarChart.setWebColorInner(Color.LTGRAY);
        radarChart.setWebLineWidthInner(1f);
        radarChart.setWebAlpha(100);
        radarChart.animateXY(1000, 1000);
    }

    private RadarData createRadarData(PokemonDetails details) {
        List<RadarEntry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        for (PokemonDetails.Stat stat : details.getStats()) {
            entries.add(new RadarEntry(stat.getBaseStat()));
            labels.add(formatStatName(stat.getStat().getName()));
        }
        RadarDataSet dataSet = new RadarDataSet(entries, "");
        dataSet.setColor(Color.BLUE);
        dataSet.setFillColor(Color.parseColor("#5BC0EB"));
        dataSet.setDrawFilled(true);
        dataSet.setFillAlpha(180);
        dataSet.setLineWidth(2f);
        dataSet.setValueTextSize(12f);
        dataSet.setValueTextColor(Color.BLACK);
        RadarData radarData = new RadarData(dataSet);
        radarData.setLabels(labels.toArray(new String[0]));
        return radarData;
    }

    private void configureChartAxes(RadarData radarData) {
        XAxis xAxis = radarChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(radarData.getLabels()));
        xAxis.setTextSize(14f);
        xAxis.setTextColor(Color.BLACK);
        YAxis yAxis = radarChart.getYAxis();
        yAxis.setLabelCount(5, true);
        yAxis.setTextSize(9f);
        yAxis.setAxisMinimum(0f);
        yAxis.setAxisMaximum(calculateYAxisMaximum(radarData));
        yAxis.setDrawLabels(false);
    }

    private float calculateYAxisMaximum(RadarData radarData) {
        float maxStat = 0f;
        for (IRadarDataSet dataSet : radarData.getDataSets()) {
            for (RadarEntry entry : dataSet.getEntriesForXValue(0)) {
                if (entry.getValue() > maxStat) {
                    maxStat = entry.getValue();
                }
            }
        }
        return maxStat * 1.2f; // Add 20% margin for better visualization
    }

    private String formatStatName(String statName) {
        switch (statName) {
            case "hp":
                return "HP";
            case "attack":
                return "Attack";
            case "defense":
                return "Defense";
            case "special-attack":
                return "Sp. Atk";
            case "special-defense":
                return "Sp. Def";
            case "speed":
                return "Speed";
            default:
                return statName;
        }
    }

}
