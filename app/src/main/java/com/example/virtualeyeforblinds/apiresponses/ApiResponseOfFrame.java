package com.example.virtualeyeforblinds.apiresponses;

import com.example.virtualeyeforblinds.models.Coordinate;

import java.util.ArrayList;

public class ApiResponseOfFrame {
    private String place;
    private String turn;

    private String doorDirection;

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

    public String getDoorDirection(){return doorDirection;}
}
