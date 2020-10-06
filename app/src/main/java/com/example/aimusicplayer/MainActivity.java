package com.example.aimusicplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private String[] itemsAll;
    private ListView mSongsList;

    private List<Song> songs = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        mSongsList = findViewById(R.id.songsList);


        appExternalStoragePermission();

    }

    public void appExternalStoragePermission() {
        Dexter.withContext(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        displayAudioSongsName();

                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {


                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission,
                                                                   PermissionToken token) {
                        token.continuePermissionRequest();

                    }
                }).check();
    }

    public ArrayList<File> readOnlyAudioSongs(File file) {
        ArrayList<File> arrayList = new ArrayList<>();

        File[] allFiles = file.listFiles();

        for (File individualFile : allFiles) {
            if (individualFile.isDirectory() && !individualFile.isHidden()) {
                arrayList.addAll(readOnlyAudioSongs(individualFile));
            } else {
                if (individualFile.getName().endsWith(".mp3")) { //|| (individualFile.getName().endsWith(".aac")) || (individualFile.getName().endsWith(".wav")) || (individualFile.getName().endsWith(".wma"))) { //OPTIONAL AUDIO FILES
                    arrayList.add(individualFile);
                }
            }
        }

        return arrayList;
    }


    private void displayAudioSongsName() {
        final ArrayList<File> audioSongs = readOnlyAudioSongs(Environment.getExternalStorageDirectory());

        itemsAll = new String[audioSongs.size()];

        for (int songCounter = 0; songCounter < audioSongs.size(); songCounter++) {
            itemsAll[songCounter] = audioSongs.get(songCounter).getName();
        }

        songs = setSongs(itemsAll); //CREATING SONGS LIST EXTENDED WITH ARTIST, SONGNAME AND LOGO


        SongAdapter songAdapter = new SongAdapter(MainActivity.this,
               R.layout.list_item, songs);
        mSongsList.setAdapter(songAdapter);

        mSongsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long l) {
                String songName = itemsAll[i].substring(0, itemsAll[i].length()-4);

                Intent intent = new Intent(MainActivity.this, SmartPlayerActivity.class);
                intent.putExtra("song", audioSongs);
                intent.putExtra("name", songName);
                intent.putExtra("position", i);

                startActivity(intent);
            }
        });


         /*
        // WORKING PART
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(MainActivity.this,
                android.R.layout.simple_list_item_1, itemsAll);
        mSongsList.setAdapter(arrayAdapter);

        mSongsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String songName = mSongsList.getItemAtPosition(i).toString();

                Intent intent = new Intent(MainActivity.this, SmartPlayerActivity.class);
                intent.putExtra("song", audioSongs);
                intent.putExtra("name", songName);
                intent.putExtra("position", i);

                startActivity(intent);
            }
        });



          */
    }

    private List<Song> setSongs(String[] info) {

        ArrayList<Song> songs = new ArrayList<>();

        for (int i = 0; i < info.length; i++)
        {
            String artist = getArtist(info, i);
            String songName = getSongName(info, i);

            songs.add(new Song(artist, songName, R.drawable.neon));
        }

            return songs;
    }

    /**
     * parse from all track info songName
     * info is ararayList with all track info
     * i is current position to parse
     * @param info
     * @param i
     * @return songName
     */

    private String getSongName(String[] info, int i) {
        String[] songName;
        songName = info[i].split("-", 2);
        String res = "";
        try {
           res = songName[1].substring(0, songName[1].length() - 4);

           if(res.charAt(0) == ' ')
           {
               res = res.substring(1);
           }

        }
        catch (Exception e)
        { }

        if(res == null || res == "" || res == " ")
        {
            return "UNKNOWN NAME";
        }

        return res;
    }

    /**
     * parse from all track info artist
     * info is ararayList with all track info
     * i is current position to parse
     * @param info
     * @param i
     * @return artist
     */
    private String getArtist(String[] info, int i) {
        String[] artist = null;
        try {
            artist = info[i].split("-", 2);
        }
        catch (Exception e)
        {

        }

        if(artist == null || artist[0] == "" || artist[0] == " ")
        {
            return "UNKNOWN ARTIST";
        }

        return artist[0];
    }

}