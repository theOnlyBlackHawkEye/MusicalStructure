package com.example.android.musicalstructue;

import android.Manifest;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity {

    public static int listItemId;
    public static ArrayList<Song> duplicateSongList = new ArrayList<>();
    private static String songTitle = null;
    private static String songArtist = null;
    private static long songId = 0;
    private static int currentListItemId;
    @BindView(R.id.play_list)
    ImageView playListButton;
    @BindView(R.id.play_button)
    ImageView playButton;
    @BindView(R.id.next_button)
    ImageView nextButton;
    @BindView(R.id.back_button)
    ImageView backButton;
    @BindView(R.id.rewind_button)
    ImageView rewindButton;
    @BindView(R.id.forward_button)
    ImageView forwardButton;
    @BindView(R.id.repeat_button)
    ImageView repeatButton;
    @BindView(R.id.shuffle_button)
    ImageView shuffleButton;
    @BindView(R.id.song_name)
    TextView songTitleTextView;
    @BindView(R.id.song_artist)
    TextView songArtistTextView;
    @BindView(R.id.song_seek_bar)
    SeekBar songSeekBar;
    private Toast backButtonToast;
    private Handler seekbarUpdateHandler = new Handler();
    private MediaPlayer mMediaPlayer;
    private Runnable updateSeekbar = new Runnable() {
        @Override
        public void run() {
            songSeekBar.setProgress(mMediaPlayer.getCurrentPosition());
            seekbarUpdateHandler.postDelayed(this, 50);
        }
    };
    private AudioManager mAudioManager;
    private AudioManager.OnAudioFocusChangeListener mOnAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int i) {
            if (i == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT || i == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
                mMediaPlayer.pause();
                mMediaPlayer.seekTo(0);
            } else if (i == AudioManager.AUDIOFOCUS_GAIN) {
                mMediaPlayer.start();
            } else if (i == AudioManager.AUDIOFOCUS_LOSS) {
                releaseMediaPlayer();
            }
        }
    };

    private MediaPlayer.OnCompletionListener mCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            releaseMediaPlayer();
            nextTrack();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setTitle("Play Now");
        ButterKnife.bind(this);

        mAudioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(this, "App need Read External Files Permission to play Music Files", Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
            }
        }

        playListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openMainActivity = new Intent(MainActivity.this, PlayListActivity.class);
                openMainActivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivityIfNeeded(openMainActivity, 0);
            }
        });

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer integer = (Integer) playButton.getTag();
                integer = integer == null ? 0 : integer;
                switch (integer) {
                    case 0:
                        Toast.makeText(getApplicationContext(), "Choose Track First from List", Toast.LENGTH_SHORT).show();
                        break;
                    case R.drawable.round_play_circle_outline_white_48:
                        resumePlaying();
                        break;
                    case R.drawable.round_pause_circle_outline_white_48:
                    default:
                        pausePlaying();
                        break;
                }
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isTrackIsSelected())
                    nextTrack();
                else
                    Toast.makeText(getApplicationContext(), "Choose Track First from List", Toast.LENGTH_SHORT).show();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isTrackIsSelected())
                    previousTrack();
                else
                    Toast.makeText(getApplicationContext(), "Choose Track First from List", Toast.LENGTH_SHORT).show();
            }
        });

        rewindButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isTrackIsSelected())
                    rewindTrack();
                else
                    Toast.makeText(getApplicationContext(), "Choose Track First from List", Toast.LENGTH_SHORT).show();
            }
        });

        forwardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isTrackIsSelected())
                    forwardTrack();
                else
                    Toast.makeText(getApplicationContext(), "Choose Track First from List", Toast.LENGTH_SHORT).show();
            }
        });

        repeatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "This Feature is NOT Implmented Yet", Toast.LENGTH_LONG).show();
            }
        });

        shuffleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "This Feature is NOT Implmented Yet", Toast.LENGTH_LONG).show();
            }
        });


        songSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && isTrackIsSelected())
                    mMediaPlayer.seekTo(progress);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    private void releaseMediaPlayer() {
        // If the media player is not null, then it may be currently playing a sound.
        if (isTrackIsSelected()) {
            // Regardless of the current state of the media player, release its resources
            // because we no longer need it.
            mMediaPlayer.release();
            // Set the media player back to null. For our code, we've decided that
            // setting the media player to null is an easy way to tell that the media player
            // is not configured to play an audio file at the moment.
            mMediaPlayer = null;
        }
        mAudioManager.abandonAudioFocus(mOnAudioFocusChangeListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (listItemId != currentListItemId) {
            startPlaying();
        }
    }

    @Override
    public void onBackPressed() {
        if (backButtonToast != null && backButtonToast.getView().getWindowToken() != null) {
            releaseMediaPlayer();
            finishAffinity();
        } else {
            backButtonToast = Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT);
            backButtonToast.show();
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        releaseMediaPlayer();
    }

    private void startPlaying() {
        songId = duplicateSongList.get(listItemId).getSongId();
        songTitle = duplicateSongList.get(listItemId).getSongTitle();
        songArtist = duplicateSongList.get(listItemId).getSongArtist();
        songTitleTextView.setText(songTitle);
        songArtistTextView.setText(songArtist);
        currentListItemId = listItemId;
        releaseMediaPlayer();
        Uri mySongUri = ContentUris.withAppendedId(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, songId);
        int result = mAudioManager.requestAudioFocus(mOnAudioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            mMediaPlayer = MediaPlayer.create(this, mySongUri);
            if (isTrackIsSelected()) {
                startTrack();
                mMediaPlayer.setOnCompletionListener(mCompletionListener);
                playButton.setTag(R.drawable.round_pause_circle_outline_white_48);
                playButton.setImageResource(R.drawable.round_pause_circle_outline_white_48);
            } else pausePlaying();
        }
    }

    private void resumePlaying() {
        playButton.setTag(R.drawable.round_pause_circle_outline_white_48);
        playButton.setImageResource(R.drawable.round_pause_circle_outline_white_48);
        mMediaPlayer.start();
    }

    private void pausePlaying() {
        playButton.setTag(R.drawable.round_play_circle_outline_white_48);
        playButton.setImageResource(R.drawable.round_play_circle_outline_white_48);
        pauseTrack();
    }

    private void startTrack() {
        songSeekBar.setMax(mMediaPlayer.getDuration());
        seekbarUpdateHandler.postDelayed(updateSeekbar, 0);
        mMediaPlayer.start();
    }

    private void pauseTrack() {
        if (isTrackIsSelected()) {
            seekbarUpdateHandler.removeCallbacks(updateSeekbar);
            mMediaPlayer.pause();
        } else
            Toast.makeText(getApplicationContext(), "This Track is NOT Valid MP3 File", Toast.LENGTH_SHORT).show();
    }

    private void nextTrack() {
        listItemId++;
        startPlaying();
    }

    private void previousTrack() {
        listItemId--;
        startPlaying();
    }

    private void rewindTrack() {
        int position = mMediaPlayer.getCurrentPosition();
        position -= 30000;
        mMediaPlayer.seekTo(position);
    }

    private void forwardTrack() {
        int position = mMediaPlayer.getCurrentPosition();
        position += 30000;
        mMediaPlayer.seekTo(position);
    }

    private boolean isTrackIsSelected() {
        return mMediaPlayer != null;
    }
}
