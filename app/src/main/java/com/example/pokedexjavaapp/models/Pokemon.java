package com.example.pokedexjavaapp.models;

import com.google.gson.annotations.SerializedName;

public class Pokemon {
    private String name;
    private String url;
    private String spriteURL;

    // Getters
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

    public void extractSpriteURL() {
        // Assuming the URL format is: https://pokeapi.co/api/v2/pokemon/{id or name}/
        String[] parts = url.split("/");
        String idOrName = parts[parts.length - 1];
        spriteURL = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/" + idOrName + ".png";
    }
}
