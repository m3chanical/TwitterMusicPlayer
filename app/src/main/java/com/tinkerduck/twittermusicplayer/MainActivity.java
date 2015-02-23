package com.tinkerduck.twittermusicplayer;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.services.StatusesService;
import com.twitter.sdk.android.tweetui.TweetView;

import java.util.ArrayList;
import java.util.List;

import io.fabric.sdk.android.Fabric;

// TODO: Add pull to refresh.
// TODO: Add Service Launcher to begin service
// TODO: Maybe add card layout for the tweets.

public class MainActivity extends Activity {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "0gufvnbrcuqDQcJ67ZOk3FpW1";
    private static final String TWITTER_SECRET = "aiRAfpseomK5qqMZdpLrzc4FUB9DiQh1UG2E71UlzJs8IqEAfx";
    TweetPullService pullService;
    TwitterSession session;
    public String[] splitTweet;
    public ArrayList<String> splitTweetList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TwitterLoginButton loginButton = (TwitterLoginButton) findViewById(R.id.login_button);
        final Button testButton = (Button) findViewById(R.id.test_button);
        Button serviceButton = (Button) findViewById(R.id.service_button);

        testButton.setEnabled(false);
        serviceButton.setEnabled(false);


        loginButton.setEnabled(true);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));

        final LinearLayout myLayout = (LinearLayout) findViewById(R.id.tweet_layout);
        loginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> twitterSessionResult) {
                session = Twitter.getSessionManager().getActiveSession();
                TwitterAuthToken authToken = session.getAuthToken();
                final String token = authToken.token;
                final String secret = authToken.secret;
                testButton.setEnabled(true);
                getMentions(myLayout);

            }
            @Override
            public void failure(TwitterException e) {
                Toast.makeText(MainActivity.this, "Unable To Log In :(", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getMentions(final LinearLayout myLayout) {
        TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient();
        StatusesService statusesService = twitterApiClient.getStatusesService();

        statusesService.mentionsTimeline(200, // Count
                566715021770887168L + 1, // Since ID
                null, // Last ID
                false, //Trim User
                true, // Contributor Details
                true, // Entities
                new Callback<List<Tweet>>() {
            @Override
            public void success(Result<List<Tweet>> listResult) {
                final List<Tweet> listTweets = listResult.data;
                int i = 0;
                for (Tweet tweet : listTweets) {

                    String tweetText = tweet.text;
                    splitTweet = tweetText.split("@\\w+");
                    splitTweetList.add(i, splitTweet[1]);
                    i++;
                    myLayout.addView(new TweetView(
                            MainActivity.this,
                                        tweet));
                }
            }
            @Override
            public void failure(TwitterException e) {
                Toast.makeText(MainActivity.this, "getMention Failure", Toast.LENGTH_SHORT).show();

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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void playSearchArtist(View view) {

        Intent intent = new Intent(MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH);
        intent.putExtra(MediaStore.EXTRA_MEDIA_FOCUS,
                MediaStore.Audio.Artists.ENTRY_CONTENT_TYPE);
        intent.putExtra(MediaStore.EXTRA_MEDIA_ARTIST, splitTweetList.get(0));
        intent.putExtra(SearchManager.QUERY, splitTweetList.get(0));
        Toast.makeText(MainActivity.this, "Playing Artist: " + splitTweetList.get(0), Toast.LENGTH_SHORT).show();

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    public void launchService(View view) {
        final LinearLayout myLayout = (LinearLayout) findViewById(R.id.tweet_layout);
        getMentions(myLayout);

        pullService.startService(new Intent(this, TweetPullService.class));
    }
}