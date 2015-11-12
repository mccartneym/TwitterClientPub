package com.mikemccartney.twitterclient.utils.dagger.modules;

import android.app.Activity;
import android.content.Context;

import com.mikemccartney.twitterclient.fragment.TimelineFragment;
import com.mikemccartney.twitterclient.utils.dagger.PerActivity;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.tweetui.TweetTimelineListAdapter;
import com.twitter.sdk.android.tweetui.UserTimeline;

import dagger.Module;
import dagger.Provides;


/**
 * A module to wrap the Activity state and expose it to the graph.
 */
@Module
public class ActivityModule {

    private final Activity mActivity;

    public ActivityModule(Activity activity) {
        mActivity = activity;
    }

    @Provides
    @PerActivity
    public Context getContext() {
        return mActivity;
    }

    @Provides
    @PerActivity
    public UserTimeline getUserTimeline() {
        return new UserTimeline.Builder()
                .screenName(getTwitterSession().getUserName())
                .maxItemsPerRequest(TimelineFragment.MAX_ITEMS_PER_REQUEST)
                .build();
    }

    @Provides
    @PerActivity
    public TwitterSession provideTwitterSession() {
        return getTwitterSession();
    }

    public TwitterSession getTwitterSession() {
        TwitterSession session = Twitter.getSessionManager().getActiveSession();
        if (session == null) {
            TwitterAuthToken token = new TwitterAuthToken("", "");
            session = new TwitterSession(token, TwitterSession.LOGGED_OUT_USER_ID, "");
        }
        return session;
    }

    @Provides
    @PerActivity
    public TweetTimelineListAdapter provideTweetTimelineListAdapter(Context context, UserTimeline userTimeline) {
        return getTweetTimelineListAdapter(context, userTimeline);
    }

    public TweetTimelineListAdapter getTweetTimelineListAdapter(Context context, UserTimeline userTimeline) {
        TweetTimelineListAdapter.Builder builder = new TweetTimelineListAdapter.Builder(context);
        return builder.setTimeline(userTimeline).build();
    }
}
