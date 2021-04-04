package com.example.demo.Model;

public class uploadedSongs {

    String songCategory, songTitle, songArtist, songAlbumArt, songDur, songLink, mkey;

    public uploadedSongs(String songCategory, String songTitle, String songArtist, String songAlbumArt, String songDur, String songLink) {

        if (songTitle.trim().equals("")){
            songTitle = "No Title";
        }

        this.songCategory = songCategory;
        this.songTitle = songTitle;
        this.songArtist = songArtist;
        this.songAlbumArt = songAlbumArt;
        this.songDur = songDur;
        this.songLink = songLink;
    }

    public String getSongCategory() {
        return songCategory;
    }

    public void setSongCategory(String songCategory) {
        this.songCategory = songCategory;
    }

    public String getSongTitle() {
        return songTitle;
    }

    public void setSongTitle(String songTitle) {
        this.songTitle = songTitle;
    }

    public String getSongArtist() {
        return songArtist;
    }

    public void setSongArtist(String songArtist) {
        this.songArtist = songArtist;
    }

    public String getSongAlbumArt() {
        return songAlbumArt;
    }

    public void setSongAlbumArt(String songAlbumArt) {
        this.songAlbumArt = songAlbumArt;
    }

    public String getSongDur() {
        return songDur;
    }

    public void setSongDur(String songDur) {
        this.songDur = songDur;
    }

    public String getSongLink() {
        return songLink;
    }

    public void setSongLink(String songLink) {
        this.songLink = songLink;
    }

    public String getMkey() {
        return mkey;
    }

    public void setMkey(String mkey) {
        this.mkey = mkey;
    }
}
