package com.example.pokedexjavaapp.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PokemonDetails {

    private int id;
    private String name;
    private int height;
    private int weight;

    @SerializedName("sprites")
    private Sprites sprites;

    @SerializedName("stats")
    private List<Stat> stats;

    // Inner classes for nested JSON objects

    public static class Sprites {
        @SerializedName("front_default")
        private String frontDefault;

        public String getFrontDefault() {
            return frontDefault;
        }
    }

    public static class Stat {
        @SerializedName("base_stat")
        private int baseStat;

        private StatDetail stat;

        public int getBaseStat() {
            return baseStat;
        }

        public StatDetail getStat() {
            return stat;
        }
    }

    public static class StatDetail {
        private String name;

        public String getName() {
            return name;
        }
    }

    // Getters for main fields

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getHeight() {
        return height;
    }

    public int getWeight() {
        return weight;
    }

    public Sprites getSprites() {
        return sprites;
    }

    public List<Stat> getStats() {
        return stats;
    }
}
