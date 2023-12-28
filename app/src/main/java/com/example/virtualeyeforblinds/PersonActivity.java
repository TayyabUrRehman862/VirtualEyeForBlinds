package com.example.virtualeyeforblinds;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.virtualeyeforblinds.adapters.MyAdapter;
import com.example.virtualeyeforblinds.adapters.MyPersonAdapter;
import com.example.virtualeyeforblinds.api.DataListener;
import com.example.virtualeyeforblinds.api.RetrofitClient;
import com.example.virtualeyeforblinds.api.WebApi;
import com.example.virtualeyeforblinds.databinding.ActivityPersonBinding;
import com.example.virtualeyeforblinds.extraClasses.NetworkUtils;
import com.example.virtualeyeforblinds.globalClass.DataStorage;
import com.example.virtualeyeforblinds.models.SimpleResponse;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PersonActivity extends AppCompatActivity {

    View viewForSnackBar;

    private static final long DELAY_CHECK_INTERVAL = 1000; // Interval to check for data availability (in milliseconds)
    private static final long MAX_WAIT_TIME = 10000; // Maximum wait time for data (in milliseconds)
    ArrayList<Person> personArrayList=new ArrayList<>();

    DataListener listener;
    ArrayList<Person> arr=new ArrayList<>();
    final int PERMISSION_REQUEST_CODE=1001;
    final int PERMISSION_RESULT_CODE_GALLERY=1003;
    private static final int SELECT_PICTURE=0;
    MyPersonAdapter adapter;
    public ArrayList<Uri> imageUris=new ArrayList<>();
    BottomSheetDialog bottomSheetDialog;

    ActivityPersonBinding binding;

    public void uploadPerson(String name, ArrayList<Uri> Images)  {
        try {

            WebApi api = RetrofitClient.getInstance().getMyApi();
            RequestBody pname = api.createPartFromString(name);
            ArrayList<MultipartBody.Part> listOfPersonImages=api.prepareFileParts("image",Images,this);




            api.uploadPerson(pname, listOfPersonImages).enqueue(new Callback<SimpleResponse>() {
                @Override
                public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> response) {
                    if (response.isSuccessful()) {
                        try {
                            SimpleResponse s = response.body();
                            DataStorage.getInstance().getPersonsList();

                            Person p = new Person();
                            p.Name = name;


                            adapter.addPerson(p);


                            //addTheDataInTheAdapterForPersons();
                            Snackbar snackbar=Snackbar.make(viewForSnackBar,"Person Added.Please try to recognize person after 5 minutes",Snackbar.LENGTH_LONG);
                            snackbar.show();

                        }catch (Exception e){
                            Log.e("response of person exception",e.getMessage());
                        }
                    } else {
                        Toast.makeText(PersonActivity.this, "Error in uploading a Person", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<SimpleResponse> call, Throwable t) {

                    Toast.makeText(PersonActivity.this, "Api Failure in saving A person", Toast.LENGTH_SHORT).show();
                    Log.e("Api failure in saving a person", t.getMessage());

                }
            });

        }catch(Exception e){
            Log.e("uploadPerson Method Exception",e.getMessage());
        }
    }
    public boolean checkPersonExists(String name){
        try {
            for (int i = 0; i < DataStorage.getInstance().getPersonArrayList().size(); i++) {
                if (name.equalsIgnoreCase(DataStorage.getInstance().getPersonArrayList().get(i).Name)) {
                    return true;
                } else {
                    continue;
                }
            }
            return false;
        }catch(Exception e){
            e.printStackTrace();
            Log.e("check person exists method exeception",e.getMessage());
            return false;

        }
    }
    public void on_click_SavePerson(View view){
        try {



            EditText etname = bottomSheetDialog.findViewById(R.id.et_add_person_name_dialog);
            String name = etname.getText().toString();

            boolean v=checkPersonExists(name);
            if(v){
                Toast.makeText(this, "Person Already Exists", Toast.LENGTH_SHORT).show();
                return;
            }


            if(imageUris.size()!=0){
                Toast.makeText(this, "Please Make sure person Images are equal to or greater than 0", Toast.LENGTH_SHORT).show();
                imageUris.clear();
                return;
            }

            uploadPerson(name,imageUris);



            //     addPlaceToRecyclerView(p);



            bottomSheetDialog.dismiss();
            imageUris.clear();
        }catch (Exception e){
            Log.e("on click Save Method of Person Exception",e.getMessage());
        }
    }
    public void on_click_go_back(View v){
        try {
            bottomSheetDialog.dismiss();
        }catch (Exception e){
            Snackbar snackbar=Snackbar.make(viewForSnackBar,"on click go back method exception",Snackbar.LENGTH_LONG);
        }
    }
    public void addTheDataInTheAdapterForPersons(){
        adapter.notifyDataSetChanged();
    }
    public ArrayList<Person> getDataForTheAdapter(){

        try {



            WebApi api = RetrofitClient.getInstance().getMyApi();
            api.getAllPersons().enqueue(new Callback<ArrayList<Person>>() {
                @Override
                public void onResponse(Call<ArrayList<Person>> call, Response<ArrayList<Person>> response) {
                    if (response.isSuccessful()) {

                        ArrayList<Person> arr1 = response.body();

                        arr = (ArrayList<Person>) arr1.clone();


                        Toast.makeText(PersonActivity.this, arr.size()+"", Toast.LENGTH_SHORT).show();

                        //listener.onDataReceived(arr);
                        //Toast.makeText(PersonActivity.this, "List waS empty", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ArrayList<Person>> call, Throwable t) {
                    //listener.onFailure();
                    Toast.makeText(PersonActivity.this, "Failure", Toast.LENGTH_SHORT).show();
                }

            });

        }catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

//        Person p = new Person();
//        p.pname="Shafeeq";
//
//        Person p1 = new Person();
//        p1.pname="Imran Khan";
//
//
//        p1.img= Uri.parse("android.resource://com.example.virtualeyeforblinds/mipmap/place");
//        p.img=Uri.parse("android.resource://com.example.virtualeyeforblinds/mipmap/place");

        //arr.add(p);
        //arr.add(p1);
        //Toast.makeText(this, arr.size()+"", Toast.LENGTH_SHORT).show();
        return arr;
        //return arr1;
    }
    public void requestPermission(View v){
        if(checkPermissions()==false){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},PERMISSION_REQUEST_CODE);
        }
        else{
            browse_images_person();
            //permission already granted
        }
    }
//    public void openGallery(){
//        Intent i = new Intent(MediaStore.ACTION_PICK_IMAGES, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        startActivityForResult(i,PERMISSION_RESULT_CODE_GALLERY);
//    }
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        try {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            if(requestCode == PERMISSION_REQUEST_CODE){
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "GRANTED", Toast.LENGTH_SHORT).show();
                    browse_images_person();
                }
                else{
                    Toast.makeText(this, "DENIED", Toast.LENGTH_SHORT).show();
                }
                //this method is called when we ask the user for a permission if he allows us or not depending on its choice.
                //basically a result containing method
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("permissions exception in Person onRequestPermission",e.getMessage());
        }
    }

    public boolean checkPermissions(){

        try {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED) {
                //if permission  granted
                return true;
            }
            else {

                //if permission not granted
                return false;
            }
        } catch (Exception e) {

            e.printStackTrace();
            Log.e("check permissions person exception",e.getMessage());
            return false;
        }
    }
    public void browse_images_person(){
        try {

            if(!imageUris.isEmpty()){
                imageUris.clear();
            }
            Intent i = new Intent();
            i.setType("image/*");
            i.putExtra(i.EXTRA_ALLOW_MULTIPLE, true);
            i.setAction(Intent.ACTION_GET_CONTENT);

            // pass the constant to compare it
            // with the returned requestCode
            startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE);

        }catch(Exception e){
            e.printStackTrace();
            Log.e("browse images person method exception",e.getMessage());
        }



    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {

            //bitmaps.clear();
            super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == SELECT_PICTURE) {

                // compare the resultCode with the
                // SELECT_PICTURE constant
                if (resultCode == Activity.RESULT_OK) {
                    if (data.getClipData() != null) {
                        int count = data.getClipData().getItemCount();
                        for (int i = 0; i < count; i++) {
                            //getting uri at specific position
                            Uri imageUri = data.getClipData().getItemAt(i).getUri();
                            imageUris.add(imageUri);
                        }
                        //picked multiple images
                    } else {
                        Uri selectedImageUri = data.getData();
                        imageUris.add(selectedImageUri);
                        //picked single image
                    }

//                // Get the url of the image from data
//                Uri selectedImageUri = data.getData();
//                if (null != selectedImageUri) {
//                    // update the preview image in the layout
//                    //IVPreviewImage.setImageURI(selectedImageUri);
//                    TextView v=bottomSheetDialog.findViewById(R.id.tv_selected_images);
//                    v.setText("Selected Images: 1");
//                }
                }
            }
            TextView v = bottomSheetDialog.findViewById(R.id.tv_selected_images_person);
            v.setText("Selected Images: " + imageUris.size());

        }catch(Exception e){
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG);
        }
    }
    public void onClickGoBackFromPerson(View v){
        try {
            DataStorage.getInstance().getPersonsList();

            finish();
        }catch (Exception e){
            e.printStackTrace();
            Log.e("on click go back from person method exception",e.getMessage());
        }
    }

    private void setUpRecyclerView(ArrayList<Person> personsArrayList) {
        try {
            if (personsArrayList != null && !personsArrayList.isEmpty()) {


                binding.itemsListRcvPerson.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                adapter = new MyPersonAdapter(getApplicationContext(),personsArrayList);
                // Initialize adapter with an empty list
                binding.itemsListRcvPerson.setAdapter(adapter);
                adapter.notifyDataSetChanged();

                // Ensure changeData method is handled safely





            } else {
                // Handle case where placesArrayList is null or empty
                showErrorUI();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("setUp Recycler view method exception",e.getMessage());
            showErrorUI();
        }
    }
    public void loadPersons(){
        try {
            Thread t=new Thread(new Runnable() {
                @Override
                public void run() {

                    Handler handler=new Handler(getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            DataStorage dataStorage = DataStorage.getInstance();
                            personArrayList = dataStorage.getPersonArrayList();

                            if (personArrayList != null && !personArrayList.isEmpty()) {
                                // If places are available, set up the RecyclerView
                                setUpRecyclerView(personArrayList);
                            } else {
                                // If places are null or empty, show an error message
                                showErrorUI();
                            }



                        }
                    });
                }
            });
            t.start();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("load person method exception",e.getMessage());
        }
    }

    public void loadingPersonsAgain(){
        final long startTime = System.currentTimeMillis(); // Get the start time before fetching data

        Runnable checkDataRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    long elapsedTime = System.currentTimeMillis() - startTime;
                    long remainingTime = MAX_WAIT_TIME - elapsedTime;

                    if (remainingTime <= 0) {
                        // If exceeded maximum wait time, show error UI
                        showErrorUI();

                    } else {
                        DataStorage.getInstance().getPersonsList();
                        // Update countdown TextView with remaining seconds
                        int secondsRemaining = (int) (remainingTime / 1000);
                        binding.errorTextViewPersons.setText("Remaining: " + secondsRemaining + " seconds");

                        DataStorage dataStorage = DataStorage.getInstance();
                        ArrayList<Person> personArrayList = dataStorage.getPersonArrayList();

                        if (personArrayList != null && !personArrayList.isEmpty()) {
                            Handler handler=new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    setUpRecyclerView(personArrayList);
                                    binding.errorTextViewPersons.setVisibility(View.GONE);
                                    binding.errorLayoutPersons.setVisibility(View.GONE);
                                    binding.itemsListRcvPerson.setVisibility(View.VISIBLE);
                                }
                            },1500);


                        } else {
                            // If within the maximum wait time, schedule the next check
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPersonBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        viewForSnackBar = findViewById(android.R.id.content);

        //ArrayList<String> b=a.getStringArrayList("persondata");

        if (!NetworkUtils.isNetworkAvailable(getApplicationContext())) {
            // Handle the case when network is not available

            showErrorUI();


        }
        else {
            loadPersons();
        }


        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                ArrayList<Person> filteredList = filterPersons(personArrayList, s);


                adapter.updateList(filteredList);
                return true;
            }
        });


        binding.btnAddPerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog = new BottomSheetDialog(PersonActivity.this);
                bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog_person);
                View container = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
                container.setBackground(ContextCompat.getDrawable(PersonActivity.this, R.drawable.design_for_bottom_sheet));
                bottomSheetDialog.show();
            }
        });
    }
    private ArrayList<Person> filterPersons(ArrayList<Person> persons, String query) {
        query = query.toLowerCase();
        ArrayList<Person> filteredList = new ArrayList<>();
        for (Person person : persons) {
            // Implement your filtering logic here, for example, filtering by person name
            if (person.getName().toLowerCase().contains(query)) {
                filteredList.add(person);
            }
        }
        return filteredList;
    }


    private void showErrorUI() {
        // Hide the RecyclerView
        binding.itemsListRcvPerson.setVisibility(View.GONE);

        binding.errorLayoutPersons.setVisibility(View.VISIBLE);

        // Show an error message
        binding.errorTextViewPersons.setVisibility(View.VISIBLE);
        //binding.errorTextViewPersons.setText("Error loading places. Please check your connection.");

        // Show a retry button
        binding.reloadButtonPersons.setVisibility(View.VISIBLE);
        binding.reloadButtonPersons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Retry loading places
                loadingPersonsAgain();

                // Hide error UI elements on retry
                binding.reloadButtonPersons.setVisibility(View.GONE);
                //binding.errorTextViewPlace.setText("Getting Data...Please wait for 1 minute");

            }
        });
    }

    @Override
    protected void onDestroy() {
        DataStorage.getInstance().getPersonsList();
        super.onDestroy();

    }

}