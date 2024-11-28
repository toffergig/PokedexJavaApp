package com.example.pokedexjavaapp;

import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pokedexjavaapp.models.Pokemon;
import com.example.pokedexjavaapp.models.PokemonAdapter;
import com.example.pokedexjavaapp.repository.PokemonRepository;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private RecyclerView pokemonRecyclerView;
    private PokemonAdapter pokemonAdapter;
    private List<Pokemon> pokemonList = new ArrayList<>();

    private PokemonRepository pokemonRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize RecyclerView
        pokemonRecyclerView = findViewById(R.id.pokemon_recycler_view);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        pokemonRecyclerView.setLayoutManager(gridLayoutManager);
        pokemonAdapter = new PokemonAdapter(pokemonList);
        pokemonRecyclerView.setAdapter(pokemonAdapter);

        // Fetch data
        pokemonRepository = new PokemonRepository();
        fetchPokemonData();
    }

    private void fetchPokemonData() {
        pokemonRepository.getPokemonList(new PokemonRepository.PokemonListCallback() {
            @Override
            public void onSuccess(List<Pokemon> pokemons) {
                pokemonAdapter.updatePokemonList(pokemons);
            }

            @Override
            public void onError(Throwable t) {
                Log.e(TAG, "API call failed: " + t.getMessage());
            }
        });
    }
}