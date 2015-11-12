package com.mikemccartney.twitterclient.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mikemccartney.twitterclient.R;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;


public class LoginFragment extends Fragment {
    private static final String TAG = LoginFragment.class.getSimpleName();

    public interface OnLoginListener {
        void onLoginSuccessful();
    }

    private TwitterLoginButton mLoginButton;
    private OnLoginListener mLoginListener;


    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_login, container, false);

        mLoginButton = (TwitterLoginButton) rootView.findViewById(R.id.login_button);
        mLoginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                mLoginListener.onLoginSuccessful();
            }

            @Override
            public void failure(TwitterException exception) {
                // don't show message, we don't know if the login was cancelled by the user
                Log.w(TAG, "Login with Twitter failure", exception);
            }
        });

        return rootView;
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);

        try {
            mLoginListener = (OnLoginListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException((activity.toString()) + " must implement OnLoginListener");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mLoginButton.onActivityResult(requestCode, resultCode, data);
    }
}
