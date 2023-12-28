package com.example.virtualeyeforblinds.globalClass;

import static android.content.ContentValues.TAG;

import static com.example.virtualeyeforblinds.extraClasses.NetworkUtils.isNetworkAvailable;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.icu.text.SimpleDateFormat;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;

import com.example.virtualeyeforblinds.Person;
import com.example.virtualeyeforblinds.PersonActivity;
import com.example.virtualeyeforblinds.Place;
import com.example.virtualeyeforblinds.PlacesActivity;
import com.example.virtualeyeforblinds.api.RetrofitClient;
import com.example.virtualeyeforblinds.api.WebApi;
import com.example.virtualeyeforblinds.extraClasses.NetworkUtils;
import com.example.virtualeyeforblinds.models.Direction;
import com.example.virtualeyeforblinds.models.Images;
import com.example.virtualeyeforblinds.models.Types;
import com.example.virtualeyeforblinds.models.floors;
import com.example.virtualeyeforblinds.models.Links;
import com.example.virtualeyeforblinds.progessbar.ProgressDialogFragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DataStorage {




    public ArrayList<String> personImages=new ArrayList<>();

    public ArrayList<Types> typesArrayList=new ArrayList<>();

    public ArrayList<floors> floorsArrayList=new ArrayList<>();

    public ArrayList<Direction> directionArrayList=new ArrayList<>();

    public ArrayList<Direction> getDirectionArrayList() {
        return directionArrayList;
    }







    public void getAllLinks(){
        WebApi api= RetrofitClient.getInstance().getMyApi();
        api.getAllLinks().enqueue(new Callback<ArrayList<Links>>() {
            @Override
            public void onResponse(Call<ArrayList<Links>> call, Response<ArrayList<Links>> response) {
                if(response.isSuccessful()){
                    DataStorage dataStorage=DataStorage.getInstance();
                    dataStorage.setLinksArrayList(response.body());

                }
                else{

                    Log.d(TAG, "onResponse: unsuccessful");
                    //Toast.makeText(placesActivity, "response was unsuccesful", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Links>> call, Throwable t) {
                Log.d(TAG, "onFailure: networkk failure"+t.getMessage());
                //  Toast.makeText(placesActivity, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    public ArrayList<Place> changeDataOfPlace(ArrayList<Place> p) {

        DataStorage d = DataStorage.getInstance();


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

        for(int q=0;q<d.directionArrayList.size();q++){
            for (int a = 0; a < p.size(); a++) {
                if(p.get(a).doordirectionid==null){
                    p.get(a).doorDirectionName="0";
                }
                else if (p.get(a).doordirectionid == d.directionArrayList.get(q).id) {
                    // Log or print values for debugging


                    p.get(a).doorDirectionName = d.directionArrayList.get(q).name;


                }
                else{
                    continue;
                }
            }
        }


        return p;
    }

    public void getPlacesList() {
        try {
//            ProgressDialogFragment progressDialog = new ProgressDialogFragment();
//            progressDialog.show(getSupportFragmentManager(), "progress_dialog");

            // Make the API request
            WebApi api = RetrofitClient.getInstance().getMyApi();
            api.getAllplaces().enqueue(new Callback<ArrayList<Place>>() {
                @Override
                public void onResponse(Call<ArrayList<Place>> call, Response<ArrayList<Place>> response) {
                    if (response.isSuccessful()) {

                                ArrayList<Place> arr1 = response.body();
                        DataStorage.getInstance().setPlacesArrayList(arr1);




                    }
                }

                @Override
                public void onFailure(Call<ArrayList<Place>> call, Throwable t) {
                    // Handle API request failure
                    //progressDialog.dismiss();
                    //Toast.makeText(, "API Request Failed", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Log.d("exception in getting places", e.getMessage());
        }
    }




    private void saveImageLocally(InputStream inputStream, String name, ImageSaveCallback callback) {
        try {
            File imageDirectory = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

            if (imageDirectory != null) {
                // Generate a unique filename with a timestamp

                String filename = name;

                // Create the image file
                File imageFile = new File(imageDirectory, filename);

                // Save the image to the file
                FileOutputStream outputStream = new FileOutputStream(imageFile);
                byte[] buffer = new byte[8 * 1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                outputStream.close();

                // Get the URI of the saved image
                String imagePath = imageFile.getAbsolutePath(); // This is the URI/path of the saved image
                callback.onImageSaved(imagePath); // Callback with the image path
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface ImageSaveCallback {
        void onImageSaved(String imagePath);
    }
    public String getImageDirectory(Context context) {
        if (context != null) {
            return context.getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath();
        } else {
            return null;
        }
    }


    public void getPersonImages(){
        for(int i=0;i<personArrayList.size();i++) {
            WebApi api = RetrofitClient.getInstance().getMyApi();
            final String s = personArrayList.get(i).Name;
            if (!s.equalsIgnoreCase("Tanzeela")) {
                api.getImageByName(personArrayList.get(i).Name).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            saveImageLocally(response.body().byteStream(), s, new ImageSaveCallback() {
                                @Override
                                public void onImageSaved(String imagePath) {
                                    Log.e("person Image path",imagePath);
                                    personImages.add(imagePath);
                                }
                            });
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        // Handle failure
                    }
                });
            }
        }
    }



    public void getPersonsList() {


        // Make the API request
        WebApi api = RetrofitClient.getInstance().getMyApi();
        api.getAllPersons().enqueue(new Callback<ArrayList<Person>>() {
            @Override
            public void onResponse(Call<ArrayList<Person>> call, Response<ArrayList<Person>> response) {
                if (response.isSuccessful()) {
                    ArrayList<Person> arr1 = response.body();



                            DataStorage dataStorage = DataStorage.getInstance();
                            dataStorage.setPersonArrayList(arr1);

                            getPersonImages();
                            //getImagesOfPersons();




                }
            }

            @Override
            public void onFailure(Call<ArrayList<Person>> call, Throwable t) {
                // Handle API request failure

                //Toast.makeText(, "API Request Failed", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void getAllTypes() {
        WebApi api = RetrofitClient.getInstance().getMyApi();
        api.getAllTypes().enqueue(new Callback<ArrayList<Types>>() {
            @Override
            public void onResponse(Call<ArrayList<Types>> call, Response<ArrayList<Types>> response) {
                if (response.isSuccessful()) {
                    DataStorage d = DataStorage.getInstance();
                    d.setTypesArrayList(response.body());

                }
            }

            @Override
            public void onFailure(Call<ArrayList<Types>> call, Throwable t) {

            }
        });
    }


    public void getAllFloors() {
        WebApi api = RetrofitClient.getInstance().getMyApi();
        api.getAllFloors().enqueue(new Callback<ArrayList<floors>>() {
            @Override
            public void onResponse(Call<ArrayList<floors>> call, Response<ArrayList<floors>> response) {
                if (response.isSuccessful()) {
                    DataStorage d = DataStorage.getInstance();
                    d.setFloorsArrayList(response.body());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<floors>> call, Throwable t) {

            }
        });
    }

    public String getDirectionName(Integer id){
        for(int i=0;i<directionArrayList.size();i++){
            if(id==directionArrayList.get(i).id){
                return directionArrayList.get(i).name;

            }
        }
        return null;
    }

    public Integer getFloorId(String floorname){
        for(int i=0;i<floorsArrayList.size();i++){
            if(floorname.equalsIgnoreCase(floorsArrayList.get(i).name)){
                return floorsArrayList.get(i).id;
            }
        }
        return -1;
    }

    public Integer getTypeId(String typename){
        for(int i=0;i<typesArrayList.size();i++){
            if(typename.equalsIgnoreCase(typesArrayList.get(i).name)){
                return typesArrayList.get(i).id;
            }
        }
        return -1;
    }

    public String getTypeName(int id){
        for(int i=0;i<typesArrayList.size();i++){
            if(id==typesArrayList.get(i).id){
                return typesArrayList.get(i).name;
            }
        }
        return null;
    }

    public String getFloorName(int id){
        for(int i=0;i<floorsArrayList.size();i++){
            if(id==floorsArrayList.get(i).id){
                return floorsArrayList.get(i).name;
            }
        }
        return null;
    }


    public Integer getDirectionId(String directionname){
        for(int i=0;i<directionArrayList.size();i++){
            if(directionname.equalsIgnoreCase(directionArrayList.get(i).name)){
                return directionArrayList.get(i).id;
            }
        }
        return -1;
    }

    public void getAllDirections(){
        WebApi api=RetrofitClient.getInstance().getMyApi();
        api.getAllDirections().enqueue(new Callback<ArrayList<Direction>>() {
            @Override
            public void onResponse(Call<ArrayList<Direction>> call, Response<ArrayList<Direction>> response) {
                if(response.isSuccessful()){

                    DataStorage d = DataStorage.getInstance();
                    d.setDirectionArrayList(response.body());
                    Log.e("direction list size which came from API",d.directionArrayList.size()+"");
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Direction>> call, Throwable t) {

            }
        });
    }

    public void setDirectionArrayList(ArrayList<Direction> directionArrayList) {
        this.directionArrayList = directionArrayList;
    }

    public ArrayList<Types> getTypesArrayList() {
        return typesArrayList;
    }

    public void setTypesArrayList(ArrayList<Types> typesArrayList) {
        this.typesArrayList = typesArrayList;
    }

    public ArrayList<floors> getFloorsArrayList() {
        return floorsArrayList;
    }

    public void setFloorsArrayList(ArrayList<floors> floorsArrayList) {
        this.floorsArrayList = floorsArrayList;
    }

    private static DataStorage instance;
    public String sharedData;

    public ArrayList<Links> linksArrayList=new ArrayList<>();

    public ArrayList<Links> getLinksArrayList() {
        return linksArrayList;
    }

    public void setLinksArrayList(ArrayList<Links> linksArrayList) {
        this.linksArrayList = linksArrayList;
    }

    public ArrayList<Person> personArrayList=new ArrayList<>();

    public ArrayList<Place> placesArrayList=new ArrayList<>();

    public ArrayList<Place> getPlacesArrayList() {
        return placesArrayList;
    }

    public void setPlacesArrayList(ArrayList<Place> placesArrayList) {
        this.placesArrayList = placesArrayList;
    }

    private DataStorage() {
        // Private constructor to prevent instantiation from outside
    }

    public static DataStorage getInstance() {
        if (instance == null) {
            synchronized (DataStorage.class) {
                if (instance == null) {
                    instance = new DataStorage();
                }
            }
        }
        return instance;
    }

    public void setSharedData(String data) {
        this.sharedData = data;
    }
    public void setPersonArrayList(ArrayList<Person> personArrayList){
        this.personArrayList=personArrayList;
    }
    private Context context;

    public void setContext(Context context) {
        this.context = context;
    }


    public String getSharedData() {
        return sharedData;
    }
    public ArrayList<Person> getPersonArrayList(){
        return personArrayList;
    }
}

