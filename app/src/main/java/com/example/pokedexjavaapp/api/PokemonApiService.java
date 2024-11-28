package com.example.pokedexjavaapp.api;

import retrofit2.Call;
import retrofit2.http.GET;

public interface PokemonApiService {
    @GET("pokemon?limit=100")
    Call<PokemonResponse> getPokemonList();
}