package com.mikemccartney.twitterclient.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.mikemccartney.twitterclient.App;
import com.mikemccartney.twitterclient.R;
import com.mikemccartney.twitterclient.fragment.LoginFragment;
import com.mikemccartney.twitterclient.fragment.TimelineFragment;
import com.mikemccartney.twitterclient.utils.dagger.components.ActivityComponent;
import com.mikemccartney.twitterclient.utils.dagger.components.DaggerActivityComponent;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.tweetcomposer.ComposerActivity;

import javax.inject.Inject;

public class MainActivity extends AppCompatActivity implements LoginFragment.OnLoginListener {

    public static final int TWEET_COMPOSE_REQUEST_CODE = 1001;

    private FloatingActionButton mFab;
    private ActionBar mActionBar;

    @Inject
    public TwitterSession mTwitterSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        mActionBar = getSupportActionBar();

        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new ComposerActivity.Builder(MainActivity.this)
                        .session(mTwitterSession)
                        .createIntent();
                startActivityForResult(intent, TWEET_COMPOSE_REQUEST_CODE);
            }
        });

        // Use Dagger 2 DI
        ActivityComponent activityComponent = DaggerActivityComponent
                .builder()
                .activityModule(App.getInstance().getApplicationModule().getActivityModule(this))
                .build();
        activityComponent.inject(this);

        if (mTwitterSession.getUserId() == TwitterSession.LOGGED_OUT_USER_ID) {
            displayLoginPage();
        } else {
            displayTweetsPage();
        }
    }

    private void displayLoginPage() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_placeholder, LoginFragment.newInstance())
                .commit();

        mActionBar.setTitle(R.string.app_name);
        mFab.setVisibility(View.GONE);
    }

    private void displayTweetsPage() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_placeholder, TimelineFragment.newInstance())
                .commit();

        mActionBar.setTitle(mTwitterSession.getUserName());
        mFab.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (mTwitterSession != null
                && !TextUtils.isEmpty(mTwitterSession.getUserName())
                && menu.size() == 0) {

            getMenuInflater().inflate(R.menu.menu_main, menu);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            Twitter.logOut();
            mTwitterSession = Twitter.getSessionManager().getActiveSession();
            invalidateOptionsMenu();
            displayLoginPage();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.content_placeholder);
        if (fragment != null) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * Provide logout functionality in overflow menu, if login successful
     * Reinitialise new TwitterSession object
     */
    @Override
    public void onLoginSuccessful() {
        invalidateOptionsMenu();
        mTwitterSession = Twitter.getSessionManager().getActiveSession();
        displayTweetsPage();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Twitter.logOut();
    }
}
