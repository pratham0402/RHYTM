package com.example.demo;

public class categoryInfo {

    String cURL, cNAME, songCategory;

    public categoryInfo() {
    }

    public categoryInfo(String cURL, String cNAME, String songCategory) {
        this.cURL = cURL;
        this.cNAME = cNAME;
        this.songCategory = songCategory;
    }

    public String getcURL() {
        return cURL;
    }

    public void setcURL(String cURL) {
        this.cURL = cURL;
    }

    public String getcNAME() {
        return cNAME;
    }

    public void setcNAME(String cNAME) {
        this.cNAME = cNAME;
    }

    public String getSongCategory() {
        return songCategory;
    }

    public void setSongCategory(String songCategory) {
        this.songCategory = songCategory;
    }
}
