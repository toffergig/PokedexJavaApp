package com.example.pokedexjavaapp.repository;

import android.util.Log;
import androidx.annotation.NonNull;

import com.example.pokedexjavaapp.api.PokemonApiService;
import com.example.pokedexjavaapp.api.PokemonResponse;
import com.example.pokedexjavaapp.api.RetrofitClient;
import com.example.pokedexjavaapp.models.Pokemon;

import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PokemonRepository {

    private static final String TAG = "PokemonRepository";

    public interface PokemonListCallback {
        void onSuccess(List<Pokemon> pokemons);
        void onError(Throwable t);
    }

    public void getPokemonList(final PokemonListCallback callback) {
        PokemonApiService apiService = RetrofitClient.getPokemonApiService();

        apiService.getPokemonList().enqueue(new Callback<PokemonResponse>() {
            @Override
            public void onResponse(@NonNull Call<PokemonResponse> call, @NonNull Response<PokemonResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Pokemon> pokemonList = response.body().getResults();
                    callback.onSuccess(pokemonList);
                } else {
                    Log.e(TAG, "API call failed with response: " + response.errorBody());
                    callback.onError(new Exception("API call failed with response"));
                }
            }

            @Override
            public void onFailure(@NonNull Call<PokemonResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "API call failed: " + t.getMessage());
                callback.onError(t);
            }
        });
    }
}
