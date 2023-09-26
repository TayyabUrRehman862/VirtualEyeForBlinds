package com.example.virtualeyeforblinds.api;

import com.example.virtualeyeforblinds.Person;

import java.util.ArrayList;

public interface DataListener {
    void onDataReceived(ArrayList<Person> data);
    void onFailure();
}
