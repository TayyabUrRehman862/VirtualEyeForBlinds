package com.example.virtualeyeforblinds;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraInfo;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.CameraX;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.core.ZoomState;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.LifecycleOwner;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.util.Rational;
import android.util.Size;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.virtualeyeforblinds.api.RetrofitClient;
import com.example.virtualeyeforblinds.api.WebApi;
import com.example.virtualeyeforblinds.databinding.ActivityMainBinding;
import com.example.virtualeyeforblinds.extraClasses.ApiResponeOfFrameObject;
import com.example.virtualeyeforblinds.extraClasses.ApiResponseOfFrame;
import com.example.virtualeyeforblinds.extraClasses.Coordinate;
import com.example.virtualeyeforblinds.extraClasses.DataStorage;
import com.example.virtualeyeforblinds.extraClasses.OverlayView;
import com.example.virtualeyeforblinds.extraClasses.Path;
import com.example.virtualeyeforblinds.extraClasses.PathGetter;
import com.example.virtualeyeforblinds.progessbar.ProgressDialogFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.common.util.concurrent.ListenableFuture;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

import kotlinx.coroutines.scheduling.Task;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    PersonActivity p=new PersonActivity();

    //SharedPreferences preferences;

    ArrayList<MultipartBody.Part> collectingImages=new ArrayList<>();


    int tempheight=0;

    int tempwidth=0;

    private volatile Object lockForPath=new Object();
    private volatile Object lockForpicture=new Object();


    PathGetter pfortask=new PathGetter();
    public volatile boolean isApiRequestForPathinProgress=false;

    WebApi forgettingPath;

