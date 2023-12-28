package com.example.virtualeyeforblinds.apiresponses;

import com.example.virtualeyeforblinds.models.Coordinate;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class ApiResponseOfPerson {



    public Coordinate getScreenCoordinates(int screenWidth, int screenHeight, int tempheight, int tempwidth) {
        Coordinate coordinate = new Coordinate();

        // Map API coordinates to screen coordinates
        int left = (int) (xmin * ((float) screenWidth / tempwidth));
        int top = (int) (ymin * ((float) screenHeight / tempheight));
        int right = (int) (xmax * ((float) screenWidth / tempwidth));
        int bottom = (int) (ymax * ((float) screenHeight / tempheight));

        coordinate.setXmax(right);
        coordinate.setXmin(left);
        coordinate.setYmax(top);
        coordinate.setYmin(bottom);

        // Set other necessary properties like label, distance, steps, position, etc.
        coordinate.setLabel(person);
        // Assuming other necessary data is available in ApiResponseOfPerson

        return coordinate;
    }

    public String person;

    public String getPerson() {
        return person;
    }

    public void setPerson(String person) {
        this.person = person;
    }

    public ArrayList<String> dress_colors;

    public ArrayList<String> left;

    public ArrayList<String> right;

    public Integer xmin;

    public Integer xmax;

    public Integer ymin;

    public Integer ymax;

    public ArrayList<String> getDress_colors() {
        return dress_colors;
    }

    public void setDress_colors(ArrayList<String> dress_colors) {
        this.dress_colors = dress_colors;
    }

    public ArrayList<String> getLeft() {
        return left;
    }

    public void setLeft(ArrayList<String> left) {
        this.left = left;
    }

    public ArrayList<String> getRight() {
        return right;
    }

    public void setRight(ArrayList<String> right) {
        this.right = right;
    }

    public Integer getXmin() {
        return xmin;
    }

    public void setXmin(Integer xmin) {
        this.xmin = xmin;
    }

    public Integer getXmax() {
        return xmax;
    }

    public void setXmax(Integer xmax) {
        this.xmax = xmax;
    }

    public Integer getYmin() {
        return ymin;
    }

    public void setYmin(Integer ymin) {
        this.ymin = ymin;
    }

    public Integer getYmax() {
        return ymax;
    }

    public void setYmax(Integer ymax) {
        this.ymax = ymax;
    }
}
