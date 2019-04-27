package com.example.android.musicalstructue;

public class Song {

    private String mSongTitle;
    private String mSongArtist;
    private long mSongId;
    private String mSongDuration;

    public Song(String songTitle, long songId, String songArtist, String songDuration) {
        mSongTitle = songTitle;
        mSongId = songId;
        mSongArtist = songArtist;
        mSongDuration = songDuration;
    }

    public String getSongTitle() {
        return mSongTitle;
    }

    public long getSongId() {
        return mSongId;
    }

    public String getSongArtist() {
        return mSongArtist;
    }

    public String getSongDuration() {
        int songDurationInSeconds = Integer.parseInt(mSongDuration) / 1000;
        int hours = songDurationInSeconds / 3600;
        int mins = (songDurationInSeconds % 3600) / 60;
        int secs = (songDurationInSeconds % 3600) % 60;
        if (mins < 10 && secs < 10) return hours + ":0" + mins + ":0" + secs;
        else if (mins < 10) return hours + ":0" + mins + ":" + secs;
        return hours + ":" + mins + ":" + secs;
    }
}