//    public boolean firstTimeGettingPath=false;

    public volatile String previousPlace="";

    public volatile ArrayList<Path> newPath=new ArrayList<>();

    volatile ApiResponseOfFrame getCurrentValuesForNavigation=new ApiResponseOfFrame();

    private volatile boolean goingfornavigation = false;
    private OverlayView overlayView;


    private ArrayList<Coordinate> which_objects;
    private volatile SpeechRecognizer speechRecognizer;
    private Intent intentRecognizer;
    int SPEECH_REQUEST_CODE = 100;
    public boolean isObjectDetection = false;
    public boolean isPersonDetection = false;
    public boolean isNavigation = false;
    private volatile boolean isApiRequestInProgress = false;
    final int PERMISSION_REQUEST_CODE = 1001;
    final int CAMERA_RESULT_CODE = 1002;
    ActivityMainBinding binding;
    private ListenableFuture<ProcessCameraProvider> cameraProviderListenableFuture;
    NavigationView nav;
    ActionBarDrawerToggle toggle;
    DrawerLayout drawerLayout;
    ImageCapture imageCapture;
    private TextToSpeech textToSpeech;

    private TextToSpeech textToSpeech1;
    int counter=0;


    private class CaptureImageTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            try {
              //  CaptureImageTask.this.wait(1000);
                isApiRequestInProgress=false;
                captureImage();
                //startSpeechRecognition();
            }catch (Exception e){
                Log.e("Capture",e.getMessage());
            }
            return null;
        }
    }

    // AsyncTask to run repeatGettingPath() concurrently
    private class RepeatPathTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {

            synchronized (lockForPath) {

                        // Capture the image and provide a callback
                        repeatGettingPath(true, pfortask.destination, pfortask.source);
                        //startSpeechRecognition();

                    }

            return null;
        }
    }


    public void openCamera() {
        try {
            cameraProviderListenableFuture = ProcessCameraProvider.getInstance(getApplicationContext());

            cameraProviderListenableFuture.addListener(
                    () -> {
                        try {
                            ProcessCameraProvider processCameraProvider = cameraProviderListenableFuture.get();
                            startCameraX(processCameraProvider);
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    , getExecutor());
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    public void requestPermission() {
        try {
            if (checkPermissions() == false) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CODE);
            } else {

                openCamera();
            }
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    public boolean checkPermissions() {
        try {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                //if permission  granted
                return true;
            } else {

                //if permission not granted
                return false;
            }
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        try {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            if (requestCode == PERMISSION_REQUEST_CODE) {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "GRANTED", Toast.LENGTH_SHORT).show();
                    openCamera();
                } else {
                    Toast.makeText(this, "DENIED", Toast.LENGTH_SHORT).show();
                }
                //this method is called when we ask the user for a permission if he allows us or not depending on its choice.
                //basically a result containing method
            }
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }





    private void startCameraX(ProcessCameraProvider processCameraProvider) {
        try {
                processCameraProvider.unbindAll();

                CameraSelector cameraSelector = new CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build();

                Preview p = new Preview.Builder().build();
                p.setSurfaceProvider(binding.previewCamera.getSurfaceProvider());










                imageCapture =
                        new ImageCapture.Builder()
                                .build();

                processCameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, p, imageCapture);
                //c.getCameraControl().setZoomRatio(1.0f);










            //startSpeechRecognition();

            // Create a handler to capture images periodically


            askForNavigation();
            //startCapturingtheImage();
            Handler h=new Handler(getMainLooper());
            Runnable r=new Runnable() {
                @Override
                public void run() {
                    captureImage();
                    startSpeechRecognition();
                    h.postDelayed(this,1000);
                }
            };
            h.postDelayed(r,1000);


            //askForNavigation();
            // Start capturing
        } catch (Exception e) {
            Log.e("Image Camera Open",e.getMessage());
        }
        // ... Rest of the code ...
    }
    public void askForNavigation() throws InterruptedException {
        String text="Do you want to navigate Please say navigate or to detect objects in the current scene please say objects in front";
        textToSpeech.speak(text,TextToSpeech.QUEUE_ADD,null,null);


    }
    public void startCapturingtheImage(){
        try {



                        new CaptureImageTask().execute();

                    //new RepeatPathTask().execute();
                   // handler.postDelayed(this,2000);



        }catch (Exception e){
            Log.e("Starting Capturing Image",e.getMessage());
        }
    }

    private void updateOverlay(ArrayList<Coordinate> coordinates) {
        if (overlayView != null) {
            overlayView.setCoordinates(coordinates);
        }
    }

    public void capturingImageForObject(){

        imageCapture.takePicture(getExecutor(), new ImageCapture.OnImageCapturedCallback() {
            @Override
            public void onCaptureSuccess(@NonNull ImageProxy image) {
                super.onCaptureSuccess(image);


                ByteBuffer buffer = image.getPlanes()[0].getBuffer();

                byte[] imageData = new byte[buffer.remaining()];
                buffer.get(imageData);


                // Create a RequestBody from the captured image data
                RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), imageData);
                tempheight = image.getHeight();
                tempwidth = image.getWidth();
                image.close();
                // Prepare a MultipartBody.Part using the RequestBody
                MultipartBody.Part imagePart = MultipartBody.Part.createFormData("img", "image.jpg", requestFile);

//                WebApi api = RetrofitClient.getInstance().getMyApi();
//                RequestBody w = api.createPartFromString(image.getWidth() + "");
//                RequestBody h = api.createPartFromString(image.getHeight() + "");
//                RequestBody fl = api.createPartFromString("second floor");
                imageSendingForObject(imagePart);


                // Now you can use the imagePart in your network call or upload process
            }


            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                super.onError(exception);
            }
        });


    }

    public void capturingImageForNavigation(){




            imageCapture.takePicture(getExecutor(), new ImageCapture.OnImageCapturedCallback() {
                @Override
                public void onCaptureSuccess(@NonNull ImageProxy image) {
                    super.onCaptureSuccess(image);


                    ByteBuffer buffer = image.getPlanes()[0].getBuffer();

                    byte[] imageData = new byte[buffer.remaining()];
                    buffer.get(imageData);


                    // Create a RequestBody from the captured image data
                    RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), imageData);
                    tempheight = image.getHeight();
                    tempwidth = image.getWidth();
                    image.close();
                    // Prepare a MultipartBody.Part using the RequestBody
                    MultipartBody.Part imagePart = MultipartBody.Part.createFormData("img", "image.jpg", requestFile);

                    WebApi api = RetrofitClient.getInstance().getMyApi();
                    RequestBody w = api.createPartFromString(image.getWidth() + "");
                    RequestBody h = api.createPartFromString(image.getHeight() + "");
                    RequestBody fl = api.createPartFromString("second floor");
                    collectingImages.add(imagePart);
                    if(collectingImages.size()==4){
                        imageSendingForNavigtion(collectingImages,h,w,fl);

                        collectingImages.clear();
                    }


                    // Now you can use the imagePart in your network call or upload process
                }


                @Override
                public void onError(@NonNull ImageCaptureException exception) {
                    super.onError(exception);
                }
            });


    }

    public void imageSendingForObject(MultipartBody.Part img){

        if (isApiRequestInProgress) {
            //Toast.makeText(this, "Another Api request in Process", Toast.LENGTH_SHORT).show();
            return; // An API request is already in progress, skip capturing this time
        }
        isApiRequestInProgress = true;

        try {
            WebApi api=RetrofitClient.getInstance().getMyApi();
            api.objectFrame(img).enqueue(new Callback<ApiResponeOfFrameObject>() {
                @Override
                public void onResponse(Call<ApiResponeOfFrameObject> call, Response<ApiResponeOfFrameObject> response) {
                    if (response.isSuccessful()) {
                        ApiResponeOfFrameObject s = response.body();
                        which_objects = s.getCoordinates();

                        ArrayList<Coordinate> screenCoordinates = new ArrayList<>();

                        // Calculate the scaling factors for X and Y coordinates
                        float scaleX = (float) overlayView.getWidth() / tempwidth; // Adjust for screen width
                        float scaleY = (float) overlayView.getHeight() / tempheight;
                        for (Coordinate apiCoordinate : which_objects) {
                            // Map API coordinates to screen coordinates
                            int left = (int) (apiCoordinate.getXmin() * scaleX);
                            int top = (int) (apiCoordinate.getYmin() * scaleY);
                            int right = (int) (apiCoordinate.getXmax() * scaleX);
                            int bottom = (int) (apiCoordinate.getYmax() * scaleY);

                            Coordinate coordinate = new Coordinate();
                            coordinate.setXmax(right);
                            coordinate.setXmin(left);
                            coordinate.setYmax(top);
                            coordinate.setYmin(bottom);
                            coordinate.setDistance(apiCoordinate.getDistance());
                            coordinate.setLabel(apiCoordinate.getLabel());
                            coordinate.setSteps(apiCoordinate.getSteps());
                            coordinate.setPosition(apiCoordinate.getPosition());
                            screenCoordinates.add(coordinate);


                        }

                        which_objects = screenCoordinates;
                        updateOverlay(which_objects);
                        isApiRequestInProgress = false;
                    }
                }

                @Override
                public void onFailure(Call<ApiResponeOfFrameObject> call, Throwable t) {
                    isApiRequestInProgress = false;
                }

            });
        } catch (Exception e) {
            Log.e("API RESPONSE OBJECT",e.getMessage());
        }

    }

    public void imageSendingForNavigtion(ArrayList<MultipartBody.Part> images, RequestBody height, RequestBody width, RequestBody floor) {



        if (isApiRequestInProgress) {

            //Toast.makeText(this, "Another Api request in Process", Toast.LENGTH_SHORT).show();
            return; // An API request is already in progress, skip capturing this time
        }
        isApiRequestInProgress = true;


            try {
                Toast.makeText(this, "request "+counter+ "is on its way", Toast.LENGTH_SHORT).show();
                WebApi api = RetrofitClient.getInstance().getMyApi();
                api.frame(images, height, width, floor).enqueue(new Callback<ApiResponseOfFrame>() {
                    @Override
                    public void onResponse(Call<ApiResponseOfFrame> call, Response<ApiResponseOfFrame> response) {
                        if (response.isSuccessful()) {
                            ApiResponseOfFrame s=response.body();
                            which_objects = s.getCoordinates();

                            ArrayList<Coordinate> screenCoordinates = new ArrayList<>();

                            // Calculate the scaling factors for X and Y coordinates
                            float scaleX = (float) overlayView.getWidth() / tempwidth; // Adjust for screen width
                            float scaleY = (float) overlayView.getHeight() / tempheight;
                            for (Coordinate apiCoordinate : which_objects) {
                                // Map API coordinates to screen coordinates
                                int left = (int) (apiCoordinate.getXmin() * scaleX);
                                int top = (int) (apiCoordinate.getYmin() * scaleY);
                                int right = (int) (apiCoordinate.getXmax() * scaleX);
                                int bottom = (int) (apiCoordinate.getYmax() * scaleY);

                                Coordinate coordinate = new Coordinate();
                                coordinate.setXmax(right);
                                coordinate.setXmin(left);
                                coordinate.setYmax(top);
                                coordinate.setYmin(bottom);
                                coordinate.setDistance(apiCoordinate.getDistance());
                                coordinate.setLabel(apiCoordinate.getLabel());
                                coordinate.setSteps(apiCoordinate.getSteps());
                                coordinate.setPosition(apiCoordinate.getPosition());
                                screenCoordinates.add(coordinate);


                            }

                            which_objects = screenCoordinates;
                            updateOverlay(which_objects);
                            isApiRequestInProgress = false;
                            counter=counter+1;

                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponseOfFrame> call, Throwable t) {
                        isApiRequestInProgress = false;
                    }
                });
            } catch (Exception e) {
                Log.e("e", e.getMessage());
            }


    }
    private  void  captureImage() {



        try {
            if(isNavigation){
                capturingImageForNavigation();
                //isApiRequestInProgress=false;
            }
            else if(isObjectDetection){
                capturingImageForObject();
                //isApiRequestInProgress=false;
            }


//            imageCapture.takePicture(getExecutor(), new ImageCapture.OnImageCapturedCallback() {
//                @Override
//                public void onCaptureSuccess(@NonNull ImageProxy image) {
//                    super.onCaptureSuccess(image);
//
//
//                    ByteBuffer buffer = image.getPlanes()[0].getBuffer();
//
//                    byte[] imageData = new byte[buffer.remaining()];
//                    buffer.get(imageData);
//
//
//                    // Create a RequestBody from the captured image data
//                    RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), imageData);
//                    tempheight = image.getHeight();
//                    tempwidth = image.getWidth();
//                    image.close();
//                    // Prepare a MultipartBody.Part using the RequestBody
//                    MultipartBody.Part imagePart = MultipartBody.Part.createFormData("img", "image.jpg", requestFile);
//
//                    WebApi api = RetrofitClient.getInstance().getMyApi();
//                    RequestBody w = api.createPartFromString(image.getWidth() + "");
//                    RequestBody h = api.createPartFromString(image.getHeight() + "");
//                    RequestBody fl = api.createPartFromString("second floor");
//
//
//                    if (isNavigation) {
//
//
//                        try {
//
//
////                            api.frame(imagePart, h, w, fl).enqueue(new Callback<ApiResponseOfFrame>() {
////
////                                @Override
////                                public void onResponse(Call<ApiResponseOfFrame> call, Response<ApiResponseOfFrame> response) {
////
////                                    if (response.isSuccessful()) {
////                                        ApiResponseOfFrame s = response.body();
////
////                                        getCurrentValuesForNavigation = s;
////                                        String p = s.getPlace();
////                                        String t = s.getTurn();
////                                        String dir = s.getDoorDirection();
////                                        which_objects = s.getCoordinates();
////
////                                        ArrayList<Coordinate> screenCoordinates = new ArrayList<>();
////
////                                        // Calculate the scaling factors for X and Y coordinates
////                                        float scaleX = (float) overlayView.getWidth() / tempwidth; // Adjust for screen width
////                                        float scaleY = (float) overlayView.getHeight() / tempheight;
////                                        for (Coordinate apiCoordinate : which_objects) {
////                                            // Map API coordinates to screen coordinates
////                                            int left = (int) (apiCoordinate.getXmin() * scaleX);
////                                            int top = (int) (apiCoordinate.getYmin() * scaleY);
////                                            int right = (int) (apiCoordinate.getXmax() * scaleX);
////                                            int bottom = (int) (apiCoordinate.getYmax() * scaleY);
////
////                                            Coordinate coordinate = new Coordinate();
////                                            coordinate.setXmax(right);
////                                            coordinate.setXmin(left);
////                                            coordinate.setYmax(top);
////                                            coordinate.setYmin(bottom);
////                                            coordinate.setDistance(apiCoordinate.getDistance());
////                                            coordinate.setLabel(apiCoordinate.getLabel());
////                                            coordinate.setSteps(apiCoordinate.getSteps());
////                                            coordinate.setPosition(apiCoordinate.getPosition());
////                                            screenCoordinates.add(coordinate);
////
////
////                                        }
////
////                                        which_objects = screenCoordinates;
////                                        updateOverlay(which_objects);
////                                        isApiRequestInProgress = false;
////                                        //Toast.makeText(MainActivity.this, "response succesful", Toast.LENGTH_SHORT).show();
////
////                                    }
////
////                                }
////
////                                @Override
////                                public void onFailure(Call<ApiResponseOfFrame> call, Throwable t) {
////                                    Toast.makeText(MainActivity.this, t.toString(), Toast.LENGTH_SHORT).show();
////                                    isApiRequestInProgress = false;
////                                }
////                            });
//                        } catch (Exception e) {
//                            Log.e("API Response", e.getMessage());
//                        }
//                    } else if (isObjectDetection) {
//                        try {
////                            api.objectFrame(imagePart).enqueue(new Callback<ApiResponeOfFrameObject>() {
////                                @Override
////                                public void onResponse(Call<ApiResponeOfFrameObject> call, Response<ApiResponeOfFrameObject> response) {
////                                    if (response.isSuccessful()) {
////                                        ApiResponeOfFrameObject s = response.body();
////                                        which_objects = s.getCoordinates();
////
////                                        ArrayList<Coordinate> screenCoordinates = new ArrayList<>();
////
////                                        // Calculate the scaling factors for X and Y coordinates
////                                        float scaleX = (float) overlayView.getWidth() / tempwidth; // Adjust for screen width
////                                        float scaleY = (float) overlayView.getHeight() / tempheight;
////                                        for (Coordinate apiCoordinate : which_objects) {
////                                            // Map API coordinates to screen coordinates
////                                            int left = (int) (apiCoordinate.getXmin() * scaleX);
////                                            int top = (int) (apiCoordinate.getYmin() * scaleY);
////                                            int right = (int) (apiCoordinate.getXmax() * scaleX);
////                                            int bottom = (int) (apiCoordinate.getYmax() * scaleY);
////
////                                            Coordinate coordinate = new Coordinate();
////                                            coordinate.setXmax(right);
////                                            coordinate.setXmin(left);
////                                            coordinate.setYmax(top);
////                                            coordinate.setYmin(bottom);
////                                            coordinate.setDistance(apiCoordinate.getDistance());
////                                            coordinate.setLabel(apiCoordinate.getLabel());
////                                            coordinate.setSteps(apiCoordinate.getSteps());
////                                            coordinate.setPosition(apiCoordinate.getPosition());
////                                            screenCoordinates.add(coordinate);
////
////
////                                        }
////
////                                        which_objects = screenCoordinates;
////                                        updateOverlay(which_objects);
////                                        isApiRequestInProgress = false;
////                                    }
////                                }
////
////                                @Override
////                                public void onFailure(Call<ApiResponeOfFrameObject> call, Throwable t) {
////                                    isApiRequestInProgress = false;
////                                }
////
////                            });
//                        } catch (Exception e) {
//                            Log.e("API RESPONSE OBJECT", e.getMessage());
//                        }
//                    }
//                    // Now you can use the imagePart in your network call or upload process
//                }
//
//
//                @Override
//                public void onError(@NonNull ImageCaptureException exception) {
//                    super.onError(exception);
//                }
//            });
        } catch (Exception e) {
            Log.e("exception has occured", e.getMessage());
        }

    }


//    private void captureImageforObject() {
//        if (isApiRequestInProgress) {
//            return; // An API request is already in progress, skip capturing this time
//        }
//        isApiRequestInProgress = true;
//        imageCapture.takePicture(getExecutor(), new ImageCapture.OnImageCapturedCallback() {
//            @Override
//            public void onCaptureSuccess(@NonNull ImageProxy image) {
//                super.onCaptureSuccess(image);
//
//                ByteBuffer buffer = image.getPlanes()[0].getBuffer();
//                byte[] imageData = new byte[buffer.remaining()];
//                buffer.get(imageData);
//                image.close();
//                // Create a RequestBody from the captured image data
//                RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), imageData);
//
//                // Prepare a MultipartBody.Part using the RequestBody
//                MultipartBody.Part imagePart = MultipartBody.Part.createFormData("img", "image.jpg", requestFile);
//
//                WebApi api = RetrofitClient.getInstance().getMyApi();
////                api.frame(imagePart).enqueue(new Callback<ApiResponseOfFrame>() {
//                    @Override
//                    public void onResponse(Call<ApiResponseOfFrame> call, Response<ApiResponseOfFrame> response) {
//                        if (response.isSuccessful()) {
//                            ApiResponseOfFrame s = response.body();
//                            String p = s.getPlace();
//                            String t = s.getTurn();
//                            ArrayList<Coordinate> value=s.getCoordinates();
//                            for(Coordinate c:value){
//                                String label=c.getLabel();
//                                Toast.makeText(getApplicationContext(), label, Toast.LENGTH_SHORT).show();
//                            }
////                           //Log.d("123", String.valueOf(value.get(0)));
//
//
//                           // String textToRead = "Place is " + p + ". Turn is " + t;
//                          //textToSpeech.speak(textToRead, TextToSpeech.QUEUE_FLUSH, null, null);
//                            isApiRequestInProgress = false;
//
//
//                            //Toast.makeText(MainActivity.this, "response succesful", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<ApiResponseOfFrame> call, Throwable t) {
//                        Toast.makeText(MainActivity.this, t.toString(), Toast.LENGTH_SHORT).show();
//                        isApiRequestInProgress = false;
//                    }
//                });
//
//                // Now you can use the imagePart in your network call or upload process
//            }
//
//
//            @Override
//            public void onError(@NonNull ImageCaptureException exception) {
//                super.onError(exception);
//            }
//        });
//    }

//    public void JSONParserNavigation() {
//
//        sh = new HttpHandler();
//        String url = "http://192.168.40.6:5000/api/navigation/recognize_place";
//        String jsonStr = sh.makeRequest(url, "POST");
//        Log.e(TAG, "Response from url: " + jsonStr);
//        if (jsonStr != null) {
//            try {
//                JSONObject jsonObj = new JSONObject(jsonStr);
//
//                // Getting JSON Array node
//                String place = jsonObj.getString("place");
//                String turn = jsonObj.getString("turn");
//                JSONArray coordinates = jsonObj.getJSONArray("coordinates");
//
//                // looping through All Contacts
//                for (int i = 0; i < coordinates.length(); i++) {
//                    JSONObject c = coordinates.getJSONObject(i);
//                    String confidence = c.getString("confidence");
//                    String distance = c.getString("distance");
//                    String label = c.getString("label");
//                    String xmax = c.getString("xmax");
//                    String xmin = c.getString("xmin");
//                    String ymax = c.getString("ymax");
//                    String ymin = c.getString("ymin");
//
//                    Toast.makeText(this, confidence + distance + label + xmax + xmin + ymax + ymin, Toast.LENGTH_SHORT).show();
//
//
//                }
//            } catch (final JSONException e) {
//                Log.e(TAG, "Json parsing error: " + e.getMessage());
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(getApplicationContext(),
//                                "Json parsing error: " + e.getMessage(),
//                                Toast.LENGTH_LONG).show();
//                    }
//                });
//
//            }
//
//        } else {
//            Log.e(TAG, "Couldn't get json from server.");
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    Toast.makeText(getApplicationContext(),
//                            "Couldn't get json from server. Check LogCat for possible errors!",
//                            Toast.LENGTH_LONG).show();
//                }
//            });
//        }
//
//    }


    public void loadToolbar() {
        try {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            nav = (NavigationView) findViewById(R.id.navmenu);
            drawerLayout = (DrawerLayout) findViewById(R.id.drawer);


            toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
            drawerLayout.addDrawerListener(toggle);
            toggle.syncState();


            nav.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    switch (menuItem.getItemId()) {
                        case R.id.menu_add_home:
                            Toast.makeText(getApplicationContext(), "Home Panel is Open", Toast.LENGTH_LONG).show();
                            drawerLayout.closeDrawer(GravityCompat.START);
                            break;

                        case R.id.menu_add_place:
                            getPlacesList();

                            break;

                        case R.id.menu_add_person:
                            getPersonsList();

                            break;
                        case R.id.menu_link_places:
                            Intent j = new Intent(getApplicationContext(), LinkPlacesActivity.class);
                            startActivity(j);
                            break;
                        case R.id.menu_exit:
                            callAlert();
                    }

                    return true;
                }
            });
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    private Executor getExecutor() {
        return ContextCompat.getMainExecutor(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (result != null && result.size() > 0) {
                String recognizedText = result.get(0);
                Toast.makeText(this, recognizedText, Toast.LENGTH_SHORT).show();

            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        which_objects = new ArrayList<>();

      //  preferences=getSharedPreferences("pref",MODE_PRIVATE);

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        overlayView = findViewById(R.id.OverlayView);
        textToSpeech = new TextToSpeech(this, status -> {
            if (status != TextToSpeech.ERROR) {
                Toast.makeText(this, "Text to Speech Enabled Successfully", Toast.LENGTH_SHORT).show();
            }
        });
        textToSpeech1 = new TextToSpeech(this, status -> {
            if (status != TextToSpeech.ERROR) {
                //Toast.makeText(this, "Text to Speech Enabled Successfully", Toast.LENGTH_SHORT).show();
            }
        });

        View v = findViewById(R.id.previewCamera);
        v.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                //String s="Where do you want to go";
                //goingfornavigation=true;
                //textToSpeech.speak(s,TextToSpeech.QUEUE_FLUSH,null,null);

                return true;
            }
        });

        loadToolbar();
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, PackageManager.PERMISSION_GRANTED);
        readyForSpeechRecognition();

        try {
            binding.btnPersonDetection.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    binding.btnPersonDetection.setBackgroundColor(getColor(R.color.blue_p));
                    binding.btnObjectDetection.setBackground(getDrawable(R.drawable.circle));
                    binding.btnNavigation.setBackground(getDrawable(R.drawable.circle));
                    isPersonDetection = true;
                    isNavigation = false;
                    isObjectDetection = false;
                    requestPermission();

                }
            });
            binding.btnObjectDetection.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    binding.btnPersonDetection.setBackground(getDrawable(R.drawable.circle));
                    binding.btnNavigation.setBackground(getDrawable(R.drawable.circle));
                    binding.btnObjectDetection.setBackgroundColor(getColor(R.color.blue_p));
                    isPersonDetection = false;
                    isNavigation = false;
                    isObjectDetection = true;
                    requestPermission();
                }
            });
            binding.btnNavigation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    binding.btnPersonDetection.setBackground(getDrawable(R.drawable.circle));
                    binding.btnNavigation.setBackgroundColor(getColor(R.color.blue_p));
                    binding.btnObjectDetection.setBackground(getDrawable(R.drawable.circle));
                    isPersonDetection = false;
                    isNavigation = true;
                    isObjectDetection = false;
                    requestPermission();
                }
            });
            binding.mic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    startSpeechRecognition();
                }
            });
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }


    }


    private void switchBetweenModules(Boolean object, Boolean person, Boolean navigation) {
        isNavigation = navigation;
        isObjectDetection = object;
        isPersonDetection = person;
        if (isPersonDetection) {
            binding.btnPersonDetection.setBackgroundColor(getColor(R.color.blue_p));
            binding.btnNavigation.setBackground(getDrawable(R.drawable.circle));
            binding.btnObjectDetection.setBackground(getDrawable(R.drawable.circle));
        } else if (isNavigation) {
            binding.btnPersonDetection.setBackground(getDrawable(R.drawable.circle));
            binding.btnNavigation.setBackgroundColor(getColor(R.color.blue_p));
            binding.btnObjectDetection.setBackground(getDrawable(R.drawable.circle));
        } else if (isObjectDetection) {
            binding.btnPersonDetection.setBackground(getDrawable(R.drawable.circle));
            binding.btnObjectDetection.setBackgroundColor(getColor(R.color.blue_p));
            binding.btnNavigation.setBackground(getDrawable(R.drawable.circle));
        }

        requestPermission();
//        Handler handler = new Handler(Looper.getMainLooper());
//
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                startSpeechRecognition();
//            }
//        }, 2000);


    }

    private void startSpeechRecognition() {
        binding.mic.setBackgroundDrawable(getDrawable(R.drawable.listening_mic));
        speechRecognizer.startListening(intentRecognizer);

    }


        // Create and start a thread for captureImage()

        private volatile boolean isRunning = true;
        private void stopRepeating() {
        isRunning = false;
        }

    private void runMethodsConcurrently() {
            int capture=0;
            int path=0;
        try {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (isRunning) {
                        try {
                            // Execute the tasks
                            Task t=new Task() {
                                @Override
                                public void run() {
                                    try {
                                        new CaptureImageTask().execute().get();
                                    } catch (ExecutionException e) {
                                        throw new RuntimeException(e);
                                    } catch (InterruptedException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                            };


                                t.run();
                                t.wait(4000);

                            // Waits for completion
                            //Thread.currentThread().join(2000);
                            //Thread.sleep(2000);

                                new RepeatPathTask().execute().get();

                                 // Waits for completion

                            // Sleep for 2000 milliseconds
                            //Thread.currentThread().join(2000);
                        } catch (Exception e) {
                            // Handle any exception
                            Log.e("Thread Exception", e.getMessage());
                        }
                    }
                }
            });

            // Start the thread
            thread.start();
        }catch (Exception e){
            Log.e("Thread for runMethods Concurrently",e.getMessage());
        }
    }
            //source = azhar dest= umar








    public void repeatGettingPath(boolean b,String dest,String source){
        try {


                            while(true) {

                                //captureImage();


                                    WebApi api = RetrofitClient.getInstance().getMyApi();

                                    String t = getCurrentValuesForNavigation.getPlace().replace('_', ' '); //azhar corridor

                                    PathGetter p = new PathGetter();
                                    p.source = t;
                                    p.destination = dest; //umar faculty
                                    textToSpeech.stop();
                                    speechRecognizer.stopListening();
                                    Toast.makeText(this, "source is"+t+" destination is"+dest, Toast.LENGTH_SHORT).show();

                                    //ArrayList<Coordinate> getCoordinate=getCurrentValuesForNavigation.getCoordinates();

                                    if (isApiRequestForPathinProgress) {
                                        Toast.makeText(this, "got returnedd by Api path request in process", Toast.LENGTH_SHORT).show();
                                        return;
                                    } else if (source.equalsIgnoreCase(dest)) {
                                        Toast.makeText(this, "destination reached", Toast.LENGTH_SHORT).show();
                                        break;

                                    } else if (!getCurrentValuesForNavigation.getPlace().replace('_', ' ').equals(source)) {
                                        //Toast.makeText(this, getCurrentValuesForNavigation.getPlace().replace('_', ' ') + " source is " + source, Toast.LENGTH_SHORT).show();

                                            //  Toast.makeText(this, "Source has been changed", Toast.LENGTH_SHORT).show();
                                            isApiRequestForPathinProgress = true;


                                            api.path(p).enqueue(new Callback<ArrayList<ArrayList<String>>>() {
                                                @Override
                                                public void onResponse(Call<ArrayList<ArrayList<String>>> call, Response<ArrayList<ArrayList<String>>> response) {
                                                    if (response.isSuccessful()) {
                                                        previousPlace = p.source; //previous = azhar

                                                        ArrayList<ArrayList<String>> paths = response.body();
                                                        if (paths != null) {
                                                            for (ArrayList<String> path : paths) {
                                                                String placeName = path.get(0);
                                                                String floor = path.get(1);
                                                                String direction = path.get(2);
                                                                int steps = Integer.parseInt(path.get(3));

                                                                // Construct a Path object
                                                                Path pathObject = new Path();
                                                                pathObject.setPlaceName(placeName);
                                                                pathObject.setFloorName(floor);
                                                                pathObject.setDirectionName(direction);
                                                                pathObject.setSteps(steps);

                                                                newPath.add(pathObject);


                                                                // Now you have a Path object, you can use it as needed
                                                                // For example, display it, use TextToSpeech, etc.
                                                            }
                                                            String t = "Next place is " + newPath.get(0).getPlaceName() + " and direction is" + newPath.get(0).getDirectionName() + " and the steps are" + newPath.get(0).getSteps();
                                                            textToSpeech.speak(t, TextToSpeech.QUEUE_ADD, null, null);


                                                        } else {
                                                            Log.e("No path available", " No path");
                                                        }
                                                    } else {
                                                        Log.e("Response Unsuccessful", " response was not good ");
                                                    }
                                                }

                                                @Override
                                                public void onFailure(Call<ArrayList<ArrayList<String>>> call, Throwable t) {
                                                    Log.e("Api Ki call ma failure", t.getMessage());
                                                }
                                            });
                                            // firstTimeGettingPath = true;
                                            isApiRequestForPathinProgress = false;


                                            //source = p.source;
                                            //azhar

                                        } else {
                                            //Toast.makeText(this, "Source has not changed", Toast.LENGTH_LONG).show();
                                            isApiRequestForPathinProgress = false;

                                        }
                                    }


//                                if (source.equalsIgnoreCase(dest)) {
//                                    String too = "You have reached your destination";
//                                    textToSpeech.speak(too, TextToSpeech.QUEUE_ADD, null, null);
//                                    break;
//                                }
                                }catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }


    }


    private void readyForSpeechRecognition() {
        intentRecognizer = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intentRecognizer.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        try {
            speechRecognizer.setRecognitionListener(new RecognitionListener() {

                @Override
                public void onReadyForSpeech(Bundle bundle) {

                }

                @Override
                public void onBeginningOfSpeech() {

                }

                @Override
                public void onRmsChanged(float v) {

                }

                @Override
                public void onBufferReceived(byte[] bytes) {

                }

                @Override
                public void onEndOfSpeech() {
                    startSpeechRecognition();
                }

                @Override
                public void onError(int i) {

                }

                @Override
                public void onResults(Bundle bundle) {
                    try {
                        ArrayList<String> s = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                        String temp = "";
                        if (s != null) {
                            temp = s.get(0);
                            if (temp.equalsIgnoreCase("Object Detection")) {
                                Toast.makeText(MainActivity.this, temp, Toast.LENGTH_SHORT).show();
                                switchBetweenModules(true, false, false);

                                String textToRead = "object Detection Mode Enabled";
                                textToSpeech.speak(textToRead, TextToSpeech.QUEUE_FLUSH, null, null);


                            } else if (temp.equalsIgnoreCase("Navigate")) {

                                if (getCurrentValuesForNavigation.getPlace() != null) {
                                    isApiRequestInProgress=true;
                                   // speechRecognizer.stopListening();
                                    String textToRead = getCurrentValuesForNavigation.getPlace() + "has been selected as source";
                                    textToSpeech.speak(textToRead, TextToSpeech.QUEUE_FLUSH, null, null);
                                    goingfornavigation = true;
                                    String t = "Please speak for the destination";
                                    textToSpeech.speak(t, TextToSpeech.QUEUE_ADD, null, null);

                                    startSpeechRecognition();
                                    isApiRequestInProgress = true;
                                }

                            } else if (temp.equalsIgnoreCase("Person Recognition")) {
                                Toast.makeText(MainActivity.this, temp, Toast.LENGTH_SHORT).show();
                                switchBetweenModules(false, true, false);
                                String textToRead = "Person Recognition Mode Enabled";
                                textToSpeech.speak(textToRead, TextToSpeech.QUEUE_FLUSH, null, null);
                            } else if (temp.equalsIgnoreCase("Navigation Module")) {
                                Toast.makeText(MainActivity.this, temp, Toast.LENGTH_SHORT).show();
                                switchBetweenModules(false, false, true);
                                String textToRead = "Navigation Mode Enabled";
                                textToSpeech.speak(textToRead, TextToSpeech.QUEUE_FLUSH, null, null);
                            } else if (temp.equalsIgnoreCase("Objects in front")) {


                                Toast.makeText(MainActivity.this, temp, Toast.LENGTH_SHORT).show();
                                if (which_objects != null) {
                                    for (Coordinate c : which_objects) {


                                        String textToRead = "" + c.getLabel();
                                        textToSpeech.speak(textToRead, TextToSpeech.QUEUE_ADD, null, null);


                                    }
                                } else {
                                    Toast.makeText(MainActivity.this, "Object list has null values", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                try {
                                    if (goingfornavigation) {

                                        //repeatGettingPath(boolean b);

                                        WebApi api = RetrofitClient.getInstance().getMyApi();


                                        String t = getCurrentValuesForNavigation.getPlace().replace('_', ' ');

                                        PathGetter p = new PathGetter();
                                        p.source = t;
                                        p.destination = temp;
                                        synchronized (lockForPath){
                                            pfortask=p;
                                        }


                                        textToSpeech.stop();
                                        speechRecognizer.stopListening();

                                        //ArrayList<Coordinate> getCoordinate=getCurrentValuesForNavigation.getCoordinates();

                                        Toast.makeText(MainActivity.this, t, Toast.LENGTH_LONG).show();
                                        String forspeak=temp+"has been selected as your destination";
                                        textToSpeech.speak(forspeak,TextToSpeech.QUEUE_ADD,null,null);
                                        synchronized (textToSpeech) {
                                            if (textToSpeech.isSpeaking()) {
                                                try {
                                                    textToSpeech.wait(3000);  // wait for 3 seconds or until notified
                                                } catch (InterruptedException e) {
                                                    // Handle InterruptedException
                                                    e.printStackTrace();
                                                }
                                            }
                                        }



                                        api.path(p).enqueue(new Callback<ArrayList<ArrayList<String>>>() {
                                            @Override
                                            public void onResponse(Call<ArrayList<ArrayList<String>>> call, Response<ArrayList<ArrayList<String>>> response) {
                                                if (response.isSuccessful()) {
                                                    previousPlace = p.source;
                                                    ArrayList<ArrayList<String>> paths = response.body();
                                                    if (paths != null) {
                                                        for (ArrayList<String> path : paths) {
                                                            String placeName = path.get(0);
                                                            String floor = path.get(1);
                                                            String direction = path.get(2);
                                                            int steps = Integer.parseInt(path.get(3));

                                                            // Construct a Path object
                                                            Path pathObject = new Path();
                                                            pathObject.setPlaceName(placeName);
                                                            pathObject.setFloorName(floor);
                                                            pathObject.setDirectionName(direction);
                                                            pathObject.setSteps(steps);

                                                            newPath.add(pathObject);


                                                            // Now you have a Path object, you can use it as needed
                                                            // For example, display it, use TextToSpeech, etc.
                                                        }
                                                       // Toast.makeText(MainActivity.this, "new Path wala"+newPath.get(0).getPlaceName().replace('_',' '), Toast.LENGTH_LONG).show();
                                                        String t = " Next place is " + newPath.get(0).getPlaceName().replace('_',' ') + " and direction is" + newPath.get(0).getDirectionName() + " and the steps are" + newPath.get(0).getSteps();
                                                        textToSpeech1.speak(t, TextToSpeech.QUEUE_ADD, null, null);


                                                            synchronized (textToSpeech1) {
                                                                if (textToSpeech1.isSpeaking()) {
                                                                    try {
                                                                        textToSpeech1.wait(3000);  // wait for 3 seconds or until notified
                                                                        repeatGettingPath(true,p.destination,p.source);
                                                                    } catch (InterruptedException e) {
                                                                        // Handle InterruptedException
                                                                        e.printStackTrace();
                                                                    }
                                                                }
                                                            }
                                                            isApiRequestForPathinProgress=false;



                                                    } else {
                                                        Toast.makeText(MainActivity.this, "No paths available.", Toast.LENGTH_SHORT).show();
                                                    }
                                                } else {
                                                    Toast.makeText(MainActivity.this, "Response unsuccessful", Toast.LENGTH_LONG).show();
                                                    isApiRequestForPathinProgress=false;
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<ArrayList<ArrayList<String>>> call, Throwable t) {
                                                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                                                isApiRequestForPathinProgress=false;
                                            }
                                        });

                                        // firstTimeGettingPath = true;
                                        //isApiRequestInProgress = false;



//                                        Handler h=new Handler(getMainLooper());
//                                        Runnable r=new Runnable() {
//                                            @Override
//                                            public void run() {
//                                                if(p.source.equalsIgnoreCase(p.destination)) {
//                                                    h.removeCallbacksAndMessages(null);
//
//                                                }else{
//                                                    Toast.makeText(MainActivity.this, "Got into repeat method", Toast.LENGTH_SHORT).show();
//                                                    repeatGettingPath(true,p.destination,p.source);
//                                                    h.postDelayed(this,500);
//                                                }
//
//                                            }
//                                        };
//                                        h.postDelayed(r,500);








                                    } else {
                                        //Toast.makeText(MainActivity.this, "" + temp, Toast.LENGTH_SHORT).show();
                                    }


                                }catch (Exception e){
                                    Log.e("Speech recognition",e.getMessage());
                                }
                            }
                        }
                        startSpeechRecognition();
                    } catch (Exception e) {
                        Log.e("problem  in speech recognition on complete results",e.getMessage());
                    }
                }

                @Override
                public void onPartialResults(Bundle bundle) {
                    startSpeechRecognition();

                }

                @Override
                public void onEvent(int i, Bundle bundle) {

                }
            });
        } catch (Exception e) {
            Log.e("problem overall in speech recognition",e.getMessage());
        }

    }


    private void callAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Exit Confirmation!!");
        builder.setMessage("Do you really want to exit");


        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        //this statement is used for the alert to be clicked on the given buttons only.
        builder.setCancelable(false);
        AlertDialog d = builder.create();
        d.show();
    }


    public void getPlacesList(){
        ProgressDialogFragment progressDialog = new ProgressDialogFragment();
        progressDialog.show(getSupportFragmentManager(), "progress_dialog");

        // Make the API request
        WebApi api = RetrofitClient.getInstance().getMyApi();
        api.getAllplaces().enqueue(new Callback<ArrayList<Place>>() {
            @Override
            public void onResponse(Call<ArrayList<Place>> call, Response<ArrayList<Place>> response) {
                if (response.isSuccessful()) {
                    ArrayList<Place> arr1 = response.body();



                    // Data received, so dismiss the progress bar
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();

                            Intent i = new Intent(getApplicationContext(), PlacesActivity.class);
                            DataStorage dataStorage = DataStorage.getInstance();
                            dataStorage.setPlacesArrayList(arr1);
                            startActivity(i);
                        }
                    }, 1000);

                }
            }
            @Override
            public void onFailure(Call<ArrayList<Place>> call, Throwable t) {
                // Handle API request failure
                progressDialog.dismiss();
                //Toast.makeText(, "API Request Failed", Toast.LENGTH_SHORT).show();
            }
        });

    }


    public void getPersonsList(){
        ProgressDialogFragment progressDialog = new ProgressDialogFragment();
        progressDialog.show(getSupportFragmentManager(), "progress_dialog");

        // Make the API request
        WebApi api = RetrofitClient.getInstance().getMyApi();
        api.getAllPersons().enqueue(new Callback<ArrayList<Person>>() {
            @Override
            public void onResponse(Call<ArrayList<Person>> call, Response<ArrayList<Person>> response) {
                if (response.isSuccessful()) {
                    ArrayList<Person> arr1 = response.body();
                    p.arr=arr1;


                    // Data received, so dismiss the progress bar
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();

                            Intent i = new Intent(getApplicationContext(), PersonActivity.class);
                            DataStorage dataStorage = DataStorage.getInstance();
                            dataStorage.setPersonArrayList(p.arr);
                            startActivity(i);
                        }
                    }, 1000);

                }
            }
            @Override
            public void onFailure(Call<ArrayList<Person>> call, Throwable t) {
                // Handle API request failure
                progressDialog.dismiss();
                //Toast.makeText(, "API Request Failed", Toast.LENGTH_SHORT).show();
            }
        });

    }


}