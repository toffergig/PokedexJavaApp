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
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {
    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void testRecyclerViewIsDisplayed() {
        onView(withId(R.id.pokemon_recycler_view)).check(matches(isDisplayed()));
    }

    @Test
    public void testSearchViewIsDisplayed() {
        onView(withId(R.id.search_view)).check(matches(isDisplayed()));
    }

    @Test
    public void testSwipeRefreshLayoutIsDisplayed() {
        onView(withId(R.id.swipe_refresh_layout)).check(matches(isDisplayed()));
    }
}