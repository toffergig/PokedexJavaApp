package com.example.pokedexjavaapp.repository;

import android.content.Context;

import androidx.annotation.NonNull;

import com.example.pokedexjavaapp.api.PokemonApiService;
import com.example.pokedexjavaapp.api.PokemonResponse;
import com.example.pokedexjavaapp.api.RetrofitClient;
import com.example.pokedexjavaapp.database.AppDatabase;
import com.example.pokedexjavaapp.database.PokemonDao;
import com.example.pokedexjavaapp.models.PokemonEntity;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.os.Handler;
import android.os.Looper;
public class PokemonRepository {

    private PokemonApiService apiService;
    private PokemonDao pokemonDao;
    private boolean isLoading = false;
    private ExecutorService executorService;

    public interface PokemonListCallback {
        void onSuccess(List<PokemonEntity> pokemons, int totalCount);
        void onError(Throwable t);
    }

    public PokemonRepository(Context context) {
        apiService = RetrofitClient.getPokemonApiService();
        AppDatabase database = AppDatabase.getInstance(context);
        pokemonDao = database.pokemonDao();
        // Initialize ExecutorService
        executorService = Executors.newSingleThreadExecutor();
    }

    public void getPokemonList(int limit, int offset, final PokemonListCallback callback) {
        executorService.execute(() -> {
            List<PokemonEntity> pokemons = pokemonDao.getPokemons(limit, offset);
            int totalCount = pokemonDao.getCount();

            if (pokemons != null && !pokemons.isEmpty()) {
                // Post result back to main thread
                new Handler(Looper.getMainLooper()).post(() -> {
                    callback.onSuccess(pokemons, totalCount);
                });
            } else {
                // Fetch from API
                fetchFromApi(limit, offset, callback);
            }
        });
    }

    private void fetchFromApi(int limit, int offset, final PokemonListCallback callback) {
        if (isLoading) return;
        isLoading = true;

        apiService.getPokemonList(limit, offset).enqueue(new Callback<PokemonResponse>() {
            @Override
            public void onResponse(@NonNull Call<PokemonResponse> call, @NonNull Response<PokemonResponse> response) {
                isLoading = false;
                if (response.isSuccessful() && response.body() != null) {
                    List<PokemonEntity> pokemonList = response.body().toPokemonEntityList();
                    int totalCount = response.body().getCount();

                    // Save to database
                    saveToDatabase(pokemonList);

                    // Return result to callback on the main thread
                    new Handler(Looper.getMainLooper()).post(() -> {
                        callback.onSuccess(pokemonList, totalCount);
                    });

                } else {
                    callback.onError(new Exception("API call failed with response"));
                }
            }

            @Override
            public void onFailure(@NonNull Call<PokemonResponse> call, @NonNull Throwable t) {
                isLoading = false;
                callback.onError(t);
            }
        });
    }


    // Helper class to hold the result
    private static class FetchResult {
        List<PokemonEntity> pokemons;
        int totalCount;

        FetchResult(List<PokemonEntity> pokemons, int totalCount) {
            this.pokemons = pokemons;
            this.totalCount = totalCount;
        }
    }

    // AsyncTask to save data to the database
    private void saveToDatabase(List<PokemonEntity> pokemonList) {
        executorService.execute(() -> {
            pokemonDao.insertAll(pokemonList);
        });
    }

    public void shutdown() {
        if (!executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}