package com.example.pokedexjavaapp.models;

import com.google.gson.annotations.SerializedName;

public class Pokemon {
    private String name;
    private String url;
    private String spriteURL;
    private int id; // Add an ID field

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public String getUrl() {
        return url;
    }
    public String getSpriteURL() {
        return spriteURL;
    }
    public void setSpriteURL(String spriteURL) {
        this.spriteURL = spriteURL;
    }

    public void extractIdAndSpriteURL() {
        // Assuming the URL format is: https://pokeapi.co/api/v2/pokemon/{id}/
        String[] parts = url.split("/");
        try {
            id = Integer.parseInt(parts[parts.length - 1]); // Extract ID
        } catch (NumberFormatException e) {
            // Handle the case where the ID is not a number
            id = -1; // Or some other default value
        }
        spriteURL = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/" + id + ".png";
    }
}
