package com.example.virtualeyeforblinds;

import android.graphics.Bitmap;
import android.net.Uri;
import android.widget.ImageView;

public class Place {

    public String floorname,typename;

    public String getFloorname() {
        return floorname;
    }

    public void setFloorname(String floorname) {
        this.floorname = floorname;
    }

    public String getTypename() {
        return typename;
    }

    public void setTypename(String typename) {
        this.typename = typename;
    }

    public Integer doordirectionid;

    public String doorDirectionName;

    public String getDoorDirectionName() {
        return doorDirectionName;
    }

    public void setDoorDirectionName(String doorDirectionName) {
        this.doorDirectionName = doorDirectionName;
    }

    public Integer id;

    public Integer floorid,typeid;


    public Integer getDoordirectionId() {
        return doordirectionid;
    }

    public void setDoordirectionId(int doordirectionid) {
        this.doordirectionid = doordirectionid;
    }

    public Integer getId() {
        return id;
    }

    public Integer getFloorid() {
        return floorid;
    }

    public void setFloorid(int floorid) {
        this.floorid = floorid;
    }

    public Integer getTypeid() {
        return typeid;
    }

    public void setTypeid(int typeid) {
        this.typeid = typeid;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }



    public Uri getImg() {
        return img;
    }

    public void setImg(Uri img) {
        this.img = img;
    }

    public String name;


    Uri img;
}
