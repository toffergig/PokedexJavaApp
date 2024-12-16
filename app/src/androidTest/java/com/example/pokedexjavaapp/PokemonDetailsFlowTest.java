package com.example.pokedexjavaapp;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.junit.Rule;
import org.junit.Test;

public class PokemonDetailsFlowTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void testPokemonOpensDetailsFlow() {
        // Perform a click on the first item in the RecyclerView
        onView(withId(R.id.pokemon_recycler_view))
                .perform(androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition(0, click()));


        onView(ViewMatchers.withId(R.id.pokemon_detail_name))
                .check(matches(isDisplayed()));
        onView(ViewMatchers.withId(R.id.pokemon_detail_id))
                .check(matches(isDisplayed()));
        onView(ViewMatchers.withId(R.id.pokemon_detail_image))
                .check(matches(isDisplayed()));
        onView(ViewMatchers.withId(R.id.pokemon_types_layout))
                .check(matches(isDisplayed()));
        onView(ViewMatchers.withId(R.id.height_label))
                .check(matches(isDisplayed()));
        onView(ViewMatchers.withId(R.id.height_value))
                .check(matches(isDisplayed()));
        onView(ViewMatchers.withId(R.id.weight_label))
                .check(matches(isDisplayed()));
        onView(ViewMatchers.withId(R.id.weight_value))
                .check(matches(isDisplayed()));
        onView(ViewMatchers.withId(R.id.radar_chart))
                .check(matches(isDisplayed()));
        onView(ViewMatchers.withId(R.id.abilities_label))
                .check(matches(isDisplayed()));
        onView(ViewMatchers.withId(R.id.pokemon_abilities_layout))
                .check(matches(isDisplayed()));
        onView(ViewMatchers.withId(R.id.base_stats_layout))
                .check(matches(isDisplayed()));
    }
}
