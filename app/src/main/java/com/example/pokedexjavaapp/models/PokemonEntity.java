package com.example.pokedexjavaapp.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "pokemon_table")
public class PokemonEntity {

    @PrimaryKey
    private int id;
    private String name;
    private String spriteURL;

    // Constructors
    public PokemonEntity() {
        // Default constructor required for Room
    }

    public PokemonEntity(int id, String name, String spriteURL) {
        this.id = id;
        this.name = name;
        this.spriteURL = spriteURL;
    }

    // Getters and Setters

    // Getter and Setter for 'id'
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    // Getter and Setter for 'name'
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Getter and Setter for 'spriteURL'
    public String getSpriteURL() {
        return spriteURL;
    }

    public void setSpriteURL(String spriteURL) {
        this.spriteURL = spriteURL;
    }
}
