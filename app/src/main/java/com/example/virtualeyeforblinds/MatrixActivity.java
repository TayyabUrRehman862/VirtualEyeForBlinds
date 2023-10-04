package com.example.virtualeyeforblinds;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;


import com.example.virtualeyeforblinds.databinding.ActivityMatrixBinding;

import java.util.ArrayList;
import java.util.List;

public class MatrixActivity extends AppCompatActivity {
    ActivityMatrixBinding binding;

    private TableLayout tableLayout;

    private boolean ijvalue=false;

    private ArrayList<String> places;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityMatrixBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        places=new ArrayList<>();

        tableLayout=findViewById(R.id.tableLayoutformatrix);
        populateArrayList();


    }
    private void populateArrayList(){
        places.add("Lab1");
        places.add("Lab2");
        places.add("Lab3");
        places.add("Lab4");
        places.add("Lab5");
        places.add("Lt1");
        places.add("Lt2");
        places.add("Lt3");
        places.add("Lt4");
        places.add("Lt5");
        int size=places.size();
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
                    params.width=120;
                    params.height=120;
                    TextView tv = new TextView(this);

                    tv.setBackground(getDrawable(R.drawable.cell_backgroud_colored));
                    tv.setTextColor(getColor(R.color.black));
                    tableRow.addView(tv, params);

                }

                else if (j == 0 && i > 0) {

                    //problem is here
                    TableRow.LayoutParams params = new TableRow.LayoutParams(
                            TableLayout.LayoutParams.WRAP_CONTENT,
                            TableLayout.LayoutParams.WRAP_CONTENT);
                    params.width=120;
                    params.height=120;

                    //params.setMargins(0, -5, 0, 0);

                    TextView tv = new TextView(this);
                    tv.setGravity(Gravity.CENTER);
                    //tv.setForegroundGravity(Gravity.FILL);
                    tv.setText(places.get(i-1));
                    tv.setBackground(getDrawable(R.drawable.cell_backgroud_colored));
                    tv.setTextColor(getColor(R.color.black));
                    tableRow.addView(tv, params);
                    //ijvalue=true;
                }
                else if((i == 0 && j>0)){
                    TableRow.LayoutParams params = new TableRow.LayoutParams(
                            TableLayout.LayoutParams.WRAP_CONTENT,
                            TableLayout.LayoutParams.WRAP_CONTENT);
                    params.height=120;
                    params.width=120;
                    //params.setMargins(0, 0, 0, 0);

                    TextView tv = new TextView(this);
                    tv.setText(places.get(j-1));
                    //tv.setGravity(Gravity.CENTER);
                    tv.setBackground(getDrawable(R.drawable.cell_backgroud_colored));
                    tv.setTextColor(getColor(R.color.black));
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
                    params.width=120;
                    params.height=120;
                    //params.setMargins(0, 0, 0, 0);  // Add some margin between cells

                    // Set the custom cell background
                    spinner.setBackgroundResource(R.drawable.cell_background);

                    tableRow.addView(spinner, params);

                }


                    else {
                        Spinner spinner = new Spinner(this);
                        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
                                android.R.layout.simple_spinner_item,
                                new String[]{"None", "West", "North","South","East"});
                        spinner.setAdapter(spinnerAdapter);

                        TableRow.LayoutParams params = new TableRow.LayoutParams(
                                TableLayout.LayoutParams.WRAP_CONTENT,
                                TableLayout.LayoutParams.WRAP_CONTENT);
                        params.width=120;
                        params.height=120;
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


}