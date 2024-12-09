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
    @SerializedName("types")
    private List<TypeInfo> types;
    @SerializedName("abilities")
    private List<AbilityInfo> abilities;

    // Inner classes for nested JSON objects
    public static class AbilityInfo {
        private Ability ability;
        @SerializedName("is_hidden")
        private boolean isHidden;
        private int slot;

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
        private String name;
        private String url;

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
    public static class TypeInfo {
        private int slot;
        private TypeDetail type;

        public int getSlot() {
            return slot;
        }

        public TypeDetail getType() {
            return type;
        }
    }

    public static class TypeDetail {
        private String name;
        private String url;

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
