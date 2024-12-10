package com.example.pokedexjavaapp;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

import android.widget.Button;
import com.example.pokedexjavaapp.models.PokemonAdapter;
import com.example.pokedexjavaapp.models.PokemonEntity;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = {28}) // Set the SDK level as needed
public class MainActivityTest {

    @Mock
    private PokemonAdapter mockPokemonAdapter;

    @Mock
    private Button mockCompareButton;

    private MainActivity mainActivity;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Build and create the activity
        mainActivity = Robolectric.buildActivity(MainActivity.class)
                .create()
                .resume() // resume() simulates the activity moving to the resumed state
                .get();

        // Replace the real adapter with a mock
        mainActivity.pokemonAdapter = mockPokemonAdapter;

        // Replace the real compareButton with a mock
        mainActivity.compareButton = mockCompareButton;
    }

    @Test
    public void togglePokemonSelection_addsPokemonToSelection() {
        // Arrange
        PokemonEntity pokemon = new PokemonEntity(1, "Bulbasaur", "url");

        // Act
        mainActivity.togglePokemonSelection(pokemon);

        // Assert
        assertEquals(1, mainActivity.selectedPokemons.size());
        assertTrue(mainActivity.selectedPokemons.contains(pokemon));
        verify(mockPokemonAdapter, times(1)).setSelectedPokemons(anyList());
    }

    @Test
    public void togglePokemonSelection_removesPokemonFromSelection() {
        // Arrange
        PokemonEntity pokemon = new PokemonEntity(1, "Bulbasaur", "url");
        mainActivity.selectedPokemons.add(pokemon);

        // Act
        mainActivity.togglePokemonSelection(pokemon);

        // Assert
        assertTrue(mainActivity.selectedPokemons.isEmpty());
        verify(mockPokemonAdapter, times(1)).setSelectedPokemons(anyList());
    }

    @Test
    public void updateCompareButtonState_disablesButtonWhenNoSelection() {
        // Act
        mainActivity.selectedPokemons.clear();
        mainActivity.updateCompareButtonState();

        // Assert
        verify(mockCompareButton).setEnabled(false);
    }

    @Test
    public void updateCompareButtonState_enablesButtonWhenSelectionExists() {
        // Arrange
        PokemonEntity pokemon = new PokemonEntity(1, "Bulbasaur", "url");
        mainActivity.selectedPokemons.add(pokemon);

        // Act
        mainActivity.updateCompareButtonState();

        // Assert
        verify(mockCompareButton).setEnabled(true);
    }

    @Test
    public void filterPokemonList_filtersCorrectly() {
        // Arrange
        List<PokemonEntity> allPokemons = new ArrayList<>();
        PokemonEntity bulbasaur = new PokemonEntity(1, "Bulbasaur", "url");
        PokemonEntity charmander = new PokemonEntity(2, "Charmander", "url");
        PokemonEntity squirtle = new PokemonEntity(3, "Squirtle", "url");
        allPokemons.add(bulbasaur);
        allPokemons.add(charmander);
        allPokemons.add(squirtle);

        mainActivity.allPokemons.addAll(allPokemons);

        // Act
        mainActivity.filterPokemonList("Bulbasaur");

        // Assert
        verify(mockPokemonAdapter).setPokemonList(argThat(filteredList ->
                filteredList.size() == 1 && filteredList.get(0).getName().equals("Bulbasaur")
        ));
    }
}
