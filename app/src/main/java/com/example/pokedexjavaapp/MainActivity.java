package com.example.pokedexjavaapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pokedexjavaapp.models.PokemonEntity;
import com.example.pokedexjavaapp.models.PokemonAdapter;
import com.example.pokedexjavaapp.repository.PokemonRepository;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // Constants
    private static final String TAG = "MainActivity";
    private static final int PAGE_SIZE = 10;
    private static final int GRID_COLUMN_COUNT = 2;

    // UI Components
    private RecyclerView pokemonRecyclerView;
    private ProgressBar progressBar;

    // Data Components
    private PokemonAdapter pokemonAdapter;
    private PokemonRepository pokemonRepository;
    private EndlessRecyclerViewScrollListener scrollListener;

    // State Variables
    private int offset = 0;
    private boolean isDataReady = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Show splash screen and prefetch data
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        splashScreen.setKeepOnScreenCondition(() -> !isDataReady);

        setContentView(R.layout.activity_main);

        initializeUIComponents();
        initializeDataComponents();
        prefetchPokemonData();
    }

    private void prefetchPokemonData() {
        pokemonRepository = new PokemonRepository(getApplicationContext());
        pokemonRepository.getPokemonList(PAGE_SIZE, offset, new PokemonRepository.PokemonListCallback() {
            @Override
            public void onSuccess(List<PokemonEntity> pokemons, int totalCount) {
                isDataReady = true;
                offset += pokemons.size();

                // Update the RecyclerView adapter with the prefetched data
                pokemonAdapter.updatePokemonList(pokemons);
            }

            @Override
            public void onError(Throwable t) {
                isDataReady = true; // Let the app continue even if there's an error
                Log.e(TAG, "Prefetch API call failed: " + t.getMessage());
            }
        });
    }

    private void initializeUIComponents() {
        // Initialize ProgressBar
        progressBar = findViewById(R.id.progress_bar);

        // Initialize RecyclerView
        pokemonRecyclerView = findViewById(R.id.pokemon_recycler_view);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, GRID_COLUMN_COUNT);
        pokemonRecyclerView.setLayoutManager(gridLayoutManager);

        // Initialize Adapter with an empty list
        pokemonAdapter = new PokemonAdapter(new ArrayList<>(), this::openPokemonDetails);
        pokemonRecyclerView.setAdapter(pokemonAdapter);

        // Initialize the scroll listener
        scrollListener = new EndlessRecyclerViewScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                fetchPokemonData();
            }
        };
        pokemonRecyclerView.addOnScrollListener(scrollListener);
    }

    private void initializeDataComponents() {
        pokemonRepository = new PokemonRepository(getApplicationContext());
    }

    private void fetchPokemonData() {
        showLoadingIndicator(true);

        pokemonRepository.getPokemonList(PAGE_SIZE, offset, new PokemonRepository.PokemonListCallback() {
            @Override
            public void onSuccess(List<PokemonEntity> pokemons, int totalCount) {
                showLoadingIndicator(false);

                if (pokemons.isEmpty()) {
                    pokemonRecyclerView.removeOnScrollListener(scrollListener);
                    return;
                }

                // Update the adapter with new data
                pokemonAdapter.updatePokemonList(pokemons);

                // Update offset for the next data fetch
                offset += pokemons.size();
            }

            @Override
            public void onError(Throwable t) {
                showLoadingIndicator(false);
                Log.e(TAG, "API call failed: " + t.getMessage());
            }
        });
    }

    private void openPokemonDetails(PokemonEntity pokemon) {
        Intent intent = new Intent(MainActivity.this, PokemonDetailsActivity.class);
        intent.putExtra("pokemon_id", pokemon.getId());
        intent.putExtra("pokemon_name", pokemon.getName());
        intent.putExtra("pokemon_sprite_url", pokemon.getSpriteURL());
        startActivity(intent);
    }

    private void showLoadingIndicator(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }
}