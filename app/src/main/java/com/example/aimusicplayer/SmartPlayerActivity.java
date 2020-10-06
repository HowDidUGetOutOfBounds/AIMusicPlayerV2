package com.example.aimusicplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.example.aimusicplayer.R.xml.rotate_center;

public class SmartPlayerActivity extends AppCompatActivity  {

    private RelativeLayout parentRelativeLayout;
    private SpeechRecognizer speechRecognizer;
    private Intent speechRecognizerIntent;
    private String keeper = "";

    private ImageView pausePlayBtn, nextBtn, previousBtn, repeatBtn;
    private TextView songNameTxt;
    private ImageView imageView;
    private RelativeLayout lowerRelativeLayout;
    private Button voiceEnabledButton;
    private String mode = "ON";
    private int repeatState = 1; // 1.
    private Random r;  // 1. для кнопки зацикливания, повтора и рандома

    private MediaPlayer myMediaPlayer = new MediaPlayer();
    private int position;
    private ArrayList<File> mySongs;
    private String mSongName;

    private Animation animationRotateCenter;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); // запрет поворота экрана


        animationRotateCenter = AnimationUtils.loadAnimation(
                this, rotate_center);
        checkVoicePermission();


        pausePlayBtn = findViewById(R.id.play_pause_btn);
        nextBtn = findViewById(R.id.next_btn);
        previousBtn = findViewById(R.id.previous_btn);
        repeatBtn = findViewById(R.id.repeat_btn);
        imageView = findViewById(R.id.logo);

        lowerRelativeLayout = findViewById(R.id.lower);
        voiceEnabledButton = findViewById(R.id.voice_enabled_btn);
        songNameTxt = findViewById(R.id.songName);


        parentRelativeLayout = findViewById(R.id.parentRelativeLayout);
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(SmartPlayerActivity.this);
        speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        //  speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.US.toString());


        validateReceiveValuesAndStartPlaying();
        imageView.setBackgroundResource(R.drawable.two);

        @SuppressLint("ResourceType") final Animation animationRotateCenter = AnimationUtils.loadAnimation(
                this, rotate_center);
        imageView.startAnimation(animationRotateCenter);

        r = new Random();  // 1.

        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float v) {

            }

            @Override
            public void onBufferReceived(byte[] bytes) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int i) {

            }

            @Override
            public void onResults(Bundle bundle) {
                ArrayList<String> matchesFound = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

                if (matchesFound != null) {
                    if (mode.equals("ON")) {
                        keeper = matchesFound.get(0);


                        if (keeper.equals("pause") || keeper.equals("boss") || keeper.equals("course") || keeper.equals("Pause")) {
                            playPauseSong();
                            Toast.makeText(SmartPlayerActivity.this, "Command = " + keeper, Toast.LENGTH_LONG).show();
                        } else if (keeper.equals("play") || keeper.equals("Play")) {
                            playPauseSong();
                            Toast.makeText(SmartPlayerActivity.this, "Command = " + keeper, Toast.LENGTH_LONG).show();

                        } else if (keeper.equals("next") || keeper.equals("Next")) {
                            playNextSong();
                            Toast.makeText(SmartPlayerActivity.this, "Command = " + keeper, Toast.LENGTH_LONG).show();

                        } else if (keeper.equals("back") || keeper.equals("Beck") || keeper.equals("Back")) {

                            playPrevSong();
                            Toast.makeText(SmartPlayerActivity.this, "Command = " + keeper, Toast.LENGTH_LONG).show();

                        } else if (keeper.equals("help")){
                            help();
                        }
                        else{
                            Toast.makeText(SmartPlayerActivity.this, "Command = " + keeper, Toast.LENGTH_LONG).show();

                        }
                    }
                }
            } //триггеры на команды

            @Override
            public void onPartialResults(Bundle bundle) {

            }

            @Override
            public void onEvent(int i, Bundle bundle) {

            }

        });


        parentRelativeLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (mode.equals("ON")) {
                    switch (motionEvent.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            speechRecognizer.startListening(speechRecognizerIntent);
                            keeper = "";
                            break;

                        case MotionEvent.ACTION_UP:
                            speechRecognizer.stopListening();
                            break;
                    }
                    return false;
                }
                return false;
            }

        });


        pausePlayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playPauseSong();
            }
        });

        previousBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (myMediaPlayer.getCurrentPosition() > 0) {
                    playPrevSong();
                }
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (myMediaPlayer.getCurrentPosition() > 0) {
                    playNextSong();
                }
            }
        });

        repeatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (repeatState) {
                    case 1:
                        repeatState++;
                        repeatBtn.setImageResource(R.drawable.repeat_sm_one);
                        break;
                    case 2:
                        repeatState++;
                        repeatBtn.setImageResource(R.drawable.random);
                        break;
                    case 3:
                        repeatBtn.setImageResource(R.drawable.repeat_sm);
                        repeatState = 1;
                        break;
                }

            }
        });


        voiceEnabledButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mode.equals("ON")) {
                    mode = "OFF";
                    voiceEnabledButton.setText("Voice Enabled Mode - OFF");
                    lowerRelativeLayout.setVisibility(View.VISIBLE);
                } else {
                    mode = "ON";
                    voiceEnabledButton.setText("Voice Enabled Mode - ON");
                    lowerRelativeLayout.setVisibility(View.GONE);

                }
            }
        });


        myMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                Log.i("myLog", "onComplete hit");
                playNextSong();
            }
        });

    }



    @Override
    public void onBackPressed() {
        myMediaPlayer.stop();
        myMediaPlayer.release();
        super.onBackPressed();
    }

    private void validateReceiveValuesAndStartPlaying() {
        if (myMediaPlayer != null) {
            myMediaPlayer.stop();
            myMediaPlayer.release();
        }

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        mySongs = (ArrayList) bundle.getParcelableArrayList("song");
        mSongName = mySongs.get(position).getName();
        String songName = intent.getStringExtra("name");

        songNameTxt.setText(songName);
        songNameTxt.setSelected(true);

        position = bundle.getInt("position", 0);
        Uri uri = Uri.parse(mySongs.get(position).toString());

        myMediaPlayer = MediaPlayer.create(SmartPlayerActivity.this, uri);
        myMediaPlayer.start();


    }


    private void checkVoicePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!(ContextCompat.checkSelfPermission(SmartPlayerActivity.this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName()));
                startActivity(intent);
                finish();
            }
        }
    }

    private void help() {
        Toast.makeText(getApplicationContext(), "Commands:" + "\n"+
                "1. pause\n" +
                "2. play\n" +
                "3. back\n" +
                "4. next\n", Toast.LENGTH_LONG).show();
    }
    // helping toast for all voice commands


    private void playPauseSong() {
        imageView.setBackgroundResource(R.drawable.logo);
        if (myMediaPlayer.isPlaying()) {
            pausePlayBtn.setImageResource(R.drawable.play);
            myMediaPlayer.pause();

            imageView.clearAnimation();
        } else {


            imageView.startAnimation(animationRotateCenter);

            pausePlayBtn.setImageResource(R.drawable.pause);
            myMediaPlayer.start();
            imageView.setBackgroundResource(R.drawable.two);
        }
    }


    private void playNextSong() {
        myMediaPlayer.pause();
        myMediaPlayer.stop();
        myMediaPlayer.release();

        switch (repeatState) {
            case 1:
                position = ((position + 1) % mySongs.size());
                break;
            case 2:
                break;
            case 3:
                position = (int)(Math.random()*mySongs.size()); //CURRENT:
                        //LAST VERSION: r.nextInt(mySongs.size());
                Log.d("myLogs", String.valueOf(position)); // DEBUG SHEET
                break;
        } // логика работы режимов кнопки repeatBTN

        Uri uri = Uri.parse(mySongs.get(position).toString());

        myMediaPlayer = MediaPlayer.create(SmartPlayerActivity.this, uri);

        mSongName = mySongs.get(position).toString();
        songNameTxt.setText(mSongName);
        myMediaPlayer.start();

        imageView.startAnimation(animationRotateCenter);
        imageView.setBackgroundResource(R.drawable.two);

        if (myMediaPlayer.isPlaying()) {
            pausePlayBtn.setImageResource(R.drawable.pause);
        } else {


            pausePlayBtn.setImageResource(R.drawable.play);
            imageView.setBackgroundResource(R.drawable.two);
        }

        myMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                Log.i("myLog", "onComplete hit");
                playNextSong();
            }
        });

    }


    private void playPrevSong() {
        myMediaPlayer.pause();
        myMediaPlayer.stop();
        myMediaPlayer.release();

        position = ((position - 1) < 0 ? (mySongs.size() - 1) : (position - 1));

        Uri uri = Uri.parse(mySongs.get(position).toString());

        myMediaPlayer = MediaPlayer.create(SmartPlayerActivity.this, uri);

        mSongName = mySongs.get(position).toString();
        songNameTxt.setText(mSongName);
        myMediaPlayer.start();

        imageView.startAnimation(animationRotateCenter);
        imageView.setBackgroundResource(R.drawable.two);

        if (myMediaPlayer.isPlaying()) {
            pausePlayBtn.setImageResource(R.drawable.pause);
        } else {
            pausePlayBtn.setImageResource(R.drawable.play);
            imageView.setBackgroundResource(R.drawable.two);
        }
    }


}