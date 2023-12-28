package com.example.virtualeyeforblinds;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.opengl.Visibility;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.ContactsContract;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.virtualeyeforblinds.adapters.FloorAdapter;
import com.example.virtualeyeforblinds.adapters.MyAdapter;
import com.example.virtualeyeforblinds.adapters.TypeAdapter;
import com.example.virtualeyeforblinds.api.RetrofitClient;
import com.example.virtualeyeforblinds.api.WebApi;
import com.example.virtualeyeforblinds.databinding.ActivityPlacesBinding;
import com.example.virtualeyeforblinds.extraClasses.NetworkUtils;
import com.example.virtualeyeforblinds.globalClass.DataStorage;
import com.example.virtualeyeforblinds.models.SimpleResponse;
import com.example.virtualeyeforblinds.models.Types;
import com.example.virtualeyeforblinds.models.floors;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlacesActivity extends AppCompatActivity {
    Integer doorDirectionId = 0;

    public ArrayList<Place> placesArrayList=new ArrayList<>();

    private static final long DELAY_CHECK_INTERVAL = 1000; // Interval to check for data availability (in milliseconds)
    private static final long MAX_WAIT_TIME = 10000; // Maximum wait time for data (in milliseconds)


    View view;

    ArrayList<Place> temp = new ArrayList<>();

    ArrayList<Uri> southImages;

    ArrayList<Uri> northImages;

    ArrayList<Uri> eastImages;

    ArrayList<Uri> westImages;

    boolean southImgflag, eastImgflag, northImgflag, westImgflag = false;

    ArrayList<String> floor;

    ArrayList<String> typeList;
    RecyclerView floorRecyclerView;

    RecyclerView typeRecyclerView;

    FloorAdapter floorAdapter;

    TypeAdapter typeAdapter;

    SearchView searchViewFloor;

    SearchView searchViewType;
    Button AddFloorButton;

    View popupView;
    PopupWindow floorPopup;

    PopupWindow typePopup;

    PopupWindow doorDirectionPopup;

    int i = 8;
    MyAdapter adapter;

    ArrayList<String> LinkFloors = new ArrayList<>();

    final int PERMISSION_REQUEST_CODE = 1001;


    ArrayList<Bitmap> bitmaps = new ArrayList<>();
    public ArrayList<Uri> imageUris = new ArrayList<>();
    public BottomSheetDialog bottomSheetDialog;

    ActivityPlacesBinding binding;
    private static final int SELECT_PICTURE = 0;

    public void on_click_go_back(View v) {
        try {
            bottomSheetDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("onClick goBack Method Exception", e.getMessage());
        }
    }

    public void btnSouthImages(View v) {
        if(southImages.size()!=0){
            southImages.clear();
        }
        eastImgflag = false;
        westImgflag = false;
        northImgflag = false;
        southImgflag = true;
        requestPermissionPlace();
    }

    public void btnNorthImages(View v) {
        if(northImages.size()!=0){
            northImages.clear();
        }
        eastImgflag = false;
        westImgflag = false;
        northImgflag = true;
        southImgflag = false;
        requestPermissionPlace();
    }

    public void btnEastImages(View v) {
        if(eastImages.size()!=0){
            eastImages.clear();
        }
        eastImgflag = true;
        westImgflag = false;
        northImgflag = false;
        southImgflag = false;
        requestPermissionPlace();
    }

    public void btnWestImages(View v) {
        if(westImages.size()!=0){
            westImages.clear();
        }
        eastImgflag = false;
        westImgflag = true;
        northImgflag = false;
        southImgflag = false;
        requestPermissionPlace();
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
    public boolean checkPermissions() {

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            //if permission  granted
            return true;
        } else {

            //if permission not granted
            return false;
        }
    }

    public void requestPermissionPlace() {
        if (checkPermissions() == false) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        } else {
            browse_images();
            //permission already granted
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "GRANTED", Toast.LENGTH_SHORT).show();
                browse_images();
            } else {
                Toast.makeText(this, "DENIED", Toast.LENGTH_SHORT).show();
            }
            //this method is called when we ask the user for a permission if he allows us or not depending on its choice.
            //basically a result containing method
        }
    }

    public void browse_images() {
        try {

            Intent i = new Intent();
            i.setType("image/*");
            i.putExtra(i.EXTRA_ALLOW_MULTIPLE, true);
            i.setAction(Intent.ACTION_GET_CONTENT);

            // pass the constant to compare it
            // with the returned requestCode
            startActivityForResult(Intent.createChooser(i, "Select Picture"), PERMISSION_REQUEST_CODE);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("browse Images person method exception", e.getMessage());
        }


    }

    public void onClickGoBackFromPlace(View v) {
        finish();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {

            //bitmaps.clear();
            super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == PERMISSION_REQUEST_CODE) {


                if (resultCode == Activity.RESULT_OK) {
                    if (data.getClipData() != null) {
                        int count = data.getClipData().getItemCount();

                        for (int i = 0; i < count; i++) {
                            if (southImgflag && !westImgflag && !eastImgflag && !northImgflag) {
                                Uri imageUri = data.getClipData().getItemAt(i).getUri();
                                southImages.add(imageUri);

                            } else if (westImgflag && !southImgflag && !eastImgflag && !northImgflag) {
                                Uri imageUri = data.getClipData().getItemAt(i).getUri();
                                westImages.add(imageUri);

                            } else if (eastImgflag && !westImgflag && !southImgflag && !northImgflag) {
                                Uri imageUri = data.getClipData().getItemAt(i).getUri();
                                eastImages.add(imageUri);

                            } else if (northImgflag && !westImgflag && !eastImgflag && !southImgflag) {
                                Uri imageUri = data.getClipData().getItemAt(i).getUri();
                                northImages.add(imageUri);

                            }
//                            Uri imageUri = data.getClipData().getItemAt(i).getUri();
//                            imageUris.add(imageUri);
                        }
                        //picked multiple images
                    } else {
                        if (eastImgflag && !westImgflag && !southImgflag && !northImgflag) {
                            Uri selectedImageUri = data.getData();
                            eastImages.add(selectedImageUri);

                        } else if (westImgflag && !southImgflag && !eastImgflag && !northImgflag) {
                            Uri selectedImageUri = data.getData();
                            westImages.add(selectedImageUri);

                        } else if (northImgflag && !westImgflag && !eastImgflag && !southImgflag) {
                            Uri selectedImageUri = data.getData();
                            northImages.add(selectedImageUri);

                        } else if (southImgflag && !westImgflag && !eastImgflag && !northImgflag) {
                            Uri selectedImageUri = data.getData();
                            southImages.add(selectedImageUri);

                        }
//                        Uri selectedImageUri = data.getData();
//                        imageUris.add(selectedImageUri);
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

            if (southImages.size() != 0) {
                Button btnTempIdSouth = bottomSheetDialog.findViewById(R.id.btn_browse_images_south);
                btnTempIdSouth.setText(String.valueOf(southImages.size()));
            }
            if (eastImages.size() != 0) {
                Button btnTempIdEast = bottomSheetDialog.findViewById(R.id.btn_browse_images_east);
                btnTempIdEast.setText(String.valueOf(eastImages.size()));
            }
            if (northImages.size() != 0) {
                Button btnTempIdNorth = bottomSheetDialog.findViewById(R.id.btn_browse_images_north);
                btnTempIdNorth.setText(String.valueOf(northImages.size()));
            }
            if (westImages.size() != 0) {
                Button btnTempIdWest = bottomSheetDialog.findViewById(R.id.btn_browse_images_west);
                btnTempIdWest.setText(String.valueOf(westImages.size()));
            }
//            TextView v = bottomSheetDialog.findViewById(R.id.tv_selected_images);
//            v.setText("Selected Images: " + imageUris.size());
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("on Activity Image selected result", e.getMessage());
        }
    }

    public boolean checkPlaceExists(String name, Integer fid, Integer tid) {

        for (int i = 0; i < DataStorage.getInstance().getPlacesArrayList().size(); i++) {
            if (DataStorage.getInstance().getPlacesArrayList().get(i).name.equalsIgnoreCase(name)
                    && DataStorage.getInstance().getPlacesArrayList().get(i).typeid == tid
                    && DataStorage.getInstance().getPlacesArrayList().get(i).floorid == fid
            ) {
                return true;
                //Toast.makeText(this, "Place A", Toast.LENGTH_SHORT).show();
            } else {
                continue;
            }

        }
        return false;

    }

    public void on_click_Save(View view) {
        try {


            Button floorSelected = bottomSheetDialog.findViewById(R.id.selectFloortextViewButton);
            String fs = floorSelected.getText().toString();
            Button typeSelected = bottomSheetDialog.findViewById(R.id.selectTypetextViewButton);
            String ts = typeSelected.getText().toString();
            EditText etname = bottomSheetDialog.findViewById(R.id.et_add_location_name_dialog);
            String name = etname.getText().toString();
            Button directionSelected = bottomSheetDialog.findViewById(R.id.selectDoorDirectiontextViewButton);
            String doorDirectionString = directionSelected.getText().toString();

            try {
                if (!NetworkUtils.isNetworkAvailable(getApplicationContext())) {
                    // Handle the case when network is not available
                    Toast.makeText(getApplicationContext(), "Network unavailable", Toast.LENGTH_SHORT).show();
                    Log.e("Network Unavailable", "Please check your internet connection");
                    return;
                }

                if (fs.equalsIgnoreCase("select floor")) {
                    //floorSelected.requestFocus();
                    Toast.makeText(this, "please select floor", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (ts.equalsIgnoreCase("select type")) {
                    //typeSelected.requestFocus();
                    Toast.makeText(this, "please select type", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (doorDirectionString.equalsIgnoreCase("select door direction")) {
                    //directionSelected.requestFocus();
                    Toast.makeText(this, "please select door direction", Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("on click save not any item selected exception", e.getMessage());
            }


            doorDirectionId = DataStorage.getInstance().getDirectionId(doorDirectionString);
            Integer fid = DataStorage.getInstance().getFloorId(fs);
            Integer tid = DataStorage.getInstance().getTypeId(ts);

            boolean v = checkPlaceExists(name, fid, tid);
            if (v) {
                Toast.makeText(this, "Place already exists", Toast.LENGTH_SHORT).show();
                return;
            }


            if (eastImages.size() > 0 && westImages.size() > 0 && southImages.size() > 0 && northImages.size() > 0) {
                Handler handler=new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            uploadPlaces(eastImages, westImages, northImages, southImages, name, fid, tid, doorDirectionId);
                            eastImages.clear();
                            westImages.clear();
                            southImages.clear();
                            northImages.clear();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                });

            } else {
                Toast.makeText(this, "Make sure each side's pictures are greater than 0", Toast.LENGTH_SHORT).show();

                return;
            }

            //uploadPlaces(imageUris.get(0),name,fs,ts);


            bottomSheetDialog.dismiss();
            //imageUris.clear();


        } catch (Exception e) {
            e.printStackTrace();
            Log.e("onClick Save person method exception", e.getMessage());
            Snackbar snackbar = Snackbar.make(view, e.getMessage(), Snackbar.LENGTH_LONG);
            snackbar.show();
        }
    }

    public void addTheDataInTheAdapter() {
        adapter.notifyDataSetChanged();
    }

    public ArrayList<Place> getDataForTheAdapter() {


        Place p = new Place();
        p.name = "LAb";
        //p.type="LAB";
        //p.floor="1st";


        Place p1 = new Place();
        p1.name = "LAb1";
        //p1.type="LAB1";
        //p1.floor="2nd";


        p1.img = Uri.parse("android.resource://com.example.virtualeyeforblinds/mipmap/place");
        p.img = Uri.parse("android.resource://com.example.virtualeyeforblinds/mipmap/place");

        ArrayList<Place> arr1 = new ArrayList<>();
        arr1.add(p);
        arr1.add(p1);
        return arr1;
    }

    private ArrayList<MultipartBody.Part> prepareImageParts(ArrayList<Uri> images) {
        try {
            ArrayList<MultipartBody.Part> parts = new ArrayList<>();

            for (Uri imageUri : images) {
                // Convert Uri to File
                File file = new File(imageUri.getPath());

                // Create RequestBody from file
                RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);

                // Create MultipartBody.Part from RequestBody
                MultipartBody.Part part = MultipartBody.Part.createFormData("images/jpeg", file.getName(), requestFile);

                parts.add(part);
            }


            return parts;
        } catch (Exception e) {
            Log.e("prepareImageParts method", e.getMessage());
            return null;
        }

    }

    public void uploadPlaces(ArrayList<Uri> eastImages, ArrayList<Uri> westImages, ArrayList<Uri> northImages,
                             ArrayList<Uri> southImages, String name, Integer floorId, Integer typeId,
                             Integer doorDirectionId) {

        try {


            // Convert integers to RequestBody
            RequestBody nameBody = RequestBody.create(MediaType.parse("text/plain"), name);
            RequestBody floorIdBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(floorId));
            RequestBody typeIdBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(typeId));
            RequestBody doorDirectionIdBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(doorDirectionId));

            // Make the API call using Retrofit
            WebApi api = RetrofitClient.getInstance().getMyApi();
            ArrayList<MultipartBody.Part> eastParts = api.prepareFileParts("east", eastImages, this);
            ArrayList<MultipartBody.Part> westParts = api.prepareFileParts("west", westImages, this);
            ArrayList<MultipartBody.Part> northParts = api.prepareFileParts("north", northImages, this);
            ArrayList<MultipartBody.Part> southParts = api.prepareFileParts("south", southImages, this);
            Call<SimpleResponse> call = api.uploadPlaces(nameBody, floorIdBody, typeIdBody, doorDirectionIdBody,eastParts,westParts,northParts,southParts);

            call.enqueue(new Callback<SimpleResponse>() {
                @Override
                public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> response) {
                    if (response.isSuccessful()) {
                        try {
                            SimpleResponse s = response.body();

                            Button floorSelected = bottomSheetDialog.findViewById(R.id.selectFloortextViewButton);
                            String fs = floorSelected.getText().toString();
                            Button typeSelected = bottomSheetDialog.findViewById(R.id.selectTypetextViewButton);
                            String ts = typeSelected.getText().toString();
                            EditText etname = bottomSheetDialog.findViewById(R.id.et_add_location_name_dialog);
                            String name = etname.getText().toString();
                            Button directionSelected = bottomSheetDialog.findViewById(R.id.selectDoorDirectiontextViewButton);
                            String doorDirectionString = directionSelected.getText().toString();
                            Place place = new Place();
                            place.name = name;
                            place.typename = ts;
                            place.floorname = fs;
                            place.doorDirectionName = DataStorage.getInstance().getDirectionName(doorDirectionId);
                            //p.doordirectionid=doorDirectionId;
                            //p.doorDirectionid=doorDirectionId;

                            //p.img = eastImages.get(0);
                            adapter.addPlace(place);
                            //addTheDataInTheAdapter();
                            DataStorage.getInstance().getPlacesList();
                            Snackbar snackbar = Snackbar.make(view, s.getMessage() + "", Snackbar.LENGTH_LONG);
                            snackbar.show();


                        }catch (Exception e){
                            e.printStackTrace();
                            Log.e("in method of upload places response",e.getMessage());
                            Snackbar snackbar = Snackbar.make(view, e.getMessage() + "", Snackbar.LENGTH_LONG);
                            snackbar.show();

                        }

                    } else {

                        Snackbar snackbar = Snackbar.make(view, "place couldn't be uploaded", Snackbar.LENGTH_LONG);
                        snackbar.show();

                        // Handle unsuccessful response
                    }
                }

                @Override
                public void onFailure(Call<SimpleResponse> call, Throwable t) {

                    Snackbar snackbar = Snackbar.make(view, "place couldn't be uploaded.. API failure", Snackbar.LENGTH_LONG);
                    snackbar.show();

                }
            });


        } catch (Exception e) {
            e.printStackTrace();

            Snackbar snackbar = Snackbar.make(view, e.getMessage(), Snackbar.LENGTH_LONG);
            snackbar.show();
            Log.e("uploadPlaces Method", e.getMessage());
        }


    }


    public ArrayList<Place> changeData(ArrayList<Place> p) {

        try {
            DataStorage d = DataStorage.getInstance();
            if(d.typesArrayList==null || d.typesArrayList.isEmpty() || d.floorsArrayList==null || d.floorsArrayList.isEmpty() || d.directionArrayList==null || d.directionArrayList.isEmpty()){
                return null;
            }

            for (int r = 0; r < d.typesArrayList.size(); r++) {
                for (int t = 0; t < p.size(); t++) {
                    if (p.get(t).typeid == d.typesArrayList.get(r).id) {
                        p.get(t).typename = d.getTypesArrayList().get(r).name;
                    }

                }


            }
            //Log.e("floor list size",d.floorsArrayList.size()+"");
            //Toast.makeText(this, d.floorsArrayList.size(), Toast.LENGTH_SHORT).show();
            for (int k = 0; k < d.floorsArrayList.size(); k++) {
                for (int q = 0; q < p.size(); q++) {
                    if (p.get(q).floorid == d.floorsArrayList.get(k).id) {
                        // Log or print values for debugging


                        p.get(q).floorname = d.getFloorsArrayList().get(k).name;


                    }
                }
            }

            for (int q = 0; q < d.directionArrayList.size(); q++) {
                for (int a = 0; a < p.size(); a++) {
                    if (p.get(a).doordirectionid == null) {
                        p.get(a).doorDirectionName = "0";
                    } else if (p.get(a).doordirectionid == d.directionArrayList.get(q).id) {
                        // Log or print values for debugging


                        p.get(a).doorDirectionName = d.directionArrayList.get(q).name;


                    } else {
                        continue;
                    }
                }
            }


            return p;
        } catch (Exception e) {
            Snackbar snackbar = Snackbar.make(view, e.getMessage(), Snackbar.LENGTH_LONG);
            snackbar.show();
            e.printStackTrace();
            Log.e("change Data of Person Method Exception",e.getMessage());
            return null;
        }
    }




    public void createFloor(String s) {

        try {
            WebApi api = RetrofitClient.getInstance().getMyApi();

            floors f = new floors();
            f.name = s;

            if (!NetworkUtils.isNetworkAvailable(getApplicationContext())) {
                // Handle the case when network is not available
                Toast.makeText(getApplicationContext(), "Network unavailable", Toast.LENGTH_SHORT).show();
                Log.e("Network Unavailable", "Please check your internet connection");
                return;
            }
            if(s.isEmpty() || s==null){
                Toast.makeText(this, "please write something in the textBox", Toast.LENGTH_SHORT).show();
                return;
            }

            for(int i=0;i<DataStorage.getInstance().getFloorsArrayList().size();i++){
                if(DataStorage.getInstance().getFloorsArrayList().get(i).name.equalsIgnoreCase(s)){
                    Toast.makeText(this, "floor already exists", Toast.LENGTH_SHORT).show();
                    return;
                }

            }



            api.createFloor(f).enqueue(new Callback<SimpleResponse>() {
                            @Override
                public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> response) {
                    if(response.isSuccessful()){
                        Snackbar snackbar = Snackbar.make(view, "Floor has been added", Snackbar.LENGTH_LONG);
                        snackbar.show();

                        String newFloorName = f.name;
                        floor.clear();
                        for(int i=0;i<DataStorage.getInstance().getFloorsArrayList().size();i++){
                            floor.add(DataStorage.getInstance().getFloorsArrayList().get(i).name);
                        }
                        floor.add(s);


                        floorAdapter = new FloorAdapter(floor);
                        floorRecyclerView.setAdapter(floorAdapter);
                        floorRecyclerView.setLayoutManager(new LinearLayoutManager(PlacesActivity.this));
                        searchViewFloor.setQuery("", false);
                        DataStorage.getInstance().getAllFloors();

                    }
                    else{
                        Toast.makeText(PlacesActivity.this, "floor has not been added", Toast.LENGTH_SHORT).show();
                    }
                }



                @Override
                public void onFailure(Call<SimpleResponse> call, Throwable t) {
                    Snackbar snackbar = Snackbar.make(view, "Api Failure in Adding Exception", Snackbar.LENGTH_LONG);
                    snackbar.show();
                    Log.e("Failure in Adding Floor exception", t.getMessage());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void createType(String s){
        try {
            WebApi api=RetrofitClient.getInstance().getMyApi();
            Types t=new Types();
            t.name=s;

            if (!NetworkUtils.isNetworkAvailable(getApplicationContext())) {
                // Handle the case when network is not available
                Toast.makeText(getApplicationContext(), "Network unavailable", Toast.LENGTH_SHORT).show();
                Log.e("Network Unavailable", "Please check your internet connection");
                return;
            }
            if(s.isEmpty() || s==null){
                Toast.makeText(this, "please write something in the textBox", Toast.LENGTH_SHORT).show();
                return;
            }

            for(int i=0;i<DataStorage.getInstance().getTypesArrayList().size();i++){
                if(DataStorage.getInstance().getTypesArrayList().get(i).name.equalsIgnoreCase(s)){
                    Toast.makeText(this, "type already exists", Toast.LENGTH_SHORT).show();
                    return;
                }

            }




            api.createType(t).enqueue(new Callback<SimpleResponse>() {
                @Override
                public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> response) {
                    if(response.isSuccessful()){
                        String newFloorName = t.name;
                        typeList.clear();
                        for(int i=0;i<DataStorage.getInstance().getTypesArrayList().size();i++){
                            typeList.add(DataStorage.getInstance().getTypesArrayList().get(i).name);
                        }
                        typeList.add(s);


                        typeAdapter = new TypeAdapter(typeList);
                        typeRecyclerView.setAdapter(typeAdapter);
                        typeRecyclerView.setLayoutManager(new LinearLayoutManager(PlacesActivity.this));
                        searchViewType.setQuery("", false);
                        DataStorage.getInstance().getAllTypes();
                        Toast.makeText(getApplicationContext(), "New type has been added", Toast.LENGTH_SHORT).show();

                    }else{
                        Toast.makeText(PlacesActivity.this, "ew type has not been added", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<SimpleResponse> call, Throwable t) {

                    Log.e("Api failure of adding type",t.getMessage());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Adding type in Place method exception",e.getMessage());
        }
    }

    public void onTypeSelected(String typeName) {
        try {
            if (typeName != null && !typeName.isEmpty()) {
                Button b = bottomSheetDialog.findViewById(R.id.selectTypetextViewButton);
                if (b != null) {
                    b.setText(typeName);
                }
            } else {
                Button b = bottomSheetDialog.findViewById(R.id.selectTypetextViewButton);
                if (b != null) {
                    b.setText("select floor");
                }
            }

            if (typePopup != null) {
                if (typePopup.isShowing()) {
                    Log.e("Popup", "Popup is showing, attempting to dismiss");
                    typePopup.dismiss();
                } else {
                    Log.e("Popup", "Popup is not showing");
                }
            } else {
                Log.e("Popup", "Popup reference is null");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Ex", e.getMessage());
        }
    }

    public void ManagingDialogsPopUp(String floorString,String typeString,String doorDirectionString) {

        try {
            bottomSheetDialog = new BottomSheetDialog(PlacesActivity.this);
            bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog_places);
            View container = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            container.setBackground(ContextCompat.getDrawable(PlacesActivity.this, R.drawable.design_for_bottom_sheet));


            ImageButton typeDropDownButton = bottomSheetDialog.findViewById(R.id.selectTypeDropDownButton);
            Button typeTextViewButton = bottomSheetDialog.findViewById(R.id.selectTypetextViewButton);
            ImageButton doorDropDownDirectionButton=bottomSheetDialog.findViewById(R.id.selectDoorDirectionDropDownButton);
            Button doortextViewDirectionButton=bottomSheetDialog.findViewById(R.id.selectDoorDirectiontextViewButton);
            typeTextViewButton.setText(typeString);
            doortextViewDirectionButton.setText(doorDirectionString);

            try {

                doorDropDownDirectionButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                        popupView = inflater.inflate(R.layout.dialog_custom_layout_doordirection, null);
                        doorDirectionPopup = new PopupWindow(popupView, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
                        doorDirectionPopup.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                        doorDirectionPopup.setOutsideTouchable(true);
                        doorDirectionPopup.setFocusable(true);

                        Button eastButton = popupView.findViewById(R.id.east_button_dialogdoorDirection);
                        Button westButton = popupView.findViewById(R.id.west_button_dialogdoorDirection);
                        Button northButton = popupView.findViewById(R.id.north_button_dialogdoorDirection);
                        Button southButton = popupView.findViewById(R.id.south_button_dialogdoorDirection);

                        eastButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                doortextViewDirectionButton.setText("East");
                                doorDirectionPopup.dismiss();
                            }
                        });

                        westButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                doortextViewDirectionButton.setText("West");
                                doorDirectionPopup.dismiss();
                            }
                        });

                        northButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                doortextViewDirectionButton.setText("North");
                                doorDirectionPopup.dismiss();
                            }
                        });

                        southButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                doortextViewDirectionButton.setText("South");
                                doorDirectionPopup.dismiss();
                            }
                        });
                        doorDirectionPopup.showAtLocation(view, Gravity.CENTER, 0, 0);
                    }
                });

                typeDropDownButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                        popupView = inflater.inflate(R.layout.dialog_custom_layout_type, null);
                        typePopup = new PopupWindow(popupView, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);

                        typePopup.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                        typePopup.setOutsideTouchable(true);
                        typePopup.setFocusable(true);

                        Button typeAddButton=popupView.findViewById(R.id.addButtonType);
                        searchViewType=popupView.findViewById(R.id.searchViewType);

                        searchViewType.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                            @Override
                            public boolean onQueryTextSubmit(String s) {
                                searchViewType.clearFocus();
                                boolean itemFound = typeAdapter.isItemInList(s);

                                if (itemFound) {
                                    // Perform actions for when an item is found

                                } else {
                                    // Perform actions for when no item is found
                                    // For example:
                                    searchViewType.setQuery("",false);
                                    Toast.makeText(PlacesActivity.this, "Item not found!", Toast.LENGTH_SHORT).show();

                                }
                                return true;
                            }

                            @Override
                            public boolean onQueryTextChange(String s) {
                                typeAdapter.setCurrentQuery(s);
                                return false;
                            }
                        });

                        typeAddButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                createType(searchViewType.getQuery().toString());
                            }
                        });

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            typePopup.setElevation(20);
                        }

                        // Find the RecyclerView in your dialog layout
                        typeRecyclerView = popupView.findViewById(R.id.typeRecyclerView);


                        // Create your adapter and set it to the RecyclerView
                        typeList = new ArrayList<>();
                        for (int i = 0; i < DataStorage.getInstance().getTypesArrayList().size(); i++) {
                            Log.e("list of types", i + "" + DataStorage.getInstance().getTypesArrayList().get(i).name);
                            typeList.add(DataStorage.getInstance().getTypesArrayList().get(i).name);
                        }

                        typeAdapter = new TypeAdapter(typeList);
                        typeRecyclerView.setAdapter(typeAdapter);

                        // Optionally, set a LayoutManager to the RecyclerView if not done in XML
                        typeRecyclerView.setLayoutManager(new LinearLayoutManager(PlacesActivity.this));


                        // Dismiss the floor PopupWindow on cancel
                        typePopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
                            @Override
                            public void onDismiss() {
                                // Do something on dismiss if needed
                            }
                        });

                        // Show the floor PopupWindow
                        typePopup.showAtLocation(view, Gravity.CENTER, 0, 0);


                    }
                });
            } catch (Exception e) {
                Snackbar snackbar = Snackbar.make(view, "type and floor popup exception", Snackbar.LENGTH_LONG);
                snackbar.show();
                Log.e("Excep",e.getMessage());
            }


            // Find the button in the popup layout
            ImageButton floorDropDownButton = bottomSheetDialog.findViewById(R.id.selectFloorDropDownButton);
            Button floorTextViewButton = bottomSheetDialog.findViewById(R.id.selectFloortextViewButton);
            floorTextViewButton.setText(floorString);


            // Set click listener for the button
            floorDropDownButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                    popupView = inflater.inflate(R.layout.dialog_custom_layout, null);


                    floorPopup = new PopupWindow(popupView, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);


                    // Set background drawable for the floor PopupWindow
                    floorPopup.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                    floorPopup.setOutsideTouchable(true);
                    floorPopup.setFocusable(true);


                    AddFloorButton = popupView.findViewById(R.id.addButton);
                    searchViewFloor = popupView.findViewById(R.id.searchViewFloor);

                    searchViewFloor.setOnCloseListener(new SearchView.OnCloseListener() {
                        @Override
                        public boolean onClose() {

                            floorAdapter.filter(floorString);
                            return false;
                        }
                    });
                    searchViewFloor.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                        @Override
                        public boolean onQueryTextSubmit(String s) {

                            searchViewFloor.clearFocus();
                            boolean itemFound = floorAdapter.isItemInList(s);

                            if (itemFound) {
                                // Perform actions for when an item is found
                                // For example:

                            } else {
                                // Perform actions for when no item is found
                                // For example:
                                searchViewFloor.setQuery("",false);
                                 Toast.makeText(PlacesActivity.this, "Item not found!", Toast.LENGTH_SHORT).show();

                            }


                            return true;
                        }

                        @Override
                        public boolean onQueryTextChange(String s) {


                            // Update the dataset based on the search query
                            floorAdapter.setCurrentQuery(s);


                            return false;
                        }
                    });


                    AddFloorButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            createFloor(searchViewFloor.getQuery().toString());
                        }
                    });


                    // Set elevation for a shadow effect if needed
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        floorPopup.setElevation(20);
                    }

                    // Find the RecyclerView in your dialog layout
                    floorRecyclerView = popupView.findViewById(R.id.floorRecyclerView);

                    // Create your adapter and set it to the RecyclerView
                    floor = new ArrayList<>();
                    for (int i = 0; i < DataStorage.getInstance().getFloorsArrayList().size(); i++) {
                        Log.e("list of floors", i + "" + DataStorage.getInstance().getFloorsArrayList().get(i).name);
                        floor.add(DataStorage.getInstance().getFloorsArrayList().get(i).name);
                    }

                    floorAdapter = new FloorAdapter(floor);
                    floorRecyclerView.setAdapter(floorAdapter);

                    // Optionally, set a LayoutManager to the RecyclerView if not done in XML
                    floorRecyclerView.setLayoutManager(new LinearLayoutManager(PlacesActivity.this));


                    // Dismiss the floor PopupWindow on cancel
                    floorPopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
                        @Override
                        public void onDismiss() {
                            // Do something on dismiss if needed
                        }
                    });

                    // Show the floor PopupWindow
                    floorPopup.showAtLocation(view, Gravity.CENTER, 0, 0);
                }

            });


            bottomSheetDialog.show();
        } catch (RuntimeException e) {
            e.printStackTrace();
            Log.e("Method Manage dialog pop up",e.getMessage());
        }

    }

    public void onFloorSelected(String floorName) {
        try {
            if (floorName != null && !floorName.isEmpty()) {
                Button b = bottomSheetDialog.findViewById(R.id.selectFloortextViewButton);
                if (b != null) {
                    b.setText(floorName);
                }
            } else {
                Button b = bottomSheetDialog.findViewById(R.id.selectFloortextViewButton);
                if (b != null) {
                    b.setText("select floor");
                }
            }

            if (floorPopup != null) {
                if (floorPopup.isShowing()) {
                    Log.e("Popup", "Popup is showing, attempting to dismiss");
                    floorPopup.dismiss();
                } else {
                    Log.e("Popup", "Popup is not showing");
                }
            } else {
                Log.e("Popup", "Popup reference is null");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Ex", e.getMessage());
        }
    }


    private void setUpRecyclerView(ArrayList<Place> placesArrayList) {
        try {
            if (placesArrayList != null && !placesArrayList.isEmpty()) {
                LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                binding.itemsListRcv.setLayoutManager(layoutManager);

                // Ensure changeData method is handled safely
                ArrayList<Place> processedData = changeData(placesArrayList);



                if (processedData != null && !processedData.isEmpty()) {
                    adapter = new MyAdapter(processedData);
                    binding.itemsListRcv.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                } else {
                    // Handle case where changeData returns null or empty data
                    showErrorUI();
                }
            } else {
                // Handle case where placesArrayList is null or empty
                showErrorUI();
            }
        } catch (Exception e) {
            e.printStackTrace();
            showErrorUI();
        }
    }


    public void loadingPlacesAgain() {


        //countdownTextView.setVisibility(View.VISIBLE); // Show the countdown TextView

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
//                        DataStorage.getInstance().getAllTypes();
//                        DataStorage.getInstance().getAllDirections();
//                        DataStorage.getInstance().getAllFloors();
//                        DataStorage.getInstance().getPlacesList();
                        // Update countdown TextView with remaining seconds
                        int secondsRemaining = (int) (remainingTime / 1000);
                        binding.errorTextViewPlace.setText("Remaining: " + secondsRemaining + " seconds");


                        placesArrayList = DataStorage.getInstance().getPlacesArrayList();

                        if (placesArrayList != null && !placesArrayList.isEmpty()) {
                            // If data is available, set up the RecyclerView
                            setUpRecyclerView(placesArrayList);
                            binding.itemsListRcv.setVisibility(View.VISIBLE);
                            binding.errorTextViewPlace.setVisibility(View.GONE);
                            binding.errorLayoutPlace.setVisibility(View.GONE);// Hide countdown TextView
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


    public void loadPlaces() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                Handler handler = new Handler(getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {





                        if (placesArrayList != null && !placesArrayList.isEmpty()) {
                            // If places are available, set up the RecyclerView
                            setUpRecyclerView(placesArrayList);
                        } else {
                            // If places are null or empty, show an error message
                            showErrorUI();
                        }
                    }
                });
            }
        });
        t.start();
    }
    private void showErrorUI() {
        // Hide the RecyclerView
        binding.itemsListRcv.setVisibility(View.GONE);

        binding.errorLayoutPlace.setVisibility(View.VISIBLE);

        // Show an error message
        binding.errorTextViewPlace.setVisibility(View.VISIBLE);
        //binding.errorTextViewPlace.setText("Error loading places. Please check your connection.");

        // Show a retry button
        binding.reloadButtonPlace.setVisibility(View.VISIBLE);
        binding.reloadButtonPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Retry loading places
                loadingPlacesAgain();

                // Hide error UI elements on retry
                binding.reloadButtonPlace.setVisibility(View.GONE);
                //binding.errorTextViewPlace.setText("Getting Data...Please wait for 1 minute");

            }
        });
    }
    private void setupNetworkAndLoadData() {
        if (!NetworkUtils.isNetworkAvailable(getApplicationContext())) {
            // Network is not available, show error UI
            showErrorUI();
            Log.e("Network Unavailable", "Please check your internet connection");
        } else {

            loadPlaces();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {


            super.onCreate(savedInstanceState);
            binding = ActivityPlacesBinding.inflate(getLayoutInflater());

            setContentView(binding.getRoot());
            view = findViewById(android.R.id.content);
            eastImages=new ArrayList<>();
            westImages=new ArrayList<>();
            southImages=new ArrayList<>();
            northImages=new ArrayList<>();

            placesArrayList = DataStorage.getInstance().getPlacesArrayList();



            setupNetworkAndLoadData();

            binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    return false;
                }

                @Override
                public boolean  onQueryTextChange(String s) {
                    ArrayList<Place> filteredList = filter(placesArrayList, s);


                    adapter.updateList(filteredList);
                    return true;
                }
            });




            binding.btnAddPlaces.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    ManagingDialogsPopUp("select floor","select type","select Door Direction");

                }
            });


        } catch (Exception e) {
            e.printStackTrace();
            Snackbar snackbar = Snackbar.make(view, "exception in onCreate Method", Snackbar.LENGTH_LONG);
            snackbar.show();
        }
    }

    private ArrayList<Place> filter(ArrayList<Place> places, String query) {
        try {
            if(places.isEmpty() || places==null){
                Snackbar snackbar=Snackbar.make(view,"Cannot Search on Empty data",Snackbar.LENGTH_LONG);
                snackbar.show();
                return null;
            }
            query = query.toLowerCase();
            ArrayList<Place> filteredList = new ArrayList<>();

            for (Place place : places) {
                // Implement your filtering logic here, for example, filtering by place name
                if (place.getName().toLowerCase().contains(query)) {
                    filteredList.add(place);
                }
            }
            return filteredList;
        }catch (Exception e){
            e.printStackTrace();
            return null;

        }
    }
    @Override
    protected void onDestroy() {
        DataStorage.getInstance().getPlacesList();
        super.onDestroy();

    }
}