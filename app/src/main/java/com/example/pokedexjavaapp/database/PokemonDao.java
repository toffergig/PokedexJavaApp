package com.example.pokedexjavaapp.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.pokedexjavaapp.models.PokemonEntity;

import java.util.List;

@Dao
public interface PokemonDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<PokemonEntity> pokemons);

    @Query("SELECT * FROM pokemon_table LIMIT :limit OFFSET :offset")
    List<PokemonEntity> getPokemons(int limit, int offset);

    @Query("SELECT COUNT(*) FROM pokemon_table")
    int getCount();
}
