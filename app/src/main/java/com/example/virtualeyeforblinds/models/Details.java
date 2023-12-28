package com.example.virtualeyeforblinds.models;

import android.widget.Spinner;

public class Details {


    public int directionId;

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



}
