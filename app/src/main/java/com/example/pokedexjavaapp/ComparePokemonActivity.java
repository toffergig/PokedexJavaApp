package com.example.pokedexjavaapp;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pokedexjavaapp.api.PokemonApiService;
import com.example.pokedexjavaapp.api.PokemonResponse;
import com.example.pokedexjavaapp.api.RetrofitClient;
import com.example.pokedexjavaapp.models.Pokemon;
import com.example.pokedexjavaapp.models.PokemonDetails;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ComparePokemonActivity extends AppCompatActivity {

    private static final String TAG = "ComparePokemonActivity";
    private static final int PAGE_SIZE = 660; // Number of Pokémon to fetch
    private AutoCompleteTextView autoCompleteTextView;
    private ChipGroup chipGroup;
    private PokemonApiService apiService;
    private ArrayAdapter<String> adapter;
    private List<String> pokemonNames = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.compare_pokemon_activity);

        initViews();
        initApiService();
        loadAllPokemonNames();
    }

    private void initViews() {
        autoCompleteTextView = findViewById(R.id.autoCompleteTextView);
        chipGroup = findViewById(R.id.chipGroup);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, pokemonNames);
        autoCompleteTextView.setAdapter(adapter);

        autoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedName = adapter.getItem(position);
            if (selectedName != null) {
                fetchPokemonDetails(selectedName.toLowerCase(Locale.ROOT));
            }
            autoCompleteTextView.setText("");
        });
    }

    private void initApiService() {
        apiService = RetrofitClient.getPokemonApiService();
    }

    private void loadAllPokemonNames() {
        // Fetch a large list of Pokémon from the API (name + URL only)
        apiService.getPokemonList(PAGE_SIZE, 0).enqueue(new Callback<PokemonResponse>() {
            @Override
            public void onResponse(@NonNull Call<PokemonResponse> call, @NonNull Response<PokemonResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (Pokemon p : response.body().getResults()) {
                        pokemonNames.add(p.getName());
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    Log.e(TAG, "Failed to load Pokémon names");
                }
            }

            @Override
            public void onFailure(@NonNull Call<PokemonResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "API call failed: " + t.getMessage());
            }
        });
    }

    private void fetchPokemonDetails(String name) {
        // Fetch Pokémon details (including base stats) by name
        apiService.getPokemonDetails(Integer.parseInt(name)).enqueue(new Callback<PokemonDetails>() {
            @Override
            public void onResponse(@NonNull Call<PokemonDetails> call, @NonNull Response<PokemonDetails> response) {
                if (response.isSuccessful() && response.body() != null) {
                    PokemonDetails details = response.body();
                    addPokemonChip(details);
                    logBaseStats(details);
                } else {
                    Log.e(TAG, "Failed to get details for: " + name);
                }
            }

            @Override
            public void onFailure(@NonNull Call<PokemonDetails> call, @NonNull Throwable t) {
                Log.e(TAG, "Details API call failed: " + t.getMessage());
            }
        });
    }

    private void addPokemonChip(PokemonDetails details) {
        Chip chip = new Chip(this);
        chip.setText(capitalize(details.getName()));
        chip.setCloseIconVisible(true);
        chip.setChipBackgroundColorResource(android.R.color.darker_gray);
        chip.setTextColor(Color.WHITE);

        // Remove chip on close
        chip.setOnCloseIconClickListener(v -> chipGroup.removeView(chip));

        chipGroup.addView(chip);
    }

    private void logBaseStats(PokemonDetails details) {
        // Log base stats for debugging or comparison
        if (details.getStats() != null) {
            StringBuilder sb = new StringBuilder();
            sb.append("Base stats for ").append(details.getName()).append(": ");
            details.getStats().forEach(stat -> sb.append(stat.getStat().getName())
                    .append(": ")
                    .append(stat.getBaseStat())
                    .append(", "));
            Log.d(TAG, sb.toString());
        }
    }

    private String capitalize(String text) {
        if (text == null || text.isEmpty()) return "";
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }
}
