package com.ramyhelow.udacity_bakingappproj4;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;

import androidx.test.InstrumentationRegistry;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.IdlingResource;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.runner.AndroidJUnit4;

import com.ramyhelow.udacity_bakingappproj4.activities.DetailActivity;
import com.ramyhelow.udacity_bakingappproj4.activities.MainActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.intent.matcher.IntentMatchers.isInternal;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.IsNot.not;


@RunWith(AndroidJUnit4.class)
public class IntentTest {

    private static final String RECIPE_ITEM_BROWNIE = "Brownies";

    @Rule
    public IntentsTestRule<MainActivity> mActivityRule = new IntentsTestRule<>(
            MainActivity.class);
    private IdlingResource mIdlingResource;

    @Before
    public void registerIdlingResources() {
        mIdlingResource = mActivityRule.getActivity().getIdlingResource();
        IdlingRegistry.getInstance().register(mIdlingResource);
    }

    @Before
    public void stubAllExternalIntents() {
        intending(not(isInternal())).respondWith(new Instrumentation.ActivityResult(Activity.RESULT_OK, null));
    }


    @Test
    public void clickRecipe_LaunchDetailActivityIntent() {
        onView(withId(R.id.recipe_recycler_view))
                .perform(RecyclerViewActions.actionOnItem(
                        hasDescendant(withText(RECIPE_ITEM_BROWNIE)), click()));
        Context targetContext = InstrumentationRegistry.getTargetContext();
        targetContext.getResources().getBoolean(R.bool.tab);
        Boolean isTabletUsed = targetContext.getResources().getBoolean(R.bool.tab);
        if (!isTabletUsed) {
            intended(hasComponent(DetailActivity.class.getName()));
        }

        if (isTabletUsed) {
            onView(withId(R.id.video_container_tab)).check(matches(isDisplayed()));
        }

    }

    @After
    public void unregisterIdlingResource() {
        if (mIdlingResource != null) {
            IdlingRegistry.getInstance().unregister(mIdlingResource);
        }
    }

}
