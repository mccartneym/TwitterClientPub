package com.mikemccartney.twitterclient.util;

import android.content.Context;
import android.support.test.espresso.contrib.CountingIdlingResource;
import android.util.Log;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetui.TimelineResult;
import com.twitter.sdk.android.tweetui.TweetTimelineListAdapter;
import com.twitter.sdk.android.tweetui.UserTimeline;

import static android.support.test.espresso.core.deps.guava.base.Preconditions.checkNotNull;
import static junit.framework.Assert.assertTrue;

/**
 * This class intercepts the callback from the TwitterTimelineList Adapter
 * to support tests which require the adapter to have finished loading
 */
public class DecoratedTweetTimelineListAdapter extends TweetTimelineListAdapter {
    private static final String TAG = DecoratedTweetTimelineListAdapter.class.getSimpleName();

    private static final int IDLE_DELAY_MILLIS = 100;
    private static final String TWITTER_EXCEPTION_MESSAGE = "Request already in flight";

    private final TweetTimelineListAdapter mRealAdapter;
    private CountingIdlingResource mIdlingResource;


    /**
     * Called during setup by the Dagger test injection
     * @param context Activity context
     * @param userTimeline timeline of Tweets
     * @param adapter the real adapter used by the app
     */
    public DecoratedTweetTimelineListAdapter(Context context, UserTimeline userTimeline, TweetTimelineListAdapter adapter) {
        super(context, userTimeline);
        mRealAdapter = checkNotNull(adapter);
    }

    /**
     * Resource set at the beginning of the test
     * @param IdlingResource
     */
    public void setIdlingResource(CountingIdlingResource IdlingResource) {
        mIdlingResource = IdlingResource;
        assertTrue(mIdlingResource.isIdleNow());
    }

    @Override
    public void refresh(final Callback<TimelineResult<Tweet>> cb) {
        if (cb == null) {
            return;
        }

        mIdlingResource.increment();

        mRealAdapter.refresh(new Callback<TimelineResult<Tweet>>() {
            @Override
            public void success(Result<TimelineResult<Tweet>> result) {
                cb.success(result);
                mIdlingResource.decrement();
            }

            @Override
            public void failure(TwitterException exception) {

                // Check if the adapter is still loading from launching the app
                if (exception.getMessage().equals(TWITTER_EXCEPTION_MESSAGE)) {
                    Log.i(TAG, TWITTER_EXCEPTION_MESSAGE + " - wait and retry");
                    try {
                        Thread.sleep(IDLE_DELAY_MILLIS);
                        DecoratedTweetTimelineListAdapter.this.refresh(cb);
                    } catch (InterruptedException e) {
                        Log.w(TAG, "InterruptedException", e);
                    }

                    // only call failure callback to update SwipeRefresh state
                } else {
                    cb.failure(exception);
                }

                mIdlingResource.decrement();
            }
        });
    }

}
