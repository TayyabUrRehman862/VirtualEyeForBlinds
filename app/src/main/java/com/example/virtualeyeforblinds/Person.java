package com.example.virtualeyeforblinds;

import android.net.Uri;

public class Person {

    public int ID;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
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
