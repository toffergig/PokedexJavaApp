package com.example.pokedexjavaapp.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import com.example.pokedexjavaapp.api.PokemonApiService;
import com.example.pokedexjavaapp.api.PokemonResponse;
import com.example.pokedexjavaapp.api.RetrofitClient;
import com.example.pokedexjavaapp.models.PokemonEntity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
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
    private SharedPreferences sharedPreferences;
    private Gson gson;
    private static final String PREFS_NAME = "pokemon_prefs";
    private static final String POKEMON_LIST_KEY = "pokemon_list_key";
    private AtomicBoolean isLoading = new AtomicBoolean(false);
    private ExecutorService executorService;
    private Context context;
    private static final String POKEMON_PAGE_KEY_PREFIX = "pokemon_page_";

    public interface PokemonListCallback {
        void onSuccess(List<PokemonEntity> pokemons, int totalCount);
        void onError(Throwable t);
    }

    public PokemonRepository(Context context) {
        this.context = context;
        apiService = RetrofitClient.getPokemonApiService();
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
        executorService = Executors.newSingleThreadExecutor();
    }

    public void getPokemonList(int limit, int offset, final PokemonListCallback callback) {
        executorService.execute(() -> {
            List<PokemonEntity> cachedPokemons = getCachedPokemons(limit, offset);
            int cachedCount = cachedPokemons.size();

            if (cachedCount < limit) {
                // Fetch missing data from API
                int apiLimit = limit - cachedCount;
                int apiOffset = offset + cachedCount;

                fetchFromApi(apiLimit, apiOffset, new PokemonListCallback() {
                    @Override
                    public void onSuccess(List<PokemonEntity> apiPokemons, int apiTotalCount) {
                        // Save new data to cache
                        saveToCache(apiPokemons);

                        // Combine cached data and API data
                        List<PokemonEntity> combinedPokemons = new ArrayList<>(cachedPokemons);
                        combinedPokemons.addAll(apiPokemons);

                        // Return combined data
                        new Handler(Looper.getMainLooper()).post(() -> {
                            callback.onSuccess(combinedPokemons, apiTotalCount);
                        });
                    }

                    @Override
                    public void onError(Throwable t) {
                        // Return cached data if available
                        if (!cachedPokemons.isEmpty()) {
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



    private List<PokemonEntity> getCachedPokemons(int limit, int offset) {
        String json = sharedPreferences.getString(POKEMON_LIST_KEY, null);
        if (json != null) {
            Type listType = new TypeToken<List<PokemonEntity>>() {}.getType();
            List<PokemonEntity> allPokemons = gson.fromJson(json, listType);
            int end = Math.min(offset + limit, allPokemons.size());
            if (offset < end) {
                return allPokemons.subList(offset, end);
            }
        }
        return new ArrayList<>();
    }

    private void saveToCache(List<PokemonEntity> newPokemons) {
        executorService.execute(() -> {
            String json = sharedPreferences.getString(POKEMON_LIST_KEY, null);
            List<PokemonEntity> allPokemons;
            if (json != null) {
                Type listType = new TypeToken<List<PokemonEntity>>() {}.getType();
                allPokemons = gson.fromJson(json, listType);
            } else {
                allPokemons = new ArrayList<>();
            }

            // Avoid duplicates
            for (PokemonEntity pokemon : newPokemons) {
                if (!allPokemons.contains(pokemon)) {
                    allPokemons.add(pokemon);
                }
            }

            // Save updated list back to SharedPreferences
            String updatedJson = gson.toJson(allPokemons);
            sharedPreferences.edit().putString(POKEMON_LIST_KEY, updatedJson).apply();
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

//    public void shutdown() {
//        if (!executorService.isShutdown()) {
//            executorService.shutdown();
//        }
//    }

//    private List<PokemonEntity> getCachedPage(int pageNumber) {
//        String json = sharedPreferences.getString(POKEMON_PAGE_KEY_PREFIX + pageNumber, null);
//        if (json != null) {
//            Type listType = new TypeToken<List<PokemonEntity>>() {}.getType();
//            return gson.fromJson(json, listType);
//        }
//        return new ArrayList<>();
//    }
//
//    private void savePageToCache(int pageNumber, List<PokemonEntity> pokemons) {
//        String json = gson.toJson(pokemons);
//        sharedPreferences.edit().putString(POKEMON_PAGE_KEY_PREFIX + pageNumber, json).apply();
//    }
}
