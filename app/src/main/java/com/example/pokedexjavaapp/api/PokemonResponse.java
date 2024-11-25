package com.example.pokedexjavaapp.api;

import com.example.pokedexjavaapp.models.Pokemon;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PokemonResponse {
    @SerializedName("results")
    private List<Pokemon> results;

    public List<Pokemon> getResults() {
        return results;
    }
}