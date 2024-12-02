package com.example.pokedexjavaapp.database;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

import com.example.pokedexjavaapp.models.PokemonEntity;

@Database(entities = {PokemonEntity.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract PokemonDao pokemonDao();

    private static AppDatabase instance;

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "pokemon_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
