package com.example.demo;

public class UserInfo {
    String name, email, bio, age,number;

    public UserInfo(){

    }

    public UserInfo(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public UserInfo(String name, String email, String bio, String age, String number) {
        this.name = name;
        this.email = email;
        this.bio = bio;
        this.age = age;
        this.number = number;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
}

