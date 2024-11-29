package com.example.pokedexjavaapp.api;

import com.example.pokedexjavaapp.models.Pokemon;
import com.example.pokedexjavaapp.models.PokemonEntity;

import java.util.ArrayList;
import java.util.List;

public class PokemonResponse {
    private int count;
    private String next;
    private String previous;
    private List<Pokemon> results;

    public int getCount() {
        return count;
    }

    public List<Pokemon> getResults() {
        return results;
    }

    // Convert API response to a list of PokemonEntity
    public List<PokemonEntity> toPokemonEntityList() {
        List<PokemonEntity> pokemonEntities = new ArrayList<>();
        for (Pokemon pokemon : results) {
            pokemon.extractIdAndSpriteURL();
            PokemonEntity entity = new PokemonEntity(pokemon.getId(), pokemon.getName(), pokemon.getSpriteURL());
            pokemonEntities.add(entity);
        }
        return pokemonEntities;
    }

}
