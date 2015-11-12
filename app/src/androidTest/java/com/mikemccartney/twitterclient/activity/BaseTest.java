package com.mikemccartney.twitterclient.activity;

import android.app.Activity;
import android.content.Context;
import android.widget.ListView;

import com.mikemccartney.twitterclient.App;
import com.mikemccartney.twitterclient.R;
import com.mikemccartney.twitterclient.util.DecoratedTweetTimelineListAdapter;
import com.mikemccartney.twitterclient.utils.dagger.PerActivity;
import com.mikemccartney.twitterclient.utils.dagger.components.ActivityComponent;
import com.mikemccartney.twitterclient.utils.dagger.components.ApplicationComponent;
import com.mikemccartney.twitterclient.utils.dagger.modules.ActivityModule;
import com.mikemccartney.twitterclient.utils.dagger.modules.ApplicationModule;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.tweetui.TweetTimelineListAdapter;
import com.twitter.sdk.android.tweetui.UserTimeline;

import javax.inject.Singleton;

import dagger.Component;
import dagger.Module;

import static android.support.test.espresso.core.deps.guava.base.Preconditions.checkNotNull;



public class BaseTest {
    private static final String TAG = BaseTest.class.getSimpleName();

    static final String EXPECTED_NAME = "@mikemccartney11";

    static boolean sIsLoggedIn;

    void beforeActivityLaunched() {
        TestApplicationModule testApplicationModule = new TestApplicationModule(App.getInstance());
        TestApplicationComponent testApplicationComponent = DaggerBaseTest_TestApplicationComponent
                .builder()
                .testApplicationModule(testApplicationModule)
                .build();

        App.getInstance().setApplicationComponentModule(testApplicationComponent, testApplicationModule);
    }

    @Singleton
    @Component(modules = TestApplicationModule.class)
    interface TestApplicationComponent extends ApplicationComponent {
    }

    @PerActivity
    @Component(modules = TestActivityModule.class)
    interface TestActivityComponent extends ActivityComponent {
    }

    @Module
    public static class TestApplicationModule extends ApplicationModule {
        public TestApplicationModule(App application) {
            super(application);
        }

        @Override
        public ActivityModule getActivityModule(MainActivity activity) {
            return new TestActivityModule(activity);
        }
    }

    @Module
    public static class TestActivityModule extends ActivityModule {
        public TestActivityModule(Activity activity) {
            super(activity);
        }

        @Override
        public TwitterSession getTwitterSession() {
            TwitterAuthToken token;
            if (sIsLoggedIn) {
                token = new TwitterAuthToken(App.TWITTER_KEY, App.TWITTER_SECRET);
                return new TwitterSession(token, 1, EXPECTED_NAME);
            } else {
                token = new TwitterAuthToken("", "");
                return new TwitterSession(token, TwitterSession.LOGGED_OUT_USER_ID, "");
            }
        }

        @Override
        public DecoratedTweetTimelineListAdapter getTweetTimelineListAdapter(Context context, UserTimeline userTimeline) {
            TweetTimelineListAdapter realAdapter = super.getTweetTimelineListAdapter(context, userTimeline);
            return new DecoratedTweetTimelineListAdapter(context, userTimeline, realAdapter);
        }

    }

   TweetTimelineListAdapter getAdapter(Activity activity) {
        ListView listView = (ListView) activity.findViewById(R.id.timeline_list);
        TweetTimelineListAdapter adapter = (TweetTimelineListAdapter) listView.getAdapter();
        return checkNotNull(adapter);
    }
}