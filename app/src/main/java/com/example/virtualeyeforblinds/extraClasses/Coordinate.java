package com.example.virtualeyeforblinds.extraClasses;

public class Coordinate {
    private String position;
    private double steps;
    private double confidence;
    private String distance;
    private String label;

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    private int xmax;
    private int xmin;
    private int ymax;
    private int ymin;

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setXmax(int xmax) {
        this.xmax = xmax;
    }

    public void setXmin(int xmin) {
        this.xmin = xmin;
    }

    public void setYmax(int ymax) {
        this.ymax = ymax;
    }

    public void setYmin(int ymin) {
        this.ymin = ymin;
    }

    public double getConfidence() {
        return confidence;
    }

    public String getDistance() {
        return distance;
    }

    public String getLabel() {
        return label;
    }
    public void setSteps(double steps){
        this.steps=steps;
    }

    public double getSteps() {return steps;}

    public int getXmax() {
        return xmax;
    }

    public int getXmin() {
        return xmin;
    }

    public int getYmax() {
        return ymax;
    }

    public int getYmin() {
        return ymin;
    }
}
