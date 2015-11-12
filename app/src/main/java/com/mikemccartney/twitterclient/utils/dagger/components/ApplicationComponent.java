package com.mikemccartney.twitterclient.utils.dagger.components;

import android.content.Context;

import com.mikemccartney.twitterclient.utils.dagger.modules.ApplicationModule;

import javax.inject.Singleton;

import dagger.Component;


@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {

    //Exposed to sub-graphs.
    Context context();
}
