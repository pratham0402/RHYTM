package com.example.demo.Model;

public class UploadImg {

    public String I_name, I_url, I_songCategory;

    public UploadImg(String i_name, String i_url, String i_songCategory) {
        I_name = i_name;
        I_url = i_url;
        I_songCategory = i_songCategory;
    }

    public String getI_name() {
        return I_name;
    }

    public void setI_name(String i_name) {
        I_name = i_name;
    }

    public String getI_url() {
        return I_url;
    }

    public void setI_url(String i_url) {
        I_url = i_url;
    }

    public String getI_songCategory() {
        return I_songCategory;
    }

    public void setI_songCategory(String i_songCategory) {
        I_songCategory = i_songCategory;
    }
}
