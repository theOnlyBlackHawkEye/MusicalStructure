package com.example.android.musicalstructue;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import butterknife.ButterKnife;

public class PlayListActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.song_list);
        this.setTitle("Songs List");
        ButterKnife.bind(this);


        final ArrayList<Song> songList = new ArrayList<>();

        String[] proj = {MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.MIME_TYPE,
                MediaStore.Audio.Media.IS_MUSIC,
                MediaStore.Audio.Media.IS_ALARM,
                MediaStore.Audio.Media.IS_NOTIFICATION,
                MediaStore.Audio.Media.IS_PODCAST,
                MediaStore.Audio.Media.IS_RINGTONE,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DURATION};
        final String[] selectionArg = new String[]{"audio/mpeg", "0", "0", "0", "0", "0"};
        Cursor audioCursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, proj,
                MediaStore.Audio.Media.MIME_TYPE + " = ? AND " + MediaStore.Audio.Media.IS_MUSIC + " != ? AND "
                        + MediaStore.Audio.Media.IS_ALARM + " = ? AND " + MediaStore.Audio.Media.IS_RINGTONE + " = ? AND "
                        + MediaStore.Audio.Media.IS_PODCAST + " = ? AND " + MediaStore.Audio.Media.IS_NOTIFICATION + " = ?", selectionArg, null);

        if (audioCursor != null) {
            if (audioCursor.moveToFirst()) {
                do {
                    songList.add(new Song(audioCursor.getString(audioCursor.getColumnIndex(MediaStore.Audio.Media.TITLE)),
                            audioCursor.getLong(audioCursor.getColumnIndex(MediaStore.Audio.Media._ID)),
                            audioCursor.getString(audioCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)),
                            audioCursor.getString(audioCursor.getColumnIndex(MediaStore.Audio.Media.DURATION))));
                } while (audioCursor.moveToNext());
            }
        }
        audioCursor.close();


        SongAdaptor songAdaptor = new SongAdaptor(PlayListActivity.this, songList);
        ListView songListView = findViewById(R.id.list);
        songListView.setAdapter(songAdaptor);

        songListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MainActivity.listItemId = position;
                MainActivity.duplicateSongList = songList;
                Intent openMainActivity = new Intent(PlayListActivity.this, MainActivity.class);
                openMainActivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivityIfNeeded(openMainActivity, 0);
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent openMainActivity = new Intent(PlayListActivity.this, MainActivity.class);
        openMainActivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivityIfNeeded(openMainActivity, 0);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent openMainActivity = new Intent(PlayListActivity.this, MainActivity.class);
                openMainActivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivityIfNeeded(openMainActivity, 0);
                return true;
        }

        return (super.onOptionsItemSelected(item));
    }


}
