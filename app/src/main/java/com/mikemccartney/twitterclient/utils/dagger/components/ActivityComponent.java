package com.mikemccartney.twitterclient.utils.dagger.components;

import com.mikemccartney.twitterclient.activity.MainActivity;
import com.mikemccartney.twitterclient.fragment.TimelineFragment;
import com.mikemccartney.twitterclient.utils.dagger.PerActivity;
import com.mikemccartney.twitterclient.utils.dagger.modules.ActivityModule;

import dagger.Component;

@PerActivity
@Component(modules = ActivityModule.class)
public interface ActivityComponent {
    void inject(MainActivity activity);
    void inject(TimelineFragment fragment);
}
