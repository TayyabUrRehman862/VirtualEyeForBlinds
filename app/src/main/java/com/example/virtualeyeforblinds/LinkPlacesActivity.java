package com.example.virtualeyeforblinds;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.example.virtualeyeforblinds.databinding.ActivityLinkPlacesBinding;

import java.util.ArrayList;

public class LinkPlacesActivity extends AppCompatActivity {

    int countx,county=0;

    ActivityLinkPlacesBinding binding;

    static ArrayList<String> listOfPlaces=new ArrayList<>();

    PlacesActivity placesActivity=new PlacesActivity();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityLinkPlacesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
//        PlacesActivity p=new PlacesActivity();
//        p.LinkFloors.add("Floor1");
//        p.LinkFloors.add("Floor2");
//        p.LinkFloors.add("Floor3");
//        listOfPlaces=placesActivity.LinkFloors;
        listOfPlaces.add("Floor1");
        listOfPlaces.add("Floor2");
        listOfPlaces.add("Floor3");
        listOfPlaces.add("Floor4");
        listOfPlaces.add("Floor5");
        listOfPlaces.add("Floor6");
        listOfPlaces.add("Floor7");

        createDynamicGridView();
    }

//    public void createDynamicGridView(){
//        binding.gridLayout.setColumnCount(2);
//        for (int i = 0; i < listOfPlaces.size(); i++) {
//
//            Button button = new Button(this);
//            button.setText(listOfPlaces.get(i));
//
//            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
//            params.height=500;
//            params.width=500;
//
//            params.columnSpec = GridLayout.spec(i % 2 == 0 ? 0 : 1);
//            params.rowSpec = GridLayout.spec(i / 2);
//            button.setLayoutParams(params);
//            binding.gridLayout.addView(button);
//
//        }
//    }
public void createDynamicGridView() {


    binding.gridLayout.setColumnCount(2);


    // Define an ArrayList to store the colors
    ArrayList<Integer> buttonColors = new ArrayList<>();

    // Assign two colors for the buttons
    int color1 = getColor(R.color.blue_p);
    int color2 = getColor(R.color.orange);
    buttonColors.add(color1);
    buttonColors.add(color2);
    buttonColors.add(color2);
    buttonColors.add(color1);


    for (int i = 0; i < listOfPlaces.size(); i++) {

        Button button = new Button(this);
        button.setId(i);
        button.setText(listOfPlaces.get(i));
        button.setTextColor(getColor(R.color.white));

        // Assign color to the button based on the index
        int colorIndex = i % 4;
        button.setBackgroundColor(buttonColors.get(colorIndex));

        GridLayout.LayoutParams params = new GridLayout.LayoutParams();

        params.setMargins(20,30,20,30);
        params.height = 500;
        params.width = 500;
        params.setGravity(Gravity.CENTER);
        params.columnSpec = GridLayout.spec(i % 2 == 0 ? 0 : 1);
        params.rowSpec = GridLayout.spec(i / 2);

        button.setLayoutParams(params);

        final int j=i;
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(LinkPlacesActivity.this,MatrixActivity.class);
                startActivity(i);
            }
        });
        binding.gridLayout.addView(button);




    }
}


}