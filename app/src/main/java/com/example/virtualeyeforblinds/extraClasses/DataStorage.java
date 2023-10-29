package com.example.virtualeyeforblinds.extraClasses;

import com.example.virtualeyeforblinds.Person;
import com.example.virtualeyeforblinds.Place;

import java.util.ArrayList;

public class DataStorage {
    private static DataStorage instance;
    public String sharedData;

    public ArrayList<Links> linksArrayList;

    public ArrayList<Links> getLinksArrayList() {
        return linksArrayList;
    }

    public void setLinksArrayList(ArrayList<Links> linksArrayList) {
        this.linksArrayList = linksArrayList;
    }

    public ArrayList<Person> personArrayList;

    public ArrayList<Place> placesArrayList;

    public ArrayList<Place> getPlacesArrayList() {
        return placesArrayList;
    }

    public void setPlacesArrayList(ArrayList<Place> placesArrayList) {
        this.placesArrayList = placesArrayList;
    }

    private DataStorage() {
        // Private constructor to prevent instantiation from outside
    }

    public static DataStorage getInstance() {
        if (instance == null) {
            synchronized (DataStorage.class) {
                if (instance == null) {
                    instance = new DataStorage();
                }
            }
        }
        return instance;
    }

    public void setSharedData(String data) {
        this.sharedData = data;
    }
    public void setPersonArrayList(ArrayList<Person> personArrayList){
        this.personArrayList=personArrayList;
    }

    public String getSharedData() {
        return sharedData;
    }
    public ArrayList<Person> getPersonArrayList(){
        return personArrayList;
    }
}

