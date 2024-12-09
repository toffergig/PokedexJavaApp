package com.example.pokedexjavaapp;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.pokedexjavaapp.api.PokemonApiService;
import com.example.pokedexjavaapp.api.RetrofitClient;
import com.example.pokedexjavaapp.helpers.RadarChartConfig;
import com.example.pokedexjavaapp.helpers.RadarChartHelper;
import com.example.pokedexjavaapp.models.PokemonDetails;
import com.github.mikephil.charting.charts.RadarChart;

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
    private LinearLayout baseStatsLayout; // Added

    // RadarChart Helper
    private RadarChartHelper radarChartHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pokemon_details_activity);
        initializeViews();
        initializeHelpers();
        setupActionBar();
        int pokemonId = getIntent().getIntExtra("pokemon_id", -1);
        if (pokemonId != -1) {
            fetchPokemonDetails(pokemonId);
        } else {
            Log.e(TAG, "Invalid Pokémon ID");
            Toast.makeText(this, "Invalid Pokémon selected.", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity since there's an invalid ID
        }
    }

    /**
     * Initialize UI components by binding them to their XML counterparts.
     */
    private void initializeViews() {
        pokemonNameTextView = findViewById(R.id.pokemon_detail_name);
        pokemonImageView = findViewById(R.id.pokemon_detail_image);
        pokemonIdTextView = findViewById(R.id.pokemon_detail_id);
        radarChart = findViewById(R.id.radar_chart);
        progressBar = findViewById(R.id.progress_bar);
        pokemonTypesLayout = findViewById(R.id.pokemon_types_layout); // Added
        pokemonAbilitiesLayout = findViewById(R.id.pokemon_abilities_layout); // Added
        baseStatsLayout = findViewById(R.id.base_stats_layout); // Added
    }

    /**
     * Setup the app bar with a back button.
     */
    private void setupActionBar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Enable the back button
            getSupportActionBar().setTitle("Pokémon Details");    // Set ActionBar title
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
     * Initialize helper classes for managing RadarChart configurations.
     */
    private void initializeHelpers() {
        radarChartHelper = new RadarChartHelper(this, radarChart);
    }

    /**
     * Fetch details for the given Pokémon ID asynchronously.
     *
     * @param pokemonId The ID of the Pokémon to fetch.
     */
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
                    Toast.makeText(PokemonDetailsActivity.this, "Failed to load Pokémon details.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<PokemonDetails> call, @NonNull Throwable t) {
                showLoadingIndicator(false);
                Log.e(TAG, "API call failed: " + t.getMessage());
                Toast.makeText(PokemonDetailsActivity.this, "Error loading Pokémon details.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Displays the Pokémon details on the UI.
     *
     * @param details The PokémonDetails object containing data.
     */
    private void displayPokemonDetails(PokemonDetails details) {
        displayPokemonBasicInfo(details);
        displayPokemonTypes(details.getTypes());
        displayPokemonAbilities(details.getAbilities());
        setupRadarChart(details);
        displayBaseStats(details.getStats()); // Added
    }

    /**
     * Displays the base stats using horizontal progress bars.
     *
     * @param stats List of Stat objects representing Pokémon base stats.
     */
    private void displayBaseStats(List<PokemonDetails.Stat> stats) {
        // Clear any existing views to avoid duplication
        baseStatsLayout.removeAllViews();

        for (PokemonDetails.Stat statInfo : stats) {
            String statName = capitalize(statInfo.getStat().getName().replace("-", " "));
            int statValue = statInfo.getBaseStat();

            // Create a vertical LinearLayout for each stat
            LinearLayout statItemLayout = new LinearLayout(this);
            statItemLayout.setOrientation(LinearLayout.VERTICAL);
            statItemLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            statItemLayout.setPadding(0, 8, 0, 8);

            // Create a horizontal LinearLayout for stat name and value
            LinearLayout statHeaderLayout = new LinearLayout(this);
            statHeaderLayout.setOrientation(LinearLayout.HORIZONTAL);
            statHeaderLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            statHeaderLayout.setGravity(Gravity.CENTER_VERTICAL);

            // Stat Name TextView
            TextView statNameTextView = new TextView(this);
            statNameTextView.setText(statName);
            statNameTextView.setTextSize(16f);
            statNameTextView.setTypeface(null, Typeface.BOLD);
            statNameTextView.setLayoutParams(new LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f));

            // Stat Value TextView
            TextView statValueTextView = new TextView(this);
            statValueTextView.setText(String.format("%s/300", statValue));
            statValueTextView.setTextSize(16f);
            statValueTextView.setTypeface(null, Typeface.NORMAL);
            statValueTextView.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));

            // Add TextViews to the header layout
            statHeaderLayout.addView(statNameTextView);
            statHeaderLayout.addView(statValueTextView);

            // Create ProgressBar
            ProgressBar statProgressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
            statProgressBar.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    20));
            statProgressBar.setMax(255); // Pokémon base stats typically max out at 255
            statProgressBar.setProgress(statValue);
