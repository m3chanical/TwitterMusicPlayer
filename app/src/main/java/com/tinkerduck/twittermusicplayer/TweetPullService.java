package com.tinkerduck.twittermusicplayer;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.services.StatusesService;
import com.twitter.sdk.android.tweetui.CompactTweetView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Carl on 2/17/2015.
 */
public class TweetPullService extends Service {

    private final TwitterSession session;
    private boolean receivedTweet;
    private MusicHandler musicHandler;
    public String[] splitTweet;
    public List<String> splitTweetList = new ArrayList<>();

    public TweetPullService(TwitterSession session){
        this.session = session;
        musicHandler = new MusicHandler(this);
    }

    @Override
    public void onCreate() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        getMentions();
        if(receivedTweet) {
            playReceivedMusic();
        } else {
            stopSelf();
        }
        stopSelf();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void playReceivedMusic() {
        String artist = splitTweetList.get(0);
        musicHandler.playArtist(artist);

    }

    private void getMentions() {
        TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient();
        StatusesService statusesService = twitterApiClient.getStatusesService();

        statusesService.mentionsTimeline(200, // Count
                1L, // Since ID
                null, // Last ID
                false, //Trim User
                true, // Contributor Details
                true, // Entities
                new Callback<List<Tweet>>() {
                    @Override
                    public void success(Result<List<Tweet>> listResult) {
                        final List<Tweet> listTweets = listResult.data;
                        if (!listTweets.isEmpty()) { //If we received a tweet...
                            Toast.makeText(TweetPullService.this, "Snagged a tweet!", Toast.LENGTH_SHORT).show();
                            receivedTweet = true;
                            for (Tweet tweet : listTweets) {

                                String tweetText = tweet.text;
                                splitTweet = tweetText.split("@\\w+");
                                splitTweetList.add(splitTweet[1]);
                            }
                        } else { //If no tweets received...
                            receivedTweet = false;
                            Toast.makeText(TweetPullService.this, "No tweets received.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void failure(TwitterException e) {
                        Toast.makeText(TweetPullService.this, "Failed to get Twitter Mentions", Toast.LENGTH_SHORT).show();

                    }
                });
    }
}
