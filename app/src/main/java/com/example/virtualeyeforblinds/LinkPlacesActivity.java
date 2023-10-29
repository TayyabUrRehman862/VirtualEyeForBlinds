package com.example.virtualeyeforblinds;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.example.virtualeyeforblinds.api.RetrofitClient;
import com.example.virtualeyeforblinds.api.WebApi;
import com.example.virtualeyeforblinds.databinding.ActivityLinkPlacesBinding;
import com.example.virtualeyeforblinds.extraClasses.DataStorage;
import com.example.virtualeyeforblinds.extraClasses.Links;
import com.example.virtualeyeforblinds.progessbar.ProgressDialogFragment;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
        allLinks();
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


public void allLinks(){
    WebApi api= RetrofitClient.getInstance().getMyApi();
    api.getAllLinks().enqueue(new Callback<ArrayList<Links>>() {
        @Override
        public void onResponse(Call<ArrayList<Links>> call, Response<ArrayList<Links>> response) {
            if(response.isSuccessful()){
                DataStorage dataStorage=DataStorage.getInstance();
                dataStorage.setLinksArrayList(response.body());
            }
            else{
                Toast.makeText(placesActivity, "response was unsuccesful", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onFailure(Call<ArrayList<Links>> call, Throwable t) {
          //  Toast.makeText(placesActivity, t.getMessage(), Toast.LENGTH_SHORT).show();
        }
    });
}

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
                ProgressDialogFragment progressDialog = new ProgressDialogFragment();
                progressDialog.show(getSupportFragmentManager(), "progress_dialog");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();

                        Intent i = new Intent(getApplicationContext(), MatrixActivity.class);
                        DataStorage dataStorage = DataStorage.getInstance();

                        startActivity(i);
                    }
                }, 1000);
            }
        });
        binding.gridLayout.addView(button);




    }
}


}