package com.mikemccartney.twitterclient;

import android.app.Application;
import android.support.annotation.VisibleForTesting;

import com.mikemccartney.twitterclient.utils.dagger.components.ApplicationComponent;
import com.mikemccartney.twitterclient.utils.dagger.components.DaggerApplicationComponent;
import com.mikemccartney.twitterclient.utils.dagger.modules.ApplicationModule;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import io.fabric.sdk.android.Fabric;


public class App extends Application {

    public static final String TWITTER_KEY = "<insert-your-key>";
    public static final String TWITTER_SECRET = "<insert-your-secret";

    private static App sInstance;

    private ApplicationComponent mAppComponent;
    private ApplicationModule mAppModule;


    public static App getInstance() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        sInstance = this;

        mAppModule = new ApplicationModule(this);
        mAppComponent = DaggerApplicationComponent.builder()
                .applicationModule(mAppModule)
                .build();

        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
    }

    public ApplicationModule getApplicationModule() {
        return mAppModule;
    }

    public ApplicationComponent getApplicationComponent() {
        return mAppComponent;
    }

    @VisibleForTesting
    public void setApplicationComponentModule(ApplicationComponent appComponent, ApplicationModule appModule) {
        mAppComponent = appComponent;
        mAppModule = appModule;
    }


}
