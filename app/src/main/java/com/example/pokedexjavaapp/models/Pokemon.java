package com.example.pokedexjavaapp.models;

import com.google.gson.annotations.SerializedName;

public class Pokemon {
    @SerializedName("name")
    private String name;

    public String getName() {
        return name;
    }
}
