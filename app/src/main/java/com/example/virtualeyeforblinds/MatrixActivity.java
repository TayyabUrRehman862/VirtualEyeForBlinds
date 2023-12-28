package com.example.virtualeyeforblinds;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.os.HandlerKt;

import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;


import com.example.virtualeyeforblinds.api.RetrofitClient;
import com.example.virtualeyeforblinds.api.WebApi;
import com.example.virtualeyeforblinds.databinding.ActivityMatrixBinding;
import com.example.virtualeyeforblinds.extraClasses.NetworkUtils;
import com.example.virtualeyeforblinds.models.Details;
import com.example.virtualeyeforblinds.models.Direction;
import com.example.virtualeyeforblinds.models.SimpleResponse;
import com.example.virtualeyeforblinds.models.Types;
import com.example.virtualeyeforblinds.models.floors;
import com.example.virtualeyeforblinds.globalClass.DataStorage;
import com.example.virtualeyeforblinds.models.DetailsSpinner;
import com.example.virtualeyeforblinds.models.Links;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MatrixActivity extends AppCompatActivity {

    private static final long MAX_WAIT_TIME = 20000; // Maximum wait time in milliseconds
    private static final long DELAY_CHECK_INTERVAL = 1000; // Delay interval for checking data in milliseconds


    HashMap<DetailsSpinner, String> originalSpinnerValues = new HashMap<>();

    public static ArrayList<Links> tobeUpdated = new ArrayList<>();

    public static ArrayList<Links> tobeDeleted=new ArrayList<>();

    //ArrayList<Links> temporary=new ArrayList<>();
    ArrayList<Direction> directions = new ArrayList<>();
    ArrayList<Place> places = new ArrayList<>();
    //ArrayList<Integer> index=new ArrayList<>();
    DataStorage d = DataStorage.getInstance();
    ArrayList<Integer> allId = new ArrayList<>();
    ArrayList<DetailsSpinner> allDataofMatrix = new ArrayList<>();
    ArrayList<Spinner> simpleSpinner = new ArrayList<>();
    int id = 0;
    ActivityMatrixBinding binding;
    int counter = 0;
    private TableLayout tableLayout;
    private boolean ijvalue = false;
    private ArrayList<Links> linkslist;

    // Define this method to set the spinner back to its original value



    public void updatingLinks(ArrayList<Links> l){
        try{


            if (!NetworkUtils.isNetworkAvailable(getApplicationContext())) {
                // Handle the case when network is not available
                Toast.makeText(getApplicationContext(), "Network unavailable", Toast.LENGTH_SHORT).show();
                Log.e("Network Unavailable", "Please check your internet connection");
                return;
            }

                    for(int i=0;i<l.size();i++){



                        WebApi api=RetrofitClient.getInstance().getMyApi();

                        api.updateLink(l.get(i).id,l.get(i)).enqueue(new Callback<SimpleResponse>() {
                            @Override
                            public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> response) {

                                if(response.isSuccessful()){
                                    DataStorage.getInstance().getAllLinks();
                                    Log.e("update successful of links","success");

                                }
                                else {


//                                    try {
//                                        String previousDirection = "";
//                                        for (int j = 0; j < d.getLinksArrayList().size(); j++) {
//                                            if (d.getLinksArrayList().get(j).id == l.get(j).id) {
//                                                previousDirection = DataStorage.getInstance().getDirectionName(d.getLinksArrayList().get(j).directionid);
//                                            }
//                                        }
//                                        for (int k = 0; k < allDataofMatrix.size(); k++) {
//                                            if (allDataofMatrix.get(k).id == l.get(finalI).id) {
//                                                Spinner spinner = new Spinner(getApplicationContext());
//                                                if (previousDirection.equalsIgnoreCase("south")) {
//                                                    ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getApplicationContext(),
//                                                            android.R.layout.simple_spinner_item,
//                                                            new String[]{"south", "west", "north", "east", "None"});
//                                                    spinner.setAdapter(spinnerAdapter);
//                                                } else if (previousDirection.equalsIgnoreCase("east")) {
//                                                    ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getApplicationContext(),
//                                                            android.R.layout.simple_spinner_item,
//                                                            new String[]{"east", "west", "north", "south", "None"});
//                                                    spinner.setAdapter(spinnerAdapter);
//                                                } else if (previousDirection.equalsIgnoreCase("west")) {
//                                                    ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getApplicationContext(),
//                                                            android.R.layout.simple_spinner_item,
//                                                            new String[]{"west", "south", "north", "east", "None"});
//                                                    spinner.setAdapter(spinnerAdapter);
//                                                } else if (previousDirection.equalsIgnoreCase("north")) {
//                                                    ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getApplicationContext(),
//                                                            android.R.layout.simple_spinner_item,
//                                                            new String[]{"north", "west", "south", "east", "None"});
//                                                    spinner.setAdapter(spinnerAdapter);
//                                                }
//
//
//                                                allDataofMatrix.get(k).setSpinner(spinner);
//                                            }
//                                        }
//
//                                        Log.e("error in updating links", "links not updated");
//                                    }catch (Exception e){
//                                        e.printStackTrace();
//                                    }
                                }

                            }

                            @Override
                            public void onFailure(Call<SimpleResponse> call, Throwable t) {



                                Log.e("Api failure in Updating links",t.getMessage());
                            }
                        });


                    }
                    if(tobeUpdated.size()!=0){
                        tobeUpdated.clear();
                    }



        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void deletingLinks(ArrayList<Links> l){
        try {

            if (!NetworkUtils.isNetworkAvailable(getApplicationContext())) {
                // Handle the case when network is not available
                Toast.makeText(getApplicationContext(), "Network unavailable", Toast.LENGTH_SHORT).show();
                Log.e("Network Unavailable", "Please check your internet connection");
                return;
            }


                    for (int i = 0; i < l.size(); i++) {

                        Log.e("Links to be deleted","id: "+l.get(i).id+" source   "+l.get(i).source+" destination "+l.get(i).destination);
                        WebApi api = RetrofitClient.getInstance().getMyApi();
                        api.deleteLinkById(l.get(i).id).enqueue(new Callback<SimpleResponse>() {
                            @Override
                            public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> response) {
                                if (response.isSuccessful()) {
                                    DataStorage.getInstance().getAllLinks();

                                    Log.e("Link deleted", "sucess");


                                } else {

                                    Log.e("Link not deleted", "error in deleting the link");
                                }
                            }

                            @Override
                            public void onFailure(Call<SimpleResponse> call, Throwable t) {
                                Log.e("Api failure in deleting the link", t.getMessage());
                            }
                        });


                    }
                    if(tobeDeleted.size()!=0){
                        tobeDeleted.clear();
                    }




        }catch(Exception e){
            e.printStackTrace();
        }

    }

    public static ArrayList<DetailsSpinner> findUniqueRecords(ArrayList<DetailsSpinner> detailSpinner, ArrayList<Links> links) {

        try {
            ArrayList<DetailsSpinner> uniqueRecords = new ArrayList<>(detailSpinner);
            ArrayList<DetailsSpinner> recordsToremove=new ArrayList<>();


            for (Links link : links) {
                for (DetailsSpinner detail : detailSpinner) {
                    if (link.getSource() == detail.getSource() && link.getDestination() == detail.getDestination()) {

                        String directionName = DataStorage.getInstance().getDirectionName(link.getDirectionid());
                        String updatedDirection = (String) detail.sp.getSelectedItem();



                        if ((link.getSource() == detail.getSource() && link.getDestination() == detail.getDestination()) && (updatedDirection.equalsIgnoreCase("None"))) {
                            Links lks = new Links();
                            lks.id=link.getId();
                            lks.source = link.getSource();
                            lks.destination = link.getDestination();
                            lks.directionid = 0;
                            tobeDeleted.add(lks);
                            Log.e("record to be deleted", "id"+lks.id+"Source: " + lks.source + "  Destination:  " + lks.destination + " previous Direction:  " + directionName + "  new Direction: " + updatedDirection);
                        } else if ((link.getSource() == detail.getSource() && link.getDestination() == detail.getDestination()) && (!updatedDirection.equalsIgnoreCase(directionName))) {
                            Links lks = new Links();
                            lks.id=link.getId();
                            lks.source = link.getSource();
                            lks.destination = link.getDestination();
                            lks.directionid = DataStorage.getInstance().getDirectionId(updatedDirection);
                            tobeUpdated.add(lks);
                            Log.e("record to be updated", "id"+lks.id+"Source: " + lks.source + "  Destination:  " + lks.destination + " previous Direction:  " + directionName + "  new Direction: " + updatedDirection);
                        }



                        recordsToremove.add(detail);
                        break;
                    }
                }
            }
            Log.e("records to remove list size",recordsToremove.size()+"");

            uniqueRecords.removeAll(recordsToremove);
            return uniqueRecords;
        } catch (Exception e) {
            Log.e("unique record list method exception", e.getMessage());
            return null;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            binding = ActivityMatrixBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());
            linkslist = new ArrayList<Links>();

            //allSpinners=new ArrayList<>();
            //getAllTypes();
            //getAllFloors();
            //getAllDirections();





            tableLayout = findViewById(R.id.tableLayoutformatrix);
            if (!NetworkUtils.isNetworkAvailable(getApplicationContext())) {
                showErrorUI();
            }
            else {
                ArrayList<Place> newPlacesList=DataStorage.getInstance().changeDataOfPlace(DataStorage.getInstance().getPlacesArrayList());
                DataStorage.getInstance().setPlacesArrayList(newPlacesList);
                populateArrayList();
            }

            try {
                binding.btnOnClickSaveMatrix.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        if (!NetworkUtils.isNetworkAvailable(getApplicationContext())) {
                            // Handle the case when network is not available
                            Toast.makeText(getApplicationContext(), "Network unavailable", Toast.LENGTH_SHORT).show();
                            Log.e("Network Unavailable", "Please check your internet connection");
                            return;
                        }

                        ArrayList<Details> tosend = new ArrayList<>();
                        ArrayList<DetailsSpinner> temp = new ArrayList<>();
                        temp = findUniqueRecords(allDataofMatrix, d.getLinksArrayList());
                        deletingLinks(tobeDeleted);
                        updatingLinks(tobeUpdated);
                        Log.e("all data of matrix size",allDataofMatrix.size()+"");
                        Log.e("temp size",temp.size()+"");
                        for (int i = 0; i < temp.size(); i++) {
                            String s = (String) temp.get(i).sp.getSelectedItem();
                            if (s .equalsIgnoreCase("None")) {
                                continue;
                            } else {
                                for (int h = 0; h < d.getDirectionArrayList().size(); h++) {
                                    if (s.equalsIgnoreCase(d.getDirectionArrayList().get(h).name)) {
                                        temp.get(i).directionId = d.getDirectionArrayList().get(h).id;
                                    }
                                }
                                Details dt = new Details();
                                dt.destination = temp.get(i).destination;
                                dt.source = temp.get(i).source;
                                dt.directionId = temp.get(i).directionId;
                                tosend.add(dt);
                            }
                        }



                        WebApi api = RetrofitClient.getInstance().getMyApi();
                        Toast.makeText(MatrixActivity.this, "to send list size" + tosend.size() + "", Toast.LENGTH_SHORT).show();
                        api.insertData(tosend).enqueue(new Callback<SimpleResponse>() {
                            @Override
                            public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> response) {
                                if (response.isSuccessful()) {

                                    DataStorage.getInstance().getAllLinks();

                                    //Toast.makeText(MatrixActivity.this, response.body()+"", Toast.LENGTH_SHORT).show();
                                } else {
                                    Log.e("Exception Sending data", response.body() + call.toString());
                                    Toast.makeText(MatrixActivity.this, response.body() + "", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<SimpleResponse> call, Throwable t) {
                                //Toast.makeText(MatrixActivity.this, "Api failure", Toast.LENGTH_SHORT).show();
                                Log.e("Exception for saving Api", t.getMessage() + call.toString());
                            }
                        });

                        tosend.clear();
                    }


                });
            } catch (Exception e) {
                e.printStackTrace();
            }

            binding.btnOnClickCancelMatrix.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }




    private void populateArrayList() {


        places = DataStorage.getInstance().getPlacesArrayList();
        linkslist = DataStorage.getInstance().getLinksArrayList();
        directions = DataStorage.getInstance().getDirectionArrayList();
        //ArrayList<Links> temp=new ArrayList<>();

        //temp=getUniquePlaces(linkslist);
        //places=getUniquePlaces(places);
        int size = places.size();
        //linkslist=temp;
        //Toast.makeText(this, size+"", Toast.LENGTH_SHORT).show();
        size = size + 1;
        createTable(size, size);
    }

    private void createTable(int rows, int columns) {
        for (int i = 0; i < rows; i++) {
            ijvalue = false;
            TableRow tableRow = new TableRow(this);
            tableRow.setLayoutParams(new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.WRAP_CONTENT,
                    TableLayout.LayoutParams.WRAP_CONTENT));


            for (int j = 0; j < columns; j++) {
                if (i == 0 && j == 0) {
                    TableRow.LayoutParams params = new TableRow.LayoutParams(
                            TableLayout.LayoutParams.WRAP_CONTENT,
                            TableLayout.LayoutParams.WRAP_CONTENT);

                    params.setMargins(8, -1, 8, 8);
                    params.width = 300;
                    params.height = 280;
                    TextView tv = new TextView(this);
                    tv.setText("Destination\nSource");
                    float textSizeInSp = 12; // Change this to your desired text size
                    tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, textSizeInSp);
                    tv.setTextColor(getColor(R.color.white));
                    tv.setGravity(Gravity.CENTER);
                    tv.setBackground(getDrawable(R.drawable.cell_backgroud_colored));

                    tableRow.addView(tv, params);


                } else if (j == 0 && i > 0) {

                    //problem is here
                    TableRow.LayoutParams params = new TableRow.LayoutParams(
                            TableLayout.LayoutParams.WRAP_CONTENT,
                            TableLayout.LayoutParams.WRAP_CONTENT);
                    params.width = 300;
                    params.height = 280;


                    params.setMargins(8, 8, 8, 8);
                    TextView tv = new TextView(this);
                    tv.setGravity(Gravity.CENTER);

                    tv.setText(places.get(i - 1).name + "_" + places.get(i-1).typename);


                    //tv.setText(places.get(i-1).name+"_");
                    tv.setBackground(getDrawable(R.drawable.cell_backgroud_colored));
                    //tv.setTextSize(2.0f);
                    float textSizeInSp = 13; // Change this to your desired text size
                    tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSizeInSp);
                    tv.setTextColor(getColor(R.color.white));
                    tableRow.addView(tv, params);
                    //ijvalue=true;
                } else if ((i == 0 && j > 0)) {
                    TableRow.LayoutParams params = new TableRow.LayoutParams(
                            TableLayout.LayoutParams.WRAP_CONTENT,
                            TableLayout.LayoutParams.WRAP_CONTENT);
                    params.setMargins(8, 8, 8, 8);
                    params.width = 300;
                    params.height = 280;
                    //params.setMargins(0, 0, 0, 0);

                    TextView tv = new TextView(this);

                    tv.setText(places.get(j - 1).name + "_" + places.get(j-1).typename);



                    float textSizeInSp = 13; // Change this to your desired text size
                    tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSizeInSp);
                    tv.setGravity(Gravity.CENTER);
                    tv.setBackground(getDrawable(R.drawable.cell_backgroud_colored));
                    tv.setTextColor(getColor(R.color.white));

                    tableRow.addView(tv, params);

                } else if (i == j && i != 0 && j != 0) {

                    Spinner spinner = new Spinner(this);
                    ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
                            android.R.layout.simple_spinner_item,
                            new String[]{"None"});
                    spinner.setAdapter(spinnerAdapter);

                    TableRow.LayoutParams params = new TableRow.LayoutParams(
                            TableLayout.LayoutParams.WRAP_CONTENT,
                            TableLayout.LayoutParams.WRAP_CONTENT);
                    //allSpinners.add(null);
                    params.setMargins(8, 8, 8, 8);
                    params.width = 300;
                    params.height = 280;
                    //params.setMargins(0, 0, 0, 0);  // Add some margin between cells

                    // Set the custom cell background
                    spinner.setBackgroundResource(R.drawable.cell_background);

                    tableRow.addView(spinner, params);

                } else {
                    String s = " ";
                    try {

                        //Log.e(" places of i ",places.get(i-1).id+" places of j "+places.get(j-1).id);

                        for (int y = 0; y < linkslist.size(); y++) {
                            //Log.e("y value source",linkslist.get(y).source+" y value destination"+linkslist.get(y).destination);
                            //Log.d(" Links List of Source plus i ",linkslist.get(y).source+""+places.get(i-1).id+" destination plus j"+linkslist.get(y).destination+""+places.get(j-1).id);
                            if (places.get(i - 1).id == linkslist.get(y).source && places.get(j - 1).id == linkslist.get(y).destination) {

                                for (int q = 0; q < directions.size(); q++) {
                                    Log.e("link list direction id", linkslist.get(y).directionid + " and Directions direction id " + directions.get(q).getId());
                                    if (linkslist.get(y).directionid == directions.get(q).id) {
                                        s = directions.get(q).name;
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        //Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("Exception of for", e.getMessage());
                    }


                    DetailsSpinner detailsSpinner = new DetailsSpinner();
                    Spinner spinner = new Spinner(this);
                    detailsSpinner.source = places.get(i - 1).getId();
                    detailsSpinner.destination = places.get(j - 1).getId();
                    detailsSpinner.sp = spinner;
                    allDataofMatrix.add(detailsSpinner);

                    if (s.equalsIgnoreCase("east")) {
                        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
                                android.R.layout.simple_spinner_item,
                                new String[]{s, "west", "north", "south", "None"});
                        spinner.setAdapter(spinnerAdapter);

                    } else if (s.equalsIgnoreCase("west")) {
                        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
                                android.R.layout.simple_spinner_item,
                                new String[]{s, "east", "north", "south", "None"});
                        spinner.setAdapter(spinnerAdapter);

                    } else if (s.equalsIgnoreCase("north")) {
                        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
                                android.R.layout.simple_spinner_item,
                                new String[]{s, "west", "east", "south", "None"});
                        spinner.setAdapter(spinnerAdapter);

                    } else if (s.equalsIgnoreCase("south")) {
                        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
                                android.R.layout.simple_spinner_item,
                                new String[]{s, "west", "north", "east", "None"});
                        spinner.setAdapter(spinnerAdapter);

                    } else {
                        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
                                android.R.layout.simple_spinner_item,
                                new String[]{"None", "east", "west", "north", "south"});
                        spinner.setAdapter(spinnerAdapter);
                    }


                    allId.add(id);


                    TableRow.LayoutParams params = new TableRow.LayoutParams(
                            TableLayout.LayoutParams.WRAP_CONTENT,
                            TableLayout.LayoutParams.WRAP_CONTENT);
                    params.setMargins(8, 8, 8, 8);
                    params.width = 300;
                    params.height = 280;
                    //params.setMargins(0, 0, 0, 0);  // Add some margin between cells

                    // Set the custom cell background
                    spinner.setBackgroundResource(R.drawable.cell_background);

                    tableRow.addView(spinner, params);


                }
            }

            // Add the TableRow to the TableLayout
            tableLayout.addView(tableRow);



        }
    }

    private void showErrorUI() {
        binding.layoutOfSaveAndCancelButtonMatrix.setVisibility(View.GONE);
        // Hide the RecyclerView
        binding.horizontalScrollViewMatrix.setVisibility(View.GONE);

        binding.errorLayoutMatrix.setVisibility(View.VISIBLE);

        // Show an error message
        binding.errorTextViewMatrix.setVisibility(View.VISIBLE);
        //binding.errorTextViewPlace.setText("Error loading places. Please check your connection.");

        // Show a retry button
        binding.reloadButtonMatrix.setVisibility(View.VISIBLE);
        binding.reloadButtonMatrix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Retry loading places
                loadingLinksAgain();

                // Hide error UI elements on retry
                binding.reloadButtonMatrix.setVisibility(View.GONE);
                //binding.errorTextViewPlace.setText("Getting Data...Please wait for 1 minute");

            }
        });
    }

    public void loadingLinksAgain() {
        final long startTime = System.currentTimeMillis(); // Get the start time before fetching data

        // Fetch all necessary data and populate arrays


        Runnable checkDataRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    long elapsedTime = System.currentTimeMillis() - startTime;
                    long remainingTime = MAX_WAIT_TIME - elapsedTime;

                    if (remainingTime <= 0) {
                        showErrorUI(); // If exceeded maximum wait time, show error UI
                    } else {
                        boolean val=fetchAllDataAndPopulate();
                        int secondsRemaining = (int) (remainingTime / 1000);
                        binding.errorTextViewMatrix.setText("Remaining: " + secondsRemaining + " seconds");
                        if (val) {
                            // If data is available, proceed with updating UI



                            binding.errorTextViewMatrix.setVisibility(View.GONE);
                            binding.errorLayoutMatrix.setVisibility(View.GONE);
                            binding.layoutOfSaveAndCancelButtonMatrix.setVisibility(View.VISIBLE);
                            binding.horizontalScrollViewMatrix.setVisibility(View.VISIBLE);

                        }else{
                            new Handler().postDelayed(this, DELAY_CHECK_INTERVAL);
                        }
                    }
                } catch (Exception e) {
                    showErrorUI();
                    // Log error or show specific error message for debugging
                    e.printStackTrace();
                }
            }
        };

        // Start checking for data availability
        new Handler().post(checkDataRunnable);
    }


    public boolean fetchAllDataAndPopulate(){

        if(DataStorage.getInstance().getLinksArrayList()==null || DataStorage.getInstance().getPlacesArrayList()==null
        || DataStorage.getInstance().getTypesArrayList()==null || DataStorage.getInstance().placesArrayList.isEmpty() ||
                DataStorage.getInstance().linksArrayList.isEmpty() || DataStorage.getInstance().typesArrayList.isEmpty()
        ){
            return false;
        }
        ArrayList<Place> newPlacesList = DataStorage.getInstance().changeDataOfPlace(DataStorage.getInstance().getPlacesArrayList());
        DataStorage.getInstance().setPlacesArrayList(newPlacesList);
        populateArrayList();
        return true;




    }


    @Override
    protected void onDestroy() {
        try {
            DataStorage.getInstance().getAllLinks();
            super.onDestroy();
        }catch (Exception e){
            e.printStackTrace();
        }

    }



}