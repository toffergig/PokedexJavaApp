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
    private static final int PAGE_SIZE = 660;
    private static final int GRID_COLUMN_COUNT = 2;
    private static final int MAX_SELECTION = 3;

    // UI Components
    private RecyclerView pokemonRecyclerView;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SearchView searchView;
    private Button backToTopButton;
    private Button compareButton;

    // Data Components
    private PokemonAdapter pokemonAdapter;
    private PokemonRepository pokemonRepository;
    private EndlessRecyclerViewScrollListener paginationScrollListener;
    private RecyclerView.OnScrollListener backToTopScrollListener;

    // State Variables
    private int offset = 0;
    private boolean isDataReady = false;
    private List<PokemonEntity> allPokemons = new ArrayList<>();

    // Selection Mode Variables
    private boolean selectionMode = false;
    private List<PokemonEntity> selectedPokemons = new ArrayList<>();

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

        compareButton = findViewById(R.id.compare_button);
        compareButton.setOnClickListener(v -> openComparePokemonActivity());
        compareButton.setEnabled(false); // Disabled until selection occurs

        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this::refreshPokemonData);

        pokemonRecyclerView = findViewById(R.id.pokemon_recycler_view);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, GRID_COLUMN_COUNT);
        pokemonRecyclerView.setLayoutManager(gridLayoutManager);

        pokemonAdapter = new PokemonAdapter(new ArrayList<>());
        pokemonAdapter.setOnItemClickListener(this::onPokemonShortClick);
        pokemonAdapter.setOnItemLongClickListener(this::onPokemonLongClick);
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
                    backToTopButton.setVisibility(firstVisibleItemPosition > 0 ? View.VISIBLE : View.GONE);
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

    private void openComparePokemonActivity() {
        Intent intent = new Intent(MainActivity.this, ComparePokemonActivity.class);
        startActivity(intent);
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
            reinitializePaginationListener();
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

    private void reinitializePaginationListener() {
        pokemonRecyclerView.removeOnScrollListener(paginationScrollListener);
        paginationScrollListener = new EndlessRecyclerViewScrollListener((GridLayoutManager) pokemonRecyclerView.getLayoutManager()) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                fetchPokemonData();
            }
        };
        pokemonRecyclerView.addOnScrollListener(paginationScrollListener);
    }

    // Handle short click on a Pokemon
    private void onPokemonShortClick(PokemonEntity pokemon) {
        if (selectionMode) {
            togglePokemonSelection(pokemon);
        } else {
            openPokemonDetails(pokemon);
        }
    }

    // Handle long click on a Pokemon
    private void onPokemonLongClick(PokemonEntity pokemon) {
        if (!selectionMode) {
            // Enable selection mode and select the Pokémon
            selectionMode = true;
            togglePokemonSelection(pokemon);
        } else {
            // Already in selection mode, just toggle selection as usual
            togglePokemonSelection(pokemon);
        }
    }

    private void togglePokemonSelection(PokemonEntity pokemon) {
        if (selectedPokemons.contains(pokemon)) {
            // Deselect if currently selected
            selectedPokemons.remove(pokemon);
        } else {
            // Select if not already selected, provided we haven't reached max limit
            if (selectedPokemons.size() < MAX_SELECTION) {
                selectedPokemons.add(pokemon);
            } else {
                Log.d(TAG, "Maximum of " + MAX_SELECTION + " Pokémon can be selected.");
                return; // Don't add more
            }
        }
        pokemonAdapter.setSelectedPokemons(selectedPokemons);
        updateCompareButtonState();
    }

    private void updateCompareButtonState() {
            boolean hasSelection = !selectedPokemons.isEmpty();
            compareButton.setEnabled(hasSelection);

            // If no Pokémon are selected, turn off selection mode
            if (!hasSelection) {
                selectionMode = false;
            }
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
