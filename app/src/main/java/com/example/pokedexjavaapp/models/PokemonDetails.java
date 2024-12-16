package com.example.pokedexjavaapp.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PokemonDetails extends Pokemon  {

    public PokemonDetails(int id, String name, int height, int weight, Sprites sprites, List<Stat> stats, List<TypeInfo> types, List<AbilityInfo> abilities) {
        this.height = height;
        this.weight = weight;
        this.sprites = sprites;
        this.stats = stats;
        this.types = types;
        this.abilities = abilities;
    }


    private int height;
    private int weight;

    @SerializedName("sprites")
    private Sprites sprites;

    @SerializedName("stats")
    private List<Stat> stats;
    @SerializedName("types")
    private List<TypeInfo> types;
    @SerializedName("abilities")
    private List<AbilityInfo> abilities;

    public PokemonDetails() {

    }

    public void setName(String pokemon1) {
    }

    public void setStats(List<Stat> stats1) {
    }

    // Inner classes for nested JSON objects
    public static class AbilityInfo {
        private final Ability ability;
        @SerializedName("is_hidden")
        private boolean isHidden;
        private final int slot;

        public AbilityInfo(Ability ability, int slot) {
            this.ability = ability;
            this.slot = slot;
        }

        public Ability getAbility() {
            return ability;
        }

        public boolean isHidden() {
            return isHidden;
        }

        public int getSlot() {
            return slot;
        }
    }
    public static class Ability {
        private final String name;
        private final String url;

        public Ability(String name, String url) {
            this.name = name;
            this.url = url;
        }

        public String getName() {
            return name;
        }

        public String getUrl() {
            return url;
        }
    }
    public static class Sprites {
        @SerializedName("other")
        private Other other;

        public Other getOther() {
            return other;
        }

        public static class Other {
            @SerializedName("showdown")
            private Showdown showdown;

            public Showdown getShowdown() {
                return showdown;
            }

            public static class Showdown {
                @SerializedName("front_default")
                private String frontDefault;

                public String getFrontDefault() {
                    return frontDefault;
                }
            }
        }
    }

    public static class Stat {
        @SerializedName("base_stat")
        private int baseStat;

        private StatDetail stat;

        public Stat(StatDetail stat) {
            this.stat = stat;
        }

        public int getBaseStat() {
            return baseStat;
        }

        public StatDetail getStat() {
            return stat;
        }
    }

    public static class StatDetail {
        private final String name;

        public StatDetail(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
    public static class TypeInfo {
        private final int slot;
        private final TypeDetail type;

        public TypeInfo(int slot, TypeDetail type) {
            this.slot = slot;
            this.type = type;
        }

        public int getSlot() {
            return slot;
        }

        public TypeDetail getType() {
            return type;
        }
    }

    public static class TypeDetail {
        private final String name;
        private final String url;

        public TypeDetail(String name, String url) {
            this.name = name;
            this.url = url;
        }

        public String getName() {
            return name;
        }

        public String getUrl() {
            return url;
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
    public List<TypeInfo> getTypes() { return types; }
    public List<AbilityInfo> getAbilities() { return abilities; }
}
