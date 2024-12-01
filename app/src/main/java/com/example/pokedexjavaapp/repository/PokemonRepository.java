package com.example.pokedexjavaapp.repository;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import com.example.pokedexjavaapp.api.PokemonApiService;
import com.example.pokedexjavaapp.api.PokemonResponse;
import com.example.pokedexjavaapp.api.RetrofitClient;
import com.example.pokedexjavaapp.database.AppDatabase;
import com.example.pokedexjavaapp.database.PokemonDao;
import com.example.pokedexjavaapp.models.PokemonEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PokemonRepository {

    private PokemonApiService apiService;
    private PokemonDao pokemonDao;
    private AtomicBoolean isLoading = new AtomicBoolean(false);
    private ExecutorService executorService;

    public interface PokemonListCallback {
        void onSuccess(List<PokemonEntity> pokemons, int totalCount);
        void onError(Throwable t);
    }

    public PokemonRepository(Context context) {
        apiService = RetrofitClient.getPokemonApiService();
        AppDatabase database = AppDatabase.getInstance(context);
        pokemonDao = database.pokemonDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    public void getPokemonList(int limit, int offset, final PokemonListCallback callback) {
        executorService.execute(() -> {
            List<PokemonEntity> cachedPokemons = pokemonDao.getPokemons(limit, offset);
            int cachedCount = cachedPokemons != null ? cachedPokemons.size() : 0;

            if (cachedCount < limit) {
                // Fetch missing data from API
                int apiLimit = limit - cachedCount;
                int apiOffset = offset + cachedCount;

                fetchFromApi(apiLimit, apiOffset, new PokemonListCallback() {
                    @Override
                    public void onSuccess(List<PokemonEntity> apiPokemons, int apiTotalCount) {
                        // Save new data to database
                        saveToDatabase(apiPokemons);

                        // Combine cached data and API data
                        List<PokemonEntity> combinedPokemons = new ArrayList<>();
                        if (cachedPokemons != null) {
                            combinedPokemons.addAll(cachedPokemons);
                        }
                        combinedPokemons.addAll(apiPokemons);

                        // Return combined data
                        new Handler(Looper.getMainLooper()).post(() -> {
                            callback.onSuccess(combinedPokemons, apiTotalCount);
                        });
                    }

                    @Override
                    public void onError(Throwable t) {
                        // Return cached data if available
                        if (cachedPokemons != null && !cachedPokemons.isEmpty()) {
                            new Handler(Looper.getMainLooper()).post(() -> {
                                callback.onSuccess(cachedPokemons, cachedPokemons.size());
                            });
                        } else {
                            new Handler(Looper.getMainLooper()).post(() -> {
                                callback.onError(t);
                            });
                        }
                    }
                });
            } else {
                // Cached data is sufficient
                new Handler(Looper.getMainLooper()).post(() -> {
                    callback.onSuccess(cachedPokemons, cachedPokemons.size());
                });
            }
        });
    }

    private void fetchFromApi(int limit, int offset, final PokemonListCallback callback) {
        if (!isLoading.compareAndSet(false, true)) {
            return;
        }

        apiService.getPokemonList(limit, offset).enqueue(new Callback<PokemonResponse>() {
            @Override
            public void onResponse(@NonNull Call<PokemonResponse> call, @NonNull Response<PokemonResponse> response) {
                isLoading.set(false);
                if (response.isSuccessful() && response.body() != null) {
                    List<PokemonEntity> pokemonList = response.body().toPokemonEntityList();
                    int totalCount = response.body().getCount();

                    // Return data via callback
                    callback.onSuccess(pokemonList, totalCount);
                } else {
                    callback.onError(new Exception("API call failed with response"));
                }
            }

            @Override
            public void onFailure(@NonNull Call<PokemonResponse> call, @NonNull Throwable t) {
                isLoading.set(false);
                callback.onError(t);
            }
        });
    }

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
