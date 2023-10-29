package com.example.virtualeyeforblinds;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;


import com.example.virtualeyeforblinds.api.RetrofitClient;
import com.example.virtualeyeforblinds.api.WebApi;
import com.example.virtualeyeforblinds.databinding.ActivityMatrixBinding;
import com.example.virtualeyeforblinds.extraClasses.DataStorage;
import com.example.virtualeyeforblinds.extraClasses.DetailsSpinner;
import com.example.virtualeyeforblinds.extraClasses.Links;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MatrixActivity extends AppCompatActivity {


    DataStorage d=DataStorage.getInstance();
    ArrayList<Integer> allId=new ArrayList<>();

    ArrayList<DetailsSpinner> allDataofMatrix=new ArrayList<>();
    ArrayList<Spinner> simpleSpinner=new ArrayList<>();

    int id=0;
    ActivityMatrixBinding binding;

    private TableLayout tableLayout;

    private boolean ijvalue=false;

    private ArrayList<Links> places;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            binding = ActivityMatrixBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());
            //allSpinners=new ArrayList<>();
            places = new ArrayList<Links>();

            tableLayout = findViewById(R.id.tableLayoutformatrix);
            populateArrayList();
            try {
                binding.btnOnClickSaveMatrix.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        for (int i = 0; i < simpleSpinner.size(); i++) {
                            int o = 0;
                            String direction = simpleSpinner.get(i).getSelectedItem().toString();
                            if (direction.equalsIgnoreCase("none")) {
                                o = 1;
                            } else if (direction.equalsIgnoreCase("west")) {
                                o = 4;
                            } else if (direction.equalsIgnoreCase("east")) {
                                o = 3;
                            } else if (direction.equalsIgnoreCase("south")) {
                                o = 2;
                            } else if (direction.equalsIgnoreCase("north")) {
                                o = 1;
                            }
                            DetailsSpinner d = new DetailsSpinner();
                            d.id = places.get(i).id;
                            d.directionId = o;
                            allDataofMatrix.add(d);

                        }
                        WebApi api = RetrofitClient.getInstance().getMyApi();
                        api.createOrUpdateMatrix(allDataofMatrix).enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {
                                if (response.isSuccessful()) {
                                    String s = response.body();
                                    Toast.makeText(MatrixActivity.this, "Data added", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(MatrixActivity.this, "Data not added", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<String> call, Throwable t) {
                                Toast.makeText(MatrixActivity.this, "APi failure", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }catch (Exception e){
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            binding.btnOnClickCancelMatrix.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });

        }catch (Exception e){
            Log.println(Log.ASSERT,"MatrixActivity",e.getMessage());
        }

    }




    private void populateArrayList(){

        places=d.getLinksArrayList();


        int size=d.getLinksArrayList().size();
        //Toast.makeText(this, size+"", Toast.LENGTH_SHORT).show();
        size=size+1;
        createTable(size,size);
    }

    private void createTable(int rows, int columns) {
        for (int i = 0; i < rows; i++) {
            ijvalue=false;
            TableRow tableRow = new TableRow(this);
            tableRow.setLayoutParams(new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.WRAP_CONTENT,
                    TableLayout.LayoutParams.WRAP_CONTENT));


            for (int j = 0; j < columns; j++) {
                if(i==0 && j==0){
                    TableRow.LayoutParams params = new TableRow.LayoutParams(
                            TableLayout.LayoutParams.WRAP_CONTENT,
                            TableLayout.LayoutParams.WRAP_CONTENT);

                    //params.setMargins(0, 0, 0, 0);
                    params.width=180;
                    params.height=180;
                    TextView tv = new TextView(this);
                    tv.setGravity(Gravity.CENTER);
                    tv.setBackground(getDrawable(R.drawable.cell_backgroud_colored));
                    tv.setTextColor(getColor(R.color.black));
                    tableRow.addView(tv, params);

                }

                else if (j == 0 && i > 0) {

                    //problem is here
                    TableRow.LayoutParams params = new TableRow.LayoutParams(
                            TableLayout.LayoutParams.WRAP_CONTENT,
                            TableLayout.LayoutParams.WRAP_CONTENT);
                    params.width=180;
                    params.height=180;

                    //params.setMargins(0, -5, 0, 0);

                    TextView tv = new TextView(this);
                    tv.setGravity(Gravity.CENTER);
                    //tv.setForegroundGravity(Gravity.FILL);

                    tv.setText(places.get(i-1).source);
                    tv.setBackground(getDrawable(R.drawable.cell_backgroud_colored));
                    tv.setTextColor(getColor(R.color.white));
                    tableRow.addView(tv, params);
                    //ijvalue=true;
                }
                else if((i == 0 && j>0)){
                    TableRow.LayoutParams params = new TableRow.LayoutParams(
                            TableLayout.LayoutParams.WRAP_CONTENT,
                            TableLayout.LayoutParams.WRAP_CONTENT);
                    params.width=180;
                    params.height=180;
                    //params.setMargins(0, 0, 0, 0);

                    TextView tv = new TextView(this);
                    tv.setText(places.get(j-1).source);
                    tv.setGravity(Gravity.CENTER);
                    tv.setBackground(getDrawable(R.drawable.cell_backgroud_colored));
                    tv.setTextColor(getColor(R.color.white));
                    tableRow.addView(tv, params);

                } else if(i==j && i!=0 && j!=0){

                    Spinner spinner = new Spinner(this);
                    ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
                            android.R.layout.simple_spinner_item,
                            new String[]{"None"});
                    spinner.setAdapter(spinnerAdapter);

                    TableRow.LayoutParams params = new TableRow.LayoutParams(
                            TableLayout.LayoutParams.WRAP_CONTENT,
                            TableLayout.LayoutParams.WRAP_CONTENT);
                    //allSpinners.add(null);
                    params.width=180;
                    params.height=180;
                    //params.setMargins(0, 0, 0, 0);  // Add some margin between cells

                    // Set the custom cell background
                    spinner.setBackgroundResource(R.drawable.cell_background);

                    tableRow.addView(spinner, params);

                }


                    else {
                    DetailsSpinner detailsSpinner=new DetailsSpinner();
                        //String src=places.get(i-1).source;
                        //String dst=places.get(j-1).destination;
                        String s=getDirection(places.get(i-1).source,places.get(j-1).destination);
                        Spinner spinner = new Spinner(this);
                        //counter=counter+1;

                        if(s.equalsIgnoreCase("east")){
                            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
                                    android.R.layout.simple_spinner_item,
                                    new String[]{s, "west", "north","south","None"});
                            spinner.setAdapter(spinnerAdapter);
                        }
                        else if(s.equalsIgnoreCase("west")){
                            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
                                    android.R.layout.simple_spinner_item,
                                    new String[]{s, "east", "north","south","None"});
                            spinner.setAdapter(spinnerAdapter);
                        }
                        else if(s.equalsIgnoreCase("north")){
                            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
                                    android.R.layout.simple_spinner_item,
                                    new String[]{s, "west", "east","south","None"});
                            spinner.setAdapter(spinnerAdapter);
                        }
                        else if(s.equalsIgnoreCase("south")){
                            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
                                    android.R.layout.simple_spinner_item,
                                    new String[]{s, "west", "north","east","None"});
                            spinner.setAdapter(spinnerAdapter);
                        }
                        else{
                            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
                                    android.R.layout.simple_spinner_item,
                                    new String[]{s, "east", "west","north","south"});
                            spinner.setAdapter(spinnerAdapter);
                        }

                        //allSpinners.add(spinner);

                        //detailsSpinner.allSpinners.add(spinner);
                        //detailsSpinner.id=id;
                    Log.e("before saving ",id+"");
                    allId.add(id);
                       // detailsSpinner.source=places.get(i-1).source;
                        //detailsSpinner.destination=places.get(j-1).destination;
                        simpleSpinner.add(spinner);
                        //detailsSpinner.id=places.



                        TableRow.LayoutParams params = new TableRow.LayoutParams(
                                TableLayout.LayoutParams.WRAP_CONTENT,
                                TableLayout.LayoutParams.WRAP_CONTENT);
                    params.width=180;
                    params.height=180;
                        //params.setMargins(0, 0, 0, 0);  // Add some margin between cells

                        // Set the custom cell background
                        spinner.setBackgroundResource(R.drawable.cell_background);

                        tableRow.addView(spinner, params);

                        // Add vertical lines between cells
