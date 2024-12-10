package com.example.pokedexjavaapp.models;

public class Pokemon {
    protected String name;
    private String url;

    // Transient fields
    protected int id;
    private String spriteURL;

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    // Extracts ID and sprite URL from the 'url' field
    public void extractIdAndSpriteURL() {
        String[] parts = url.split("/");
        try {
            id = Integer.parseInt(parts[parts.length - 1]);
        } catch (NumberFormatException e) {
            id = -1;
        }
        spriteURL = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/" + id + ".png";
    }

    public int getId() {
        return id;
    }

    public String getSpriteURL() {
        return spriteURL;
    }
}
