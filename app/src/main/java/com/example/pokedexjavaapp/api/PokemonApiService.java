package com.example.pokedexjavaapp.api;

import com.example.pokedexjavaapp.models.PokemonDetails;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface PokemonApiService {
    @GET("pokemon")
    Call<PokemonResponse> getPokemonList(
            @Query("limit") int limit,
            @Query("offset") int offset
    );

    @GET("pokemon/{id}")
    Call<PokemonDetails> getPokemonDetails(@Path("id") int id);
}