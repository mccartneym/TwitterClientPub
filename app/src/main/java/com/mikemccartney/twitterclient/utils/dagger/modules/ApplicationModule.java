package com.mikemccartney.twitterclient.utils.dagger.modules;

import android.content.Context;

import com.mikemccartney.twitterclient.App;
import com.mikemccartney.twitterclient.activity.MainActivity;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Dagger module that provides objects which will live during the application lifecycle.
 */
@Module
public class ApplicationModule {
    private final App mApplication;

    public ApplicationModule(App application) {
        mApplication = application;
    }

    public ActivityModule getActivityModule(MainActivity activity) {
        return new ActivityModule(activity);
    }

    @Provides
    @Singleton
    public Context provideApplicationContext() {
        return mApplication;
    }

}