//                        if (j < columns - 1) {
//                            View verticalLine = new View(this);
//                            TableRow.LayoutParams verticalParams = new TableRow.LayoutParams(
//                                    2,  // Adjust the line thickness as needed
//                                    TableRow.LayoutParams.MATCH_PARENT);
//                            verticalLine.setBackgroundColor(getResources().getColor(android.R.color.black));
//                            tableRow.addView(verticalLine, verticalParams);
//                        }
                    }
                }

                // Add the TableRow to the TableLayout
                tableLayout.addView(tableRow);

                // Add horizontal lines after each row except the last one
//                if (i < rows - 1) {
//                    if(i==0) {
//                    }
//                    else {
//                        TableRow horizontalLine = new TableRow(this);
//                        TableRow.LayoutParams horizontalParams = new TableRow.LayoutParams(
//                                TableRow.LayoutParams.MATCH_PARENT,
//                                2);  // Adjust the line thickness as needed
//                        horizontalLine.setBackgroundColor(getResources().getColor(android.R.color.black));
//                        tableLayout.addView(horizontalLine, horizontalParams);
//                    }
//                }

        }
    }

public String getDirection(String i, String j){
        for(int k=0;k<places.size();k++){

            if(places.get(k).source.equalsIgnoreCase(i) && places.get(k).destination.equalsIgnoreCase(j)){

                allId.add(places.get(k).id);
                Log.e("id",id+"");
                return places.get(k).directionid;
            }

        }
        return " ";
}

}