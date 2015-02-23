package com.tinkerduck.twittermusicplayer;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.twitter.sdk.android.core.TwitterSession;

/**
 * Created by Carl on 2/17/2015.
 */
public class TweetPullService extends Service {

    private final TwitterSession session;

    public TweetPullService(TwitterSession session){
        this.session = session;

    }

    @Override
    public void onCreate() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }



    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
