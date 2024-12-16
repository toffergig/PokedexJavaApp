package com.example.pokedexjavaapp;

import com.example.pokedexjavaapp.models.PokemonDetails;
import com.example.pokedexjavaapp.models.PokemonDetails.*;

import org.mockito.Mockito;

import java.util.List;

public class PokemonDetailsMock {

    public static PokemonDetails createMockPokemonDetails() {
        PokemonDetails pokemonDetails = Mockito.mock(PokemonDetails.class);

        // Mock individual fields
//        Mockito.when(pokemonDetails.getId()).thenReturn(1);
//        Mockito.when(pokemonDetails.getName()).thenReturn("Pikachu");
//        Mockito.when(pokemonDetails.getHeight()).thenReturn(4);
//        Mockito.when(pokemonDetails.getWeight()).thenReturn(60);

        // Mock Sprites
        Sprites sprites = Mockito.mock(Sprites.class);
        Sprites.Other other = Mockito.mock(Sprites.Other.class);
        Sprites.Other.Showdown showdown = Mockito.mock(Sprites.Other.Showdown.class);
//        Mockito.when(showdown.getFrontDefault()).thenReturn("front_default_url");
//        Mockito.when(other.getShowdown()).thenReturn(showdown);
//        Mockito.when(sprites.getOther()).thenReturn(other);
        //Mockito.when(pokemonDetails.getSprites()).thenReturn(sprites);

        // Mock Stats
        StatDetail statDetail = Mockito.mock(StatDetail.class);
     //   Mockito.when(statDetail.getName()).thenReturn("hp");
        Stat stat = Mockito.mock(Stat.class);
        Mockito.when(stat.getBaseStat()).thenReturn(35);
//        Mockito.when(stat.getStat()).thenReturn(statDetail);
        List<Stat> stats = List.of(stat);
        Mockito.when(pokemonDetails.getStats()).thenReturn(stats);

        // Mock Types
        TypeDetail typeDetail = Mockito.mock(TypeDetail.class);
//        Mockito.when(typeDetail.getName()).thenReturn("electric");
        TypeInfo typeInfo = Mockito.mock(TypeInfo.class);
//        Mockito.when(typeInfo.getSlot()).thenReturn(1);
//        Mockito.when(typeInfo.getType()).thenReturn(typeDetail);
        List<TypeInfo> types = List.of(typeInfo);
//        Mockito.when(pokemonDetails.getTypes()).thenReturn(types);

        // Mock Abilities
        Ability ability = Mockito.mock(Ability.class);
//        Mockito.when(ability.getName()).thenReturn("static");
        AbilityInfo abilityInfo = Mockito.mock(AbilityInfo.class);
//        Mockito.when(abilityInfo.getAbility()).thenReturn(ability);
//        Mockito.when(abilityInfo.isHidden()).thenReturn(false);
//        Mockito.when(abilityInfo.getSlot()).thenReturn(1);
        List<AbilityInfo> abilities = List.of(abilityInfo);
//        Mockito.when(pokemonDetails.getAbilities()).thenReturn(abilities);

        return pokemonDetails;
    }
}