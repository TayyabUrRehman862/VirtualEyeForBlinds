package com.example.virtualeyeforblinds;

import android.graphics.Bitmap;
import android.net.Uri;
import android.widget.ImageView;

public class Place {
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Uri getImg() {
        return img;
    }

    public void setImg(Uri img) {
        this.img = img;
    }

    public String name,floor,type;
    Uri img;
}
