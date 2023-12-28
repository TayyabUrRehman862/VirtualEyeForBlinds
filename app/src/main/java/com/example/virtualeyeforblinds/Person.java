package com.example.virtualeyeforblinds;

import android.net.Uri;

public class Person {

    public Integer id;

    public int getID() {
        return id;
    }

    public void setID(int ID) {
        this.id = ID;
    }

    public String Name;

    public Uri img;

    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }
    public void setImgPerson(Uri img){
        this.img=img;
    }
    public Uri getImgPerson(){
        return img;
    }
}
