package com.example.virtualeyeforblinds;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.virtualeyeforblinds.api.RetrofitClient;
import com.example.virtualeyeforblinds.api.WebApi;
import com.example.virtualeyeforblinds.databinding.ActivityPlacesBinding;
import com.example.virtualeyeforblinds.extraClasses.DataStorage;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlacesActivity extends AppCompatActivity {

    int i=8;
    MyAdapter adapter;

    ArrayList<String> LinkFloors = new ArrayList<>();

    final int PERMISSION_REQUEST_CODE=1001;


    ArrayList<Bitmap> bitmaps = new ArrayList<>();
    public ArrayList<Uri> imageUris=new ArrayList<>();
    BottomSheetDialog bottomSheetDialog;

    ActivityPlacesBinding binding;
    private static final int SELECT_PICTURE=0;
    public void on_click_go_back(View v){
        try {
            bottomSheetDialog.dismiss();
        }catch (Exception e){
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG);
        }
    }
    public void AddFloor(){
        try {


            LinkFloors.add("Floor1");
            LinkFloors.add("First Floor");
            LinkFloors.add("Ground Floor");
            LinkFloors.add("Basement");
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, LinkFloors);
            View v = bottomSheetDialog.findViewById(R.id.spinner_floor);
            Spinner v1 = (Spinner) v;
            v1.setAdapter(adapter);
        }catch(Exception e){
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG);
        }


    }
    public void AddType(){
        try {
            ArrayList<String> temp = new ArrayList<>();
            temp.add("Lab");
            temp.add("Faculty");
            temp.add("Washroom");
            temp.add("LT1");
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, temp);
            View v = bottomSheetDialog.findViewById(R.id.spinner_type);
            Spinner v1 = (Spinner) v;
            v1.setAdapter(adapter);
        }catch (Exception e){
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG);
        }


    }
//    public void Uri_to_image(ArrayList<Uri> image){
//        try {
//
//            ContentResolver contentResolver = getContentResolver();
//
//            for (Uri uri : image) {
//                try {
//                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri);
//                    bitmaps.add(bitmap);
//                } catch (IOException e) {
//                    Toast.makeText(this, e.toString(), Toast.LENGTH_LONG);
//                }
//            }
//        }catch (Exception e){
//            Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG);
//        }
//    }
    public boolean checkPermissions(){

    if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED) {
        //if permission  granted
        return true;
    }
    else {

        //if permission not granted
        return false;
    }
}
    public void requestPermissionPlace(View v){
        if(checkPermissions()==false){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},PERMISSION_REQUEST_CODE);
        }
        else{
            browse_images();
            //permission already granted
        }
    }
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == PERMISSION_REQUEST_CODE){
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "GRANTED", Toast.LENGTH_SHORT).show();
                browse_images();
            }
            else{
                Toast.makeText(this, "DENIED", Toast.LENGTH_SHORT).show();
            }
            //this method is called when we ask the user for a permission if he allows us or not depending on its choice.
            //basically a result containing method
        }
    }
    public void browse_images(){
        try {

            Intent i = new Intent();
            i.setType("image/*");
            i.putExtra(i.EXTRA_ALLOW_MULTIPLE, true);
            i.setAction(Intent.ACTION_GET_CONTENT);

            // pass the constant to compare it
            // with the returned requestCode
            startActivityForResult(Intent.createChooser(i, "Select Picture"), PERMISSION_REQUEST_CODE);
        }catch(Exception e){
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG);
        }



    }

    public void onClickGoBackFromPlace(View v){
        finish();
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            imageUris.clear();
            //bitmaps.clear();
            super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == PERMISSION_REQUEST_CODE) {

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
//            TextView v = bottomSheetDialog.findViewById(R.id.tv_selected_images);
//            v.setText("Selected Images: " + imageUris.size());
        }catch(Exception e){
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG);
        }
    }

    public void on_click_Save(View view){
        try {


            Spinner v = bottomSheetDialog.findViewById(R.id.spinner_floor);
            Spinner v2 = bottomSheetDialog.findViewById(R.id.spinner_type);
            Object a = v.getSelectedItem();
            Object b = v2.getSelectedItem();
            String floor = a.toString();
            String type = b.toString();
            EditText etname = bottomSheetDialog.findViewById(R.id.et_add_location_name_dialog);
            String name = etname.getText().toString();

            Place p = new Place();
            p.name=name;
            p.floor=floor;
            p.type=type;
            p.img=imageUris.get(0);
            adapter.addPlace(p);
            addTheDataInTheAdapter();
       //     addPlaceToRecyclerView(p);


            uploadPlaces(imageUris.get(0),name,floor,type);

            //Toast.makeText(this, "Data Saved", Toast.LENGTH_SHORT).show();

            bottomSheetDialog.dismiss();
            imageUris.clear();
        }catch (Exception e){
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG);
        }
    }

    public void addTheDataInTheAdapter(){
        adapter.notifyDataSetChanged();
    }
    public ArrayList<Place> getDataForTheAdapter(){



        Place p = new Place();
        p.name="LAb";
        p.type="LAB";
        p.floor="1st";




        Place p1 = new Place();
        p1.name="LAb1";
        p1.type="LAB1";
        p1.floor="2nd";






        p1.img=Uri.parse("android.resource://com.example.virtualeyeforblinds/mipmap/place");
        p.img=Uri.parse("android.resource://com.example.virtualeyeforblinds/mipmap/place");

        ArrayList<Place> arr1=new ArrayList<>();
        arr1.add(p);
        arr1.add(p1);
        return arr1;
    }

    private void uploadPlaces(Uri imageUri,String name,String floor,String type)  {
        try {

            WebApi api = RetrofitClient.getInstance().getMyApi();
            ArrayList<MultipartBody.Part> p = new ArrayList<>();

                p.add(api.prepareFilePart("image", imageUri, this));


            RequestBody pname=api.createPartFromString(name);
            RequestBody fname=api.createPartFromString(floor);
            RequestBody ptype=api.createPartFromString(type);
            RequestBody pid=api.createPartFromString(20+"");





            api.uploadPlaces(p,pid,pname,fname,ptype).enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if(response.isSuccessful()){
                        Toast.makeText(getApplicationContext(), "File Uploaded", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), "File not uploaded", Toast.LENGTH_SHORT).show();
                    Log.d("exception",t.getMessage());
                }
            });
        }catch (Exception e){
            Toast.makeText(getApplicationContext(), "Exception"+e.getMessage().toString(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {


            super.onCreate(savedInstanceState);
            binding = ActivityPlacesBinding.inflate(getLayoutInflater());

            setContentView(binding.getRoot());
            DataStorage dataStorage = DataStorage.getInstance();
            ArrayList<Place> placesArrayList = dataStorage.getPlacesArrayList();

            binding.itemsListRcv.setLayoutManager(new LinearLayoutManager(this));




             adapter = new MyAdapter(placesArrayList);
//
            binding.itemsListRcv.setAdapter(adapter);


            binding.btnAddPlaces.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    bottomSheetDialog = new BottomSheetDialog(PlacesActivity.this);
                    bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog_places);
                    View container = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
                    container.setBackground(ContextCompat.getDrawable(PlacesActivity.this, R.drawable.design_for_bottom_sheet));
                    bottomSheetDialog.show();
                    AddFloor();
                    AddType();
                }
            });

        }catch (Exception e){
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG);
        }
    }
}