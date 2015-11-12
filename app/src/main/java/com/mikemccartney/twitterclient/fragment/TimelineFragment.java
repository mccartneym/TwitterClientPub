package com.mikemccartney.twitterclient.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.mikemccartney.twitterclient.App;
import com.mikemccartney.twitterclient.R;
import com.mikemccartney.twitterclient.activity.MainActivity;
import com.mikemccartney.twitterclient.utils.dagger.components.ActivityComponent;
import com.mikemccartney.twitterclient.utils.dagger.components.DaggerActivityComponent;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetui.TimelineResult;
import com.twitter.sdk.android.tweetui.TweetTimelineListAdapter;

import javax.inject.Inject;


public class TimelineFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = TimelineFragment.class.getSimpleName();

    public static final int MAX_ITEMS_PER_REQUEST = 20;

    @Inject
    public TwitterSession mTwitterSession;

    @Inject
    public TweetTimelineListAdapter mAdapter;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ListView mListView;



    public static TimelineFragment newInstance() {
        return new TimelineFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");

        View rootView = inflater.inflate(R.layout.fragment_timeline, container, false);

        mListView = (ListView) rootView.findViewById(R.id.timeline_list);

        // Dagger 2 DI
        ActivityComponent activityComponent = DaggerActivityComponent
                .builder()
                .activityModule(App.getInstance().getApplicationModule().getActivityModule((MainActivity) getActivity()))
                .build();
        activityComponent.inject(this);

        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.timeline_swipe_refresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");

        mListView.setAdapter(mAdapter);
    }

    @Override
    public void onRefresh() {
        Log.i(TAG, "onRefresh");
        mSwipeRefreshLayout.setRefreshing(true);
        mAdapter.refresh(new Callback<TimelineResult<Tweet>>() {
            @Override
            public void success(Result<TimelineResult<Tweet>> result) {
                Log.i(TAG, "success:");
                mSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void failure(TwitterException exception) {
                Log.i(TAG, "failure: " + exception.getMessage());
                Toast.makeText(App.getInstance().getApplicationContext(), R.string.error_swipe_refresh_failed, Toast.LENGTH_LONG).show();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "onActivityResult");

        if (requestCode == MainActivity.TWEET_COMPOSE_REQUEST_CODE) {
            onRefresh();
        }
    }
}
