package com.example.pokedexjavaapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.pokedexjavaapp.api.PokemonApiService;
import com.example.pokedexjavaapp.api.PokemonResponse;
import com.example.pokedexjavaapp.api.RetrofitClient;
import com.example.pokedexjavaapp.models.Pokemon;
import com.example.pokedexjavaapp.models.PokemonDetails;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ComparePokemonActivity extends AppCompatActivity {

    private AutoCompleteTextView autoCompleteTextView;
    private static final String TAG = "ComparePokemonActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.compare_pokemon_activity);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        autoCompleteTextView = findViewById(R.id.autoCompleteTextView);

        // Fetch Pokemon names and base stats
        fetchPokemonData();
    }

    private void fetchPokemonData() {
//        showLoadingIndicator(true);
        PokemonApiService apiService = RetrofitClient.getPokemonApiService();
        // Fetch the first 1000 Pokemon (adjust limit as needed)
        apiService.getPokemonList(1000, 0).enqueue(new Callback<PokemonResponse>() {
            @Override
            public void onResponse(@NonNull Call<PokemonResponse> call, @NonNull Response<PokemonResponse> response) {
//                showLoadingIndicator(false);
                if (response.isSuccessful() && response.body() != null) {
                    List<Pokemon> pokemonList = response.body().getResults();
                    List<String> pokemonNames = new ArrayList<>();
                    for (Pokemon pokemon : pokemonList) {
                        pokemonNames.add(pokemon.getName());
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            ComparePokemonActivity.this,
                            android.R.layout.simple_dropdown_item_1line,
                            pokemonNames
                    );
                    autoCompleteTextView.setAdapter(adapter);
                } else {
                    Log.e(TAG, "Failed to get Pokémon list");
                }
            }

            @Override
            public void onFailure(@NonNull Call<PokemonResponse> call, @NonNull Throwable t) {
//                showLoadingIndicator(false);
                Log.e(TAG, "API call failed: " + t.getMessage());
            }
        });
    }
}