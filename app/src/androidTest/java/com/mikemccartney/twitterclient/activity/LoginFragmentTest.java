package com.mikemccartney.twitterclient.activity;

import android.content.ComponentName;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.mikemccartney.twitterclient.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;


@RunWith(AndroidJUnit4.class)
@LargeTest
public class LoginFragmentTest extends BaseTest {

    private static final String EXPECTED_TOOLBAR_TEXT = "TwitterClient";

    @Rule
    public IntentsTestRule<MainActivity> mActivityRule = new IntentsTestRule<MainActivity>(MainActivity.class) {
        protected void beforeActivityLaunched() {
            LoginFragmentTest.super.beforeActivityLaunched();
            sIsLoggedIn = false;
        }
    };


    @Test
    public void loggedOutUser_hasAppNameInToolBar() {
        onView(withText(EXPECTED_TOOLBAR_TEXT)).check(matches(isDisplayed()));
    }

    @Test
    public void loggedOutUser_hasLoginButton() {
        onView(withId(R.id.login_button)).check(matches(isDisplayed()));
    }

    @Test
    public void loggedOutUser_hasNoFloatingActionButton() {
        onView(withId(R.id.fab)).check(matches(not((isDisplayed()))));
    }

    @Test
    public void pressLoginButton_createsOauthPage() {
        onView(withId(R.id.login_button)).perform(click());
        ComponentName componentName = new ComponentName("com.mikemccartney.twitterclient",
                "com.twitter.sdk.android.core.identity.OAuthActivity");
        intended(hasComponent(componentName));
    }
}