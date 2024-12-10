package com.example.pokedexjavaapp;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import com.example.pokedexjavaapp.api.PokemonApiService;
import com.example.pokedexjavaapp.models.PokemonDetails;
import com.example.pokedexjavaapp.models.PokemonDetails.Stat;
import com.example.pokedexjavaapp.models.PokemonDetails.StatDetail;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

@RunWith(RobolectricTestRunner.class)
public class ComparePokemonActivityTest {

    private ComparePokemonActivity activity;

    @Mock
    private PokemonApiService apiServiceMock;
    @Mock
    private Call<PokemonDetails> callMock;

    @Before
    public void setUp() {
        activity = Robolectric.buildActivity(ComparePokemonActivity.class).create().get();
        MockitoAnnotations.openMocks(this);
        activity = new ComparePokemonActivity();
        activity.apiService = apiServiceMock;
        activity.selectedPokemonIds = new ArrayList<>();
    }

    private PokemonDetails createMockPokemonDetails(int id, String name, int baseStat) {
        List<Stat> stats = new ArrayList<>();
        stats.add(new Stat(new StatDetail("speed")));
        stats.get(0).baseStat = baseStat;

        return new PokemonDetails(id, name, 10, 20, null, stats, null, null);
    }

    @Test
    public void fetchSelectedPokemonDetails_handlesSuccessfulResponse() {
        // Mock data
        PokemonDetails mockPokemon = createMockPokemonDetails(1, "Bulbasaur", 50);
        when(apiServiceMock.getPokemonDetails(1)).thenReturn(callMock);

        // Capture callback
        ArgumentCaptor<Callback<PokemonDetails>> callbackCaptor = ArgumentCaptor.forClass(Callback.class);
        doNothing().when(callMock).enqueue(callbackCaptor.capture());

        // Execute the test
        activity.selectedPokemonIds.add(1);
        activity.fetchSelectedPokemonDetails();

        // Trigger the success callback
        callbackCaptor.getValue().onResponse(callMock, Response.success(mockPokemon));

        // Assertions
        assertFalse(activity.fetchedDetails.isEmpty());
        assertEquals(1, activity.fetchedDetails.size());
        assertEquals("Bulbasaur", activity.fetchedDetails.get(0).getName());
    }

    @Test
    public void fetchSelectedPokemonDetails_handlesFailureResponse() {
        when(apiServiceMock.getPokemonDetails(1)).thenReturn(callMock);

        // Capture callback
        ArgumentCaptor<Callback<PokemonDetails>> callbackCaptor = ArgumentCaptor.forClass(Callback.class);
        doNothing().when(callMock).enqueue(callbackCaptor.capture());

        // Execute the test
        activity.selectedPokemonIds.add(1);
        activity.fetchSelectedPokemonDetails();

        // Trigger the failure callback
        callbackCaptor.getValue().onFailure(callMock, new Throwable("Network error"));

        // Assertions
        assertTrue(activity.fetchedDetails.isEmpty());
    }

    @Test
    public void fetchSelectedPokemonDetails_handlesMultipleSuccessfulResponses() {
        // Mock data
        PokemonDetails mockPokemon1 = createMockPokemonDetails(1, "Bulbasaur", 50);
        PokemonDetails mockPokemon2 = createMockPokemonDetails(2, "Charmander", 60);

        when(apiServiceMock.getPokemonDetails(1)).thenReturn(callMock);
        when(apiServiceMock.getPokemonDetails(2)).thenReturn(callMock);

        // Capture callback
        ArgumentCaptor<Callback<PokemonDetails>> callbackCaptor = ArgumentCaptor.forClass(Callback.class);
        doNothing().when(callMock).enqueue(callbackCaptor.capture());

        // Execute the test
        activity.selectedPokemonIds.add(1);
        activity.selectedPokemonIds.add(2);
        activity.fetchSelectedPokemonDetails();

        // Trigger the success callbacks
        callbackCaptor.getValue().onResponse(callMock, Response.success(mockPokemon1));
        callbackCaptor.getValue().onResponse(callMock, Response.success(mockPokemon2));

        // Assertions
        assertEquals(2, activity.fetchedDetails.size());
        assertEquals("Bulbasaur", activity.fetchedDetails.get(0).getName());
        assertEquals("Charmander", activity.fetchedDetails.get(1).getName());
    }
}
