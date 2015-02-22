package com.tinkerduck.twittermusicplayer;

import android.app.IntentService;
import android.content.Intent;

/**
 * Created by Carl on 2/17/2015.
 */
public class TweetPullService extends IntentService{
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public TweetPullService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }
}
