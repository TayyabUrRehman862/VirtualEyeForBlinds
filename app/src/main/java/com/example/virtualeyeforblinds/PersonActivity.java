package com.example.virtualeyeforblinds;

import static kotlinx.coroutines.DelayKt.delay;

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
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.virtualeyeforblinds.api.DataListener;
import com.example.virtualeyeforblinds.api.RetrofitClient;
import com.example.virtualeyeforblinds.api.WebApi;
import com.example.virtualeyeforblinds.databinding.ActivityPersonBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PersonActivity extends AppCompatActivity {

    DataListener listener;
    ArrayList<Person> arr=new ArrayList<>();
    final int PERMISSION_REQUEST_CODE=1001;
    final int PERMISSION_RESULT_CODE_GALLERY=1003;
    private static final int SELECT_PICTURE=0;
    MyPersonAdapter adapter;
    public ArrayList<Uri> imageUris=new ArrayList<>();
    BottomSheetDialog bottomSheetDialog;

    ActivityPersonBinding binding;
    public void on_click_Save(View view){
        try {



            EditText etname = bottomSheetDialog.findViewById(R.id.et_add_person_name_dialog);
            String name = etname.getText().toString();

            Person p = new Person();
            p.pname=name;

            p.img=imageUris.get(0);
            adapter.addPerson(p);
            addTheDataInTheAdapterForPersons();
            //     addPlaceToRecyclerView(p);

            Toast.makeText(this, "Data Saved", Toast.LENGTH_SHORT).show();

            bottomSheetDialog.dismiss();
            imageUris.clear();
        }catch (Exception e){
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG);
        }
    }
    public void on_click_go_back(View v){
        try {
            bottomSheetDialog.dismiss();
        }catch (Exception e){
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG);
        }
    }
    public void addTheDataInTheAdapterForPersons(){
        adapter.notifyDataSetChanged();
    }
    public ArrayList<Person> getDataForTheAdapter(){


//        WebApi api= RetrofitClient.getInstance().getMyApi();
//        api.getAllPersons().enqueue(new Callback<ArrayList<Person>>() {
//            @Override
//            public void onResponse(Call<ArrayList<Person>> call, Response<ArrayList<Person>> response) {
//                if(response.isSuccessful()){
//                     ArrayList<Person> arr1=response.body();
//
//                    arr=arr1;
//                    listener.onDataReceived(arr);
//                    Toast.makeText(PersonActivity.this, "List waS empty", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ArrayList<Person>> call, Throwable t) {
//                listener.onFailure();
//                Toast.makeText(PersonActivity.this, "Failure", Toast.LENGTH_SHORT).show();
//            }
//
//        });



        Person p = new Person();
        p.pname="Shafeeq";

        Person p1 = new Person();
        p1.pname="Imran Khan";


        p1.img= Uri.parse("android.resource://com.example.virtualeyeforblinds/mipmap/place");
        p.img=Uri.parse("android.resource://com.example.virtualeyeforblinds/mipmap/place");

        arr.add(p);
        arr.add(p1);
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
    }

    public boolean checkPermissions(){

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED) {
            //if permission  granted
            return true;
        }
        else {

            //if permission not granted
            return false;
        }
    }
    public void browse_images_person(){
        try {

            Intent i = new Intent();
            i.setType("image/*");
            i.putExtra(i.EXTRA_ALLOW_MULTIPLE, true);
            i.setAction(Intent.ACTION_GET_CONTENT);

            // pass the constant to compare it
            // with the returned requestCode
            startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE);

        }catch(Exception e){
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG);
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
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPersonBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.itemsListRcvPerson.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyPersonAdapter(getDataForTheAdapter()); // Initialize adapter with an empty list
        binding.itemsListRcvPerson.setAdapter(adapter);



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

}