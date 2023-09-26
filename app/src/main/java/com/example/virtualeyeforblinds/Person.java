package com.example.virtualeyeforblinds;

import android.net.Uri;

public class Person {
    public String pname;

    public Uri img;

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }
    public void setImgPerson(Uri img){
        this.img=img;
    }
    public Uri getImgPerson(){
        return img;
    }
}
