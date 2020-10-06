package com.example.aimusicplayer;

public class Song {
    private String artist; // artist
    private String songName; // track name
    private int trackPicture; // track picture


    public Song(String artist, String songName, int trackPicture) {
        this.artist = artist;
        this.songName = songName;
        this.trackPicture = trackPicture;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public int getTrackPicture() {
        return trackPicture;
    }

    public void setTrackPicture(int trackPicture) {
        this.trackPicture = trackPicture;
    }

}
