package com.mikemccartney.twitterclient.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.contrib.CountingIdlingResource;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.mikemccartney.twitterclient.R;
import com.mikemccartney.twitterclient.util.DecoratedTweetTimelineListAdapter;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.registerIdlingResources;
import static android.support.test.espresso.Espresso.unregisterIdlingResources;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.swipeDown;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;


@RunWith(AndroidJUnit4.class)
@LargeTest
public class TimelineFragmentTest extends BaseTest {
    private static final String TAG = TimelineFragmentTest.class.getSimpleName();

    private static final String EXPECTED_LOGOUT_BUTTON_TEXT = "Logout";
    private static final String EXPECTED_TOOLBAR_TEXT = "TwitterClient";
    private static final int EXPECTED_TWEETS_COUNT = 20;

    @Rule
    public final IntentsTestRule<MainActivity> mActivityRule = new IntentsTestRule<MainActivity>(MainActivity.class) {
        @Override
        protected void beforeActivityLaunched() {
            TimelineFragmentTest.super.beforeActivityLaunched();
            sIsLoggedIn = true;
        }
    };


    @Test
    public void loggedInUser_hasNameInToolBar() {
        onView(withText(EXPECTED_NAME)).check(matches(isDisplayed()));
    }

    @Test
    public void loggedInUser_hasFloatingActionButton() {
        onView(withId(R.id.fab)).check(matches(isDisplayed()));
    }

    @Test
    public void pressFloatingActionButton_displaysTweetComposer() {
        onView(withId(R.id.fab)).perform(click());
        ComponentName componentName = new ComponentName("com.mikemccartney.twitterclient",
                "com.twitter.sdk.android.tweetcomposer.ComposerActivity");
        intended(hasComponent(componentName));
    }

    @Test
    public void pressLogoutInOverflow_logsUserOut() {
        Espresso.openActionBarOverflowOrOptionsMenu(mActivityRule.getActivity());
        onView(withText(EXPECTED_LOGOUT_BUTTON_TEXT)).perform(click());
        onView(withText(EXPECTED_TOOLBAR_TEXT)).check(matches(isDisplayed()));
        onView(withId(R.id.login_button)).check(matches(isDisplayed()));
    }

    @Test
    public void testAdapter_loadsTwentyItems() {
        CountingIdlingResource countingResource = new CountingIdlingResource("isRefreshingState", true);
        assertTrue(registerIdlingResources(countingResource));

        Activity activity = mActivityRule.getActivity();

        DecoratedTweetTimelineListAdapter adapter = (DecoratedTweetTimelineListAdapter) getAdapter(activity);
        adapter.setIdlingResource(countingResource);

        onView(withId(R.id.timeline_list)).perform(swipeDown());
        assertEquals(EXPECTED_TWEETS_COUNT, getAdapter(activity).getCount());
        assertTrue(unregisterIdlingResources(countingResource));
    }

}