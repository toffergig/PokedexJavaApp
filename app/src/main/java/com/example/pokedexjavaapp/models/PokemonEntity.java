package com.example.pokedexjavaapp.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Objects;

public class PokemonEntity implements Serializable {
    private int id;
    private String name;

    @SerializedName("spriteURL")
    private String spriteURL;

    // Constructor
    public PokemonEntity(int id, String name, String spriteURL) {
        this.id = id;
        this.name = name;
        this.spriteURL = spriteURL;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getSpriteURL() { return spriteURL; }

    // Override equals and hashCode for proper comparison
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PokemonEntity that = (PokemonEntity) o;

        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
