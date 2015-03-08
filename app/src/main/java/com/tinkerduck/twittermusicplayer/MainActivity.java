package com.tinkerduck.twittermusicplayer;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.SessionManager;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.services.StatusesService;
import com.twitter.sdk.android.tweetui.CompactTweetView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import io.fabric.sdk.android.Fabric;

// TODO: Add pull to refresh.
// TODO: Add Service Launcher to begin service
// TODO: Add Regex to look for Artist, Genre, Song, etc.

public class MainActivity extends Activity {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.

    private Handler handler = new Handler();
    private int timeDelay = 1000 * 60 * 3; //3 minute delay, from miliseconds to seconds ;)
    public long lastMentionID = 1L;
    public TwitterSession session;
    public String[] splitTweet;
    public List<String> splitTweetList = new ArrayList<>();
    public MusicHandler musicHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        musicHandler = new MusicHandler(this);
        startHandler(null);

        TwitterLoginButton loginButton = (TwitterLoginButton) findViewById(R.id.login_button);
        loginButton.setEnabled(true);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        if (Twitter.getSessionManager().getActiveSession() != null) {
            session = Twitter.getSessionManager().getActiveSession();
            getMentions();
        }
        loginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> twitterSessionResult) {
                session = Twitter.getSessionManager().getActiveSession();
                TwitterAuthToken authToken = session.getAuthToken();
                final String token = authToken.token;
                final String secret = authToken.secret;
                getMentions();
            }
            @Override
            public void failure(TwitterException e) {
                Toast.makeText(MainActivity.this, "Unable To Log In :(", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacks(runnable);
        super.onDestroy();
    }

    private void getMentions() {
        final LinearLayout tweetLayout = (LinearLayout) findViewById(R.id.tweet_layout);
        TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient();
        StatusesService statusesService = twitterApiClient.getStatusesService();

        statusesService.mentionsTimeline(5, // Count
                lastMentionID, // Since ID
                null, // Last ID
                false, //Trim User
                true, // Contributor Details
                true, // Entities
                new Callback<List<Tweet>>() {
                    @Override
                    public void success(Result<List<Tweet>> listResult) {
                        final List<Tweet> listTweets = listResult.data;
                        if(!listTweets.isEmpty()) {
                            splitTweetList.clear();
                            splitTweet = null;
                            for (Tweet tweet : listTweets) {

                                String tweetText = tweet.text;
                                splitTweet = tweetText.split("@\\w+");
                                splitTweetList.add(splitTweet[1]);

                                tweetLayout.addView(new CompactTweetView(
                                        MainActivity.this,
                                        tweet));
                                if (tweet.id > lastMentionID) {
                                    lastMentionID = tweet.id;
                                }
                            }
                        }
                    }

                    @Override
                    public void failure(TwitterException e) {
                        Toast.makeText(MainActivity.this, "Failed to get Twitter Mentions", Toast.LENGTH_SHORT).show();

                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        TwitterLoginButton loginButton = (TwitterLoginButton) findViewById(R.id.login_button);

        // Pass the activity result to the login button.
        loginButton.onActivityResult(requestCode, resultCode,
                data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case (R.id.action_service):
                Toast.makeText(this, "I don't do anything yet!", Toast.LENGTH_SHORT).show();
                return true;
            case (R.id.action_settings):
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void playReceivedMusic(View view) {
        musicHandler.playArtist(splitTweetList.get(0));
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd:MMMM:yyyy HH:mm:ss a");
                final String strDate = simpleDateFormat.format(calendar.getTime());
                getMentions();
                playReceivedMusic(null);
                Toast.makeText(getApplicationContext(), strDate, Toast.LENGTH_SHORT).show();
                //if tweet received...
                //  use regex to strip! ;)
                //  generate an intent for Google Music
                //else, don't do anything.
                handler.postDelayed(this, timeDelay);
        }
    };

    public void startHandler(View view) {
        handler.postDelayed(runnable, 10000);
        Toast.makeText(this, "Handler Started", Toast.LENGTH_SHORT).show();
    }

    public void stopHandler(View view) {
        handler.removeCallbacks(runnable);
        Toast.makeText(this, "Handler Stopped", Toast.LENGTH_SHORT).show();

    }
}