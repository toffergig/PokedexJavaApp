package com.example.pokedexjavaapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.splashscreen.SplashScreen;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.pokedexjavaapp.models.PokemonAdapter;
import com.example.pokedexjavaapp.models.PokemonEntity;
import com.example.pokedexjavaapp.repository.PokemonRepository;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // Constants
    private static final String TAG = "MainActivity";
    private static final int PAGE_SIZE = 20;
    private static final int GRID_COLUMN_COUNT = 2;

    // UI Components
    private RecyclerView pokemonRecyclerView;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SearchView searchView;
    private Button backToTopButton;

    // Data Components
    private PokemonAdapter pokemonAdapter;
    private PokemonRepository pokemonRepository;
    private EndlessRecyclerViewScrollListener paginationScrollListener;
    private RecyclerView.OnScrollListener backToTopScrollListener;

    // State Variables
    private int offset = 0;
    private boolean isDataReady = false;
    private List<PokemonEntity> allPokemons = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        splashScreen.setKeepOnScreenCondition(() -> !isDataReady);

        setContentView(R.layout.activity_main);

        initializeUIComponents();
        initializeDataComponents();
        prefetchPokemonData();
    }

    private void initializeUIComponents() {
        progressBar = findViewById(R.id.progress_bar);

        searchView = findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                filterPokemonList(query.trim());
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterPokemonList(newText.trim());
                return true;
            }
        });

        backToTopButton = findViewById(R.id.back_to_top_button);
        backToTopButton.setOnClickListener(v -> scrollToTop());
        backToTopButton.setVisibility(View.GONE);

        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this::refreshPokemonData);

        pokemonRecyclerView = findViewById(R.id.pokemon_recycler_view);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, GRID_COLUMN_COUNT);
        pokemonRecyclerView.setLayoutManager(gridLayoutManager);

        pokemonAdapter = new PokemonAdapter(new ArrayList<>(), this::openPokemonDetails);
        pokemonRecyclerView.setAdapter(pokemonAdapter);

        // Initialize pagination scroll listener
        paginationScrollListener = new EndlessRecyclerViewScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                fetchPokemonData();
            }
        };
        pokemonRecyclerView.addOnScrollListener(paginationScrollListener);

        // Add Back to Top scroll listener
        addBackToTopScrollListener();
    }

    private void addBackToTopScrollListener() {
        backToTopScrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null) {
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
                    if (firstVisibleItemPosition > 0) {
                        // Show the button
                        if (backToTopButton.getVisibility() != View.VISIBLE) {
                            backToTopButton.setVisibility(View.VISIBLE);
                        }
                    } else {
                        // Hide the button
                        if (backToTopButton.getVisibility() != View.GONE) {
                            backToTopButton.setVisibility(View.GONE);
                        }
                    }
                }
            }
        };
        pokemonRecyclerView.addOnScrollListener(backToTopScrollListener);
    }

    private void scrollToTop() {
        pokemonRecyclerView.smoothScrollToPosition(0);
    }

    private void initializeDataComponents() {
        pokemonRepository = new PokemonRepository(getApplicationContext());
    }

    private void prefetchPokemonData() {
        showLoadingIndicator(true);
        pokemonRepository.getPokemonList(PAGE_SIZE, offset, new PokemonRepository.PokemonListCallback() {
            @Override
            public void onSuccess(List<PokemonEntity> pokemons, int totalCount) {
                isDataReady = true;
                offset += pokemons.size();
                allPokemons.addAll(pokemons);
                pokemonAdapter.updatePokemonList(pokemons);
                showLoadingIndicator(false);
            }

            @Override
            public void onError(Throwable t) {
                isDataReady = true;
                Log.e(TAG, "Prefetch API call failed: " + t.getMessage());
                showLoadingIndicator(false);
            }
        });
    }

    private void fetchPokemonData() {
        if (!swipeRefreshLayout.isRefreshing()) {
            showLoadingIndicator(true);
        }

        pokemonRepository.getPokemonList(PAGE_SIZE, offset, new PokemonRepository.PokemonListCallback() {
            @Override
            public void onSuccess(List<PokemonEntity> pokemons, int totalCount) {
                if (!swipeRefreshLayout.isRefreshing()) {
                    showLoadingIndicator(false);
                } else {
                    swipeRefreshLayout.setRefreshing(false);
                }

                if (pokemons.isEmpty()) {
                    pokemonRecyclerView.removeOnScrollListener(paginationScrollListener);
                    return;
                }

                offset += pokemons.size();
                allPokemons.addAll(pokemons);
                pokemonAdapter.updatePokemonList(pokemons);
            }

            @Override
            public void onError(Throwable t) {
                if (!swipeRefreshLayout.isRefreshing()) {
                    showLoadingIndicator(false);
                } else {
                    swipeRefreshLayout.setRefreshing(false);
                }
                Log.e(TAG, "API call failed: " + t.getMessage());
            }
        });
    }

    private void refreshPokemonData() {
        offset = 0;
        allPokemons.clear();
        pokemonAdapter.clearPokemonList();
        // Remove pagination scroll listener
        pokemonRecyclerView.removeOnScrollListener(paginationScrollListener);
        // Re-initialize pagination scroll listener
        paginationScrollListener = new EndlessRecyclerViewScrollListener((GridLayoutManager) pokemonRecyclerView.getLayoutManager()) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                fetchPokemonData();
            }
        };
        pokemonRecyclerView.addOnScrollListener(paginationScrollListener);
        // Back to Top scroll listener remains attached
        fetchPokemonData();
    }

    private void filterPokemonList(String query) {
        if (query.isEmpty()) {
            pokemonAdapter.setPokemonList(new ArrayList<>(allPokemons));
            // Remove and re-add pagination scroll listener
            pokemonRecyclerView.removeOnScrollListener(paginationScrollListener);
            paginationScrollListener = new EndlessRecyclerViewScrollListener((GridLayoutManager) pokemonRecyclerView.getLayoutManager()) {
                @Override
                public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                    fetchPokemonData();
                }
            };
            pokemonRecyclerView.addOnScrollListener(paginationScrollListener);
            return;
        }

        List<PokemonEntity> filteredList = new ArrayList<>();
        for (PokemonEntity pokemon : allPokemons) {
            if (pokemon.getName().toLowerCase().contains(query.toLowerCase())
                    || String.valueOf(pokemon.getId()).contains(query)) {
                filteredList.add(pokemon);
            }
        }

        pokemonAdapter.setPokemonList(filteredList);
        // Remove pagination scroll listener when filtering
        pokemonRecyclerView.removeOnScrollListener(paginationScrollListener);
        // Back to Top scroll listener remains attached
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
