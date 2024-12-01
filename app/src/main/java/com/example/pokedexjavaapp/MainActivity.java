package com.example.pokedexjavaapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pokedexjavaapp.models.PokemonEntity;
import com.example.pokedexjavaapp.models.PokemonAdapter;
import com.example.pokedexjavaapp.repository.PokemonRepository;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private RecyclerView pokemonRecyclerView;
    private PokemonAdapter pokemonAdapter;
    private List<PokemonEntity> pokemonList = new ArrayList<>();
    private ProgressBar progressBar;

    private PokemonRepository pokemonRepository;
    private EndlessRecyclerViewScrollListener scrollListener;
    private GridLayoutManager gridLayoutManager;

    private static final int PAGE_SIZE = 24; // Number of items to load per page
    private int offset = 0; // Current offset
    //private int totalItemCount = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize ProgressBar
        progressBar = findViewById(R.id.progress_bar);

        // Initialize RecyclerView
        pokemonRecyclerView = findViewById(R.id.pokemon_recycler_view);
        gridLayoutManager = new GridLayoutManager(this, 2);
        pokemonRecyclerView.setLayoutManager(gridLayoutManager);

        // Initialize repository
        pokemonRepository = new PokemonRepository(getApplicationContext());

        // Initialize adapter
        pokemonAdapter = new PokemonAdapter(pokemonList, pokemon -> {
            Intent intent = new Intent(MainActivity.this, PokemonDetailsActivity.class);
            intent.putExtra("pokemon_id", pokemon.getId());
            intent.putExtra("pokemon_name", pokemon.getName());
            intent.putExtra("pokemon_sprite_url", pokemon.getSpriteURL());
            startActivity(intent);
        });

        pokemonRecyclerView.setAdapter(pokemonAdapter);

        // Initialize the scroll listener
        scrollListener = new EndlessRecyclerViewScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                fetchPokemonData(offset);
            }
        };

        pokemonRecyclerView.addOnScrollListener(scrollListener);

        fetchPokemonData(offset);
    }

    private void fetchPokemonData(int offset) {
        showLoadingIndicator(true);

        pokemonRepository.getPokemonList(PAGE_SIZE, offset, new PokemonRepository.PokemonListCallback() {
            @Override
            public void onSuccess(List<PokemonEntity> pokemons, int totalCount) {
                showLoadingIndicator(false);

                // If we received no new data, we've reached the end
                if (pokemons.isEmpty()) {
                    pokemonRecyclerView.removeOnScrollListener(scrollListener);
                    return;
                }

                // Add new Pokémon to the list without duplicates
                int initialSize = pokemonList.size();
                for (PokemonEntity pokemon : pokemons) {
                    if (!pokemonList.contains(pokemon)) {
                        pokemonList.add(pokemon);
                    }
                }

                pokemonAdapter.notifyItemRangeInserted(initialSize, pokemonList.size() - initialSize);

                // Update offset based on the number of new Pokémon received
                MainActivity.this.offset += pokemons.size();
            }

            @Override
            public void onError(Throwable t) {
                showLoadingIndicator(false);
                Log.e(TAG, "API call failed: " + t.getMessage());
            }
        });
    }

    private void showLoadingIndicator(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Shutdown the ExecutorService
        if (pokemonRepository != null) {
            pokemonRepository.shutdown();
        }
    }
}
