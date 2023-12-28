package com.example.virtualeyeforblinds.models;

public class Links {

    public int id;

    public int directionid;

    public int destination;

    public int steps;

    public int source;

    public int getSource() {
        return source;
    }

    public void setSource(int source) {
        this.source = source;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDirectionid() {
        return directionid;
    }

    public void setDirectionid(int directionid) {
        this.directionid = directionid;
    }

    public int getDestination() {
        return destination;
    }

    public void setDestination(int destination) {
        this.destination = destination;
    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }
}
