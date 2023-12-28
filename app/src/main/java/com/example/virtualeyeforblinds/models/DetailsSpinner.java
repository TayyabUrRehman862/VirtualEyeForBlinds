package com.example.virtualeyeforblinds.models;

import android.widget.Spinner;

import java.util.ArrayList;

public class DetailsSpinner {


    public Spinner sp;
    public int directionId;

    public Spinner getSpinner() {
        return sp;
    }

    public void setSpinner(Spinner spinner) {
        this.sp = spinner;
    }

    public int id;

    public int source;

    public int destination;

    public int getSource() {
        return source;
    }

    public void setSource(int source) {
        this.source = source;
    }

    public int getDestination() {
        return destination;
    }

    public void setDestination(int destination) {
        this.destination = destination;
    }

    public void setDirectionId(int directionId){
        this.directionId=directionId;
    }
    public int getDirectionId(){
        return directionId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


}
