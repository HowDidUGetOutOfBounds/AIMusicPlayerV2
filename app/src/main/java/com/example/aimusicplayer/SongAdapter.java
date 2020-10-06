package com.example.aimusicplayer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class SongAdapter extends ArrayAdapter<Song> {

    private LayoutInflater inflater;
    private int layout;
    private List<Song> songs;

    public SongAdapter(@NonNull Context context, int resource, @NonNull List<Song> songs) {
        super(context, resource, songs);
        this.songs = songs;;
        this.layout = resource;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view=inflater.inflate(this.layout, parent, false);

        ImageView coverView =  view.findViewById(R.id.cover);
        TextView songNameView =  view.findViewById(R.id.songName);
        TextView artistView =  view.findViewById(R.id.artist);

        songNameView.setSelected(true); // для вращения
        artistView.setSelected(true); // для вращения

        Song song = songs.get(position);

        coverView.setImageResource(song.getTrackPicture());
        songNameView.setText(song.getSongName());
        artistView.setText(song.getArtist());

        return view;
    }
}
