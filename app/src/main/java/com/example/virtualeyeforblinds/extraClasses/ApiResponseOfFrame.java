package com.example.virtualeyeforblinds.extraClasses;

import java.util.ArrayList;
import java.util.Dictionary;

public class ApiResponseOfFrame {
    private String place;
    private String turn;

    private ArrayList<Coordinate> coordinates;


//    public void set(String p,String t){
//        palce=p;
//        turn=t;
//    }

    public String getPlace() {
        return place;
    }

    public String getTurn() {
        return turn;
    }

    public ArrayList<Coordinate> getCoordinates(){
        return coordinates;
    }
}
