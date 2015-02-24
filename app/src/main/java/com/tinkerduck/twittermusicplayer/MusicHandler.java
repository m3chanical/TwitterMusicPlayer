package com.tinkerduck.twittermusicplayer;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.provider.MediaStore;
import android.widget.Toast;

/**
 * Created by Carl on 2/23/2015.
 */
public class MusicHandler {
    Context context;


    public MusicHandler(Context context){
        this.context = context; // Need the context of the activity in order to properly launch Intents
    }

    public void playArtist(String artist) {
        Intent intent = new Intent(MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH);
        intent.putExtra(MediaStore.EXTRA_MEDIA_FOCUS,
                MediaStore.Audio.Artists.ENTRY_CONTENT_TYPE);
        intent.putExtra(MediaStore.EXTRA_MEDIA_ARTIST, artist);
        intent.putExtra(SearchManager.QUERY, artist);
        Toast.makeText(context, "Playing Artist: " + artist, Toast.LENGTH_SHORT).show();

        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        }
    }
    public void playSong(String song){
        Intent intent = new Intent(MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH);
        intent.putExtra(MediaStore.EXTRA_MEDIA_TITLE,
                "blahblahblah"); //TODO: Look up Play Song string from Common Intents.
    }

    public void playGenre(String genre){}

    public void playPlaylist(String playlist){}

}