//            statProgressBar.setBackgroundColor(ContextCompat.getColor(this, R.color.progress_bar_color));
          //  statProgressBar.setProgressTint(getTypeColor("progress_bar_color")); // Optional: Define a color in colors.xml

            // Add header and progress bar to the stat item layout
            statItemLayout.addView(statHeaderLayout);
            statItemLayout.addView(statProgressBar);

            // Add the stat item layout to the base stats container
            baseStatsLayout.addView(statItemLayout);
        }
    }

    /**
     * Displays basic Pokémon information: ID, name, and image.
     *
     * @param details The PokémonDetails object containing basic info.
     */
    private void displayPokemonBasicInfo(PokemonDetails details) {
        String formattedId = String.format(Locale.getDefault(), "#%03d", details.getId());
        pokemonIdTextView.setText(formattedId);
        pokemonNameTextView.setText(capitalize(details.getName()));
        String gifUrl = details.getSprites().getOther().getShowdown().getFrontDefault();
        if (gifUrl != null && !gifUrl.isEmpty()) {
            Glide.with(this)
                    .asGif()
                    .load(gifUrl)
                    .into(pokemonImageView);
        }
    }

    /**
     * Displays the Pokémon abilities.
     *
     * @param abilities List of AbilityInfo objects representing Pokémon abilities.
     */
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
                abilityTextView.setText(String.format("%s (Hidden)", abilityTextView.getText()));
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

    /**
     * Displays the Pokémon types.
     *
     * @param types List of TypeInfo objects representing Pokémon types.
     */
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
            typeTextView.setTypeface(null, Typeface.BOLD);
            typeTextView.setPadding(16, 8, 16, 8);

            // Set background with rounded corners and specific color
            GradientDrawable bgDrawable = new GradientDrawable();
            bgDrawable.setShape(GradientDrawable.RECTANGLE);
            bgDrawable.setCornerRadius(16f);
            bgDrawable.setColor(getTypeColor(typeName));
            typeTextView.setBackground(bgDrawable);

            // Set margins for horizontal spacing
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

    /**
     * Sets up the RadarChart using RadarChartHelper.
     *
     * @param details The PokémonDetails object containing stats.
     */
    private void setupRadarChart(PokemonDetails details) {
        List<PokemonDetails> detailsList = new ArrayList<>();
        detailsList.add(details);

        // Create RadarChartConfig based on activity requirements
        // For PokemonDetailsActivity, legends and labels are not needed
        RadarChartConfig config = new RadarChartConfig(false, false);

        // Build the RadarChart with the provided configuration
        radarChartHelper.buildRadarChart(detailsList, config);
    }

    /**
     * Shows or hides the loading indicator.
     *
     * @param show True to show the progress bar, false to hide.
     */
    private void showLoadingIndicator(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    /**
     * Capitalizes the first letter of each word in a given string.
     *
     * @param text The input string.
     * @return The capitalized string.
     */
    private String capitalize(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        String[] words = text.split(" ");
        StringBuilder capitalized = new StringBuilder();
        for (String word : words) {
            if(!word.isEmpty()){
                capitalized.append(word.substring(0, 1).toUpperCase())
                        .append(word.substring(1))
                        .append(" ");
            }
        }
        return capitalized.toString().trim();
    }

    /**
     * Retrieves the color associated with a Pokémon type or a default color.
     *
     * @param typeName The name of the Pokémon type or a predefined key.
     * @return The color integer.
     */
    private int getTypeColor(String typeName) {
        // Assuming you have defined a color named 'progress_bar_color' in colors.xml
        int colorResourceId = getResources().getIdentifier(typeName, "color", getPackageName());
        if (colorResourceId != 0) {
            return getResources().getColor(colorResourceId);
        } else {
            // Default color if type not found
            return getResources().getColor(R.color.default_card_background);
        }
    }
}
