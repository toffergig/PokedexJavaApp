package com.example.pokedexjavaapp.api;

import com.example.pokedexjavaapp.models.Pokemon;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PokemonResponse {

    private final int count;
    private final String next;
    private final String previous;
    private final List<Pokemon> results;

    public PokemonResponse(int count, String next, String previous, List<Pokemon> results) {
        this.count = count;
        this.next = next;
        this.previous = previous;
        this.results = results;
    }

    // Getters and setters
    public int getCount() {
        return count;
    }
    public String getNext() {
        return next;
    }
    public String getPrevious() {
        return previous;
    }
    public List<Pokemon> getResults() {
        return results;
    }
}