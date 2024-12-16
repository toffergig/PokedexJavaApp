package com.example.pokedexjavaapp;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.junit.Rule;
import org.junit.Test;

public class ComparePokemonFlowTest {
    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void testComparePokemonFlow() {
        // Long-click on the first Pokémon in the RecyclerView
        onView(withId(R.id.pokemon_recycler_view))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, longClick()));

        // Long-click on the second Pokémon in the RecyclerView
        onView(withId(R.id.pokemon_recycler_view))
                .perform(RecyclerViewActions.actionOnItemAtPosition(1, longClick()));

        // Click the Compare button
        onView(withId(R.id.compare_button)).perform(click());

        onView(ViewMatchers.withId(R.id.radar_chart))
                .check(matches(isDisplayed()));
        onView(ViewMatchers.withId(R.id.textView5))
                .check(matches(isDisplayed()));
        onView(ViewMatchers.withId(R.id.base_stats_layout))
                .check(matches(isDisplayed()));
    }

}
