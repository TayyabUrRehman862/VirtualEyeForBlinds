package com.example.virtualeyeforblinds;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.virtualeyeforblinds.databinding.ActivityMainBinding;
import com.example.virtualeyeforblinds.databinding.ActivityMatrixBinding;

import java.util.ArrayList;
import java.util.List;

public class MatrixActivity extends AppCompatActivity {

    private GridLayout gl;
    private List<String> itemList;

    ActivityMatrixBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMatrixBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //gl=findViewById(R.id.gridLayoutMatrix);
        gl = binding.gridLayoutMatrix;
        itemList = new ArrayList<>();
        itemList.add("Item 1");
        itemList.add("Item 2");
        itemList.add("Item 3");
        itemList.add("Item 4");
        gl.setColumnCount(itemList.size());
        gl.setRowCount(itemList.size());
        for (int i = 0; i < itemList.size(); i++) {
            for (int j = 0; j < itemList.size(); j++) {
                Button b = new Button(this);
                b.setText(j + "");


            }

        }

    }


}