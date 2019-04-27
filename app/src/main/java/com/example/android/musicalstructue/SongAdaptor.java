package com.example.android.musicalstructue;

import android.app.Activity;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class SongAdaptor extends ArrayAdapter<Song> {


    public SongAdaptor(Activity context, ArrayList<Song> songList) {
        super(context, 0, songList);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        Song currentSong = getItem(position);

        TextView songTitle = listItemView.findViewById(R.id.song_title);
        TextView songArtist = listItemView.findViewById(R.id.song_artist);
        TextView songDuration = listItemView.findViewById(R.id.song_duration);
        songTitle.setText(currentSong.getSongTitle());
        songArtist.setText(currentSong.getSongArtist());
        songDuration.setText(currentSong.getSongDuration());
        songArtist.setTypeface(null, Typeface.ITALIC);
        songDuration.setTypeface(null, Typeface.ITALIC);

        return listItemView;
    }
}
