package com.example.virtualeyeforblinds;
import org.apache.commons.codec.language.DoubleMetaphone;
import static android.content.ContentValues.TAG;
import static com.example.virtualeyeforblinds.api.WebApi.BASE_URL;


import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.LifecycleOwner;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.util.Rational;
import android.util.Size;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.widget.Toast;

import com.example.virtualeyeforblinds.api.RetrofitClient;
import com.example.virtualeyeforblinds.api.WebApi;
//import com.example.virtualeyeforblinds.databinding.ActivityMainBinding;
import com.example.virtualeyeforblinds.apiresponses.ApiResponeOfFrameObject;
import com.example.virtualeyeforblinds.apiresponses.ApiResponseOfFrame;
import com.example.virtualeyeforblinds.apiresponses.ApiResponseOfPerson;
import com.example.virtualeyeforblinds.databinding.ActivityMainBinding;
//import com.example.virtualeyeforblinds.extraClasses.Soundex;
import com.example.virtualeyeforblinds.extraClasses.NetworkUtils;
import com.example.virtualeyeforblinds.models.Coordinate;
import com.example.virtualeyeforblinds.globalClass.DataStorage;
import com.example.virtualeyeforblinds.extraClasses.OverlayView;
import com.example.virtualeyeforblinds.models.Path;
import com.example.virtualeyeforblinds.models.PathGetter;
import com.example.virtualeyeforblinds.models.Types;
import com.example.virtualeyeforblinds.models.floors;
import com.example.virtualeyeforblinds.progessbar.ProgressDialogFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;


import kotlinx.coroutines.scheduling.Task;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    public Dictionary<String,String> objPosition=new Hashtable<>();


    View viewForSnacBar;
    long lastTouchTime = 0;

    final int THRESHOLD_SECONDS = 2;


    boolean isLongPressing = false;
    //public boolean hasIdentidfiedPerson=false;

    public boolean comingFromdoubletapPerson=false;

    public boolean comingFromLongPressPerson=false;

    public Integer counterForSpeakingSource=0;
    public String userSelectedDestination="";

    public ArrayList<String> doubleMetaphonesArrayList=new ArrayList<String>();

    public Thread forGettingSourceRepeatedly=new Thread();

    private volatile boolean isTTSSpeakingForPerson=false;

    private volatile boolean isTTSSpeakingForNavigation=false;

    private volatile boolean isTTSSpeakingForObject=false;

    public ArrayList<String> personsInFront=new ArrayList<>();

    public Dictionary<String,ArrayList<String>> dictPosition=new Hashtable<>();

    public Dictionary<String,ArrayList<String>> dictColor=new Hashtable<>();

    public volatile boolean isApiRequestInProgressPerson=false;

    final int PERMISSION_REQUEST_CODE = 1001;
    final int CAMERA_RESULT_CODE = 1002;
    public volatile boolean listeningForFloor = false;
    public int clickCount = 0;
    public volatile boolean isApiRequestForPathinProgress = false;
    public volatile String previousPlace = "";
    public volatile ArrayList<Path> newPath = new ArrayList<>();

    //SharedPreferences preferences;
    public volatile boolean flag = false;
    public volatile boolean isObjectDetection = false;
    public volatile boolean isPersonDetection = false;
    public volatile boolean isNavigation = false;
    PersonActivity p = new PersonActivity();
    volatile boolean isApiRequestInProgressObject = false;
    RequestBody h = null;
    RequestBody w = null;
    RequestBody fl = null;
    volatile ArrayList<MultipartBody.Part> collectingImages = new ArrayList<>();
    int tempheight = 3024;

//    public boolean firstTimeGettingPath=false;
    int tempwidth = 4032;
    PathGetter pfortask = new PathGetter();
    WebApi forgettingPath;
    volatile ApiResponseOfFrame getCurrentValuesForNavigation = new ApiResponseOfFrame();
    int SPEECH_REQUEST_CODE = 100;
    ActivityMainBinding binding;
    NavigationView nav;
    ActionBarDrawerToggle toggle;
    DrawerLayout drawerLayout;
    volatile ImageCapture imageCapture;
    int counter = 0;
    private volatile GestureDetector gestureDetector;
    private volatile boolean isCapturing = false;
    private Handler captureHandler = new Handler();
    private volatile Object lockForPath = new Object();
    private volatile Object lockForpicture = new Object();
    private volatile boolean goingfornavigation = false;
    private OverlayView overlayView;
    private volatile ArrayList<Coordinate> which_objects;
    private volatile SpeechRecognizer speechRecognizer;

    private volatile SpeechRecognizer speechRecognizerForPerson;

    private volatile SpeechRecognizer speechRecognizerForObject;
    private Intent intentRecognizer;
    private volatile boolean isApiRequestInProgress = false;
    private ListenableFuture<ProcessCameraProvider> cameraProviderListenableFuture;
    private TextToSpeech textToSpeech;

    private TextToSpeech textToSpeechPerson;

    private TextToSpeech textToSpeechNavigation;

    private TextToSpeech textToSpeechObject;
    private TextToSpeech textToSpeech1;
    private volatile boolean isRunning = true;

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
//            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
//                //if permission  granted
//                return true;
//            } else {
//
//                return false;
//            }
            int permissionState = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
            if (permissionState == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                return true;
            } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                // Permission was denied, but not permanently. Request the permission again.
                return false;
            } else {
                // Permission was denied permanently or granted temporarily ('Only this time').
                // You might need to implement additional logic here to handle this case.
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
            Size targetResolution = new Size(720,480);

            //.setTargetAspectRatio(AspectRatio.RATIO_16_9)
            //.setTargetResolution(targetResolution)
            //.setTargetRotation(Surface.ROTATION_0)
            Preview p = new Preview.Builder().setTargetResolution(targetResolution).build();

            imageCapture =
                    new ImageCapture.Builder().setTargetResolution(targetResolution).setTargetRotation(Surface.ROTATION_90)
                            .build();

            p.setSurfaceProvider(binding.previewCamera.getSurfaceProvider());

            processCameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, p, imageCapture);
            captureImage();

            //askForNavigation();
            // Start capturing
        } catch (Exception e) {
            Log.e("Image Camera Open", e.getMessage());
        }
        // ... Rest of the code ...
    }

    public void askForNavigation() throws InterruptedException {
        String text = "Do you want to navigate Please say navigate or to detect objects in the current scene please say objects in front";
        textToSpeech.speak(text, TextToSpeech.QUEUE_ADD, null, null);


    }



    private void updateOverlay(ArrayList<Coordinate> coordinates, String choice) {
        if (overlayView != null) {
            overlayView.setCoordinates(coordinates,choice);
        }
    }

    public void capturingImageForObject() {

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
//                try {
//                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//                Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
////                bitmap.compress(Bitmap.CompressFormat.JPEG, 60, byteArrayOutputStream);
////                byte[] compressedImageData = byteArrayOutputStream.toByteArray();
//
//
//                    ExifInterface exif = new ExifInterface(new ByteArrayInputStream(imageData));
//                    int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
//
//                    int rotationDegrees = 0;
//                    switch (orientation) {
//                        case ExifInterface.ORIENTATION_ROTATE_90:
//                            rotationDegrees = 90;
//                            break;
//                        case ExifInterface.ORIENTATION_ROTATE_180:
//                            rotationDegrees = 180;
//                            break;
//                        case ExifInterface.ORIENTATION_ROTATE_270:
//                            rotationDegrees = 270;
//                            break;
//                        // Add other cases as needed
//                    }
//
//                    // Rotate the bitmap to adjust orientation
//                    if (rotationDegrees != 0) {
//                        Matrix matrix = new Matrix();
//                        matrix.postRotate(rotationDegrees);
//                        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }



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

    public void capturingImageForNavigation() {


        imageCapture.takePicture(getExecutor(), new ImageCapture.OnImageCapturedCallback() {
            @Override
            public void onCaptureSuccess(@NonNull ImageProxy image) {
                super.onCaptureSuccess(image);

                //tempheight = image.getHeight();
                //tempwidth = image.getWidth();
                ByteBuffer buffer = image.getPlanes()[0].getBuffer();

                byte[] imageData = new byte[buffer.remaining()];
                buffer.get(imageData);
                image.close();
                buffer.clear();

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 60, byteArrayOutputStream);
                byte[] compressedImageData = byteArrayOutputStream.toByteArray();

//                try {
//                    ExifInterface exif = new ExifInterface(new ByteArrayInputStream(compressedImageData));
//                    int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
//
//                    int rotationDegrees = 0;
//                    switch (orientation) {
//                        case ExifInterface.ORIENTATION_ROTATE_90:
//                            rotationDegrees = 90;
//                            break;
//                        case ExifInterface.ORIENTATION_ROTATE_180:
//                            rotationDegrees = 180;
//                            break;
//                        case ExifInterface.ORIENTATION_ROTATE_270:
//                            rotationDegrees = 270;
//                            break;
//                        // Add other cases as needed
//                    }
//
//                    // Rotate the bitmap to adjust orientation
//                    if (rotationDegrees != 0) {
//                        Matrix matrix = new Matrix();
//                        matrix.postRotate(rotationDegrees);
//                        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }


                RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), compressedImageData);
                // Prepare a MultipartBody.Part using the RequestBody
                MultipartBody.Part imagePart = MultipartBody.Part.createFormData("img", "image.jpeg", requestFile);

                collectingImages.add(imagePart);


                // Now you can use the imagePart in your network call or upload process
            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                super.onError(exception);
            }
        });


    }

    public void imageSendingForObject(MultipartBody.Part img) {

        if (isApiRequestInProgressObject) {
            //Toast.makeText(this, "Another Api request in Process", Toast.LENGTH_SHORT).show();
            return; // An API request is already in progress, skip capturing this time
        }
        isApiRequestInProgressObject = true;

        try {
            WebApi api = RetrofitClient.getInstance().getMyApi();
            api.objectFrame(img).enqueue(new Callback<ArrayList<Coordinate>>() {
                @Override
                public void onResponse(Call<ArrayList<Coordinate>> call, Response<ArrayList<Coordinate>> response) {
                    if (response.isSuccessful()) {



                        ArrayList<Coordinate> apiCoordinates = response.body();
                        //which_objects = s.getCoordinates();

                        ArrayList<Coordinate> screenCoordinates = new ArrayList<>();

                        // Calculate the scaling factors for X and Y coordinates
                        float scaleX = (float) overlayView.getWidth() / tempwidth; // Adjust for screen width
                        float scaleY = (float) overlayView.getHeight() / tempheight;
                        for (Coordinate apiCoordinate : apiCoordinates) {
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
                            objPosition.put("where is "+apiCoordinate.getLabel(),apiCoordinate.getPosition());
                            Log.e("obj Position list","Key "+"where is "+apiCoordinate.getLabel()+" value "+apiCoordinate.getPosition());


                        }

                        which_objects = screenCoordinates;
                        updateOverlay(which_objects,"object");
                        isApiRequestInProgressObject = false;
                    }
                }

                @Override
                public void onFailure(Call<ArrayList<Coordinate>> call, Throwable t) {
                    isApiRequestInProgressObject = false;
                }

            });
        } catch (Exception e) {
            Log.e("API RESPONSE OBJECT", e.getMessage());
        }

    }



    public void imageSendingForNavigtion(ArrayList<MultipartBody.Part> images, RequestBody height, RequestBody width, RequestBody floor) {

        //Toast.makeText(this, "In image sending for navigation method", Toast.LENGTH_SHORT).show();


        if (isApiRequestInProgress) {

            Toast.makeText(this, "Another Api request in Process", Toast.LENGTH_SHORT).show();
            return; // An API request is already in progress, skip capturing this time
        }
        isApiRequestInProgress = true;


        try {

            //Toast.makeText(this, images.size() + "", Toast.LENGTH_SHORT).show();
            WebApi api = RetrofitClient.getInstance().getMyApi();
            api.frame(images, height, width, floor).enqueue(new Callback<ApiResponseOfFrame>() {
                @Override
                public void onResponse(Call<ApiResponseOfFrame> call, Response<ApiResponseOfFrame> response) {
                    if (response.isSuccessful()) {
                        ApiResponseOfFrame s = response.body();

                        getCurrentValuesForNavigation=s;

                            if(counterForSpeakingSource==0) {
                                textToSpeech.speak(s.getPlace() + "has been selected as source. Please speak for the destination", TextToSpeech.QUEUE_ADD, null, null);

                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        goingfornavigation = true;

                                        startSpeechRecognition();
                                    }
                                }, 5000);
                            }
                            else{
                                GetPath(userSelectedDestination);
                            }




//                            which_objects = s.getCoordinates();
//
//                            ArrayList<Coordinate> screenCoordinates = new ArrayList<>();
//
//                            // Calculate the scaling factors for X and Y coordinates
//                            float scaleX = (float) overlayView.getWidth() / tempwidth; // Adjust for screen width
//                            float scaleY = (float) overlayView.getHeight() / tempheight;
//                            for (Coordinate apiCoordinate : which_objects) {
//                                // Map API coordinates to screen coordinates
//                                int left = (int) (apiCoordinate.getXmin() * scaleX);
//                                int top = (int) (apiCoordinate.getYmin() * scaleY);
//                                int right = (int) (apiCoordinate.getXmax() * scaleX);
//                                int bottom = (int) (apiCoordinate.getYmax() * scaleY);
//
//                                Coordinate coordinate = new Coordinate();
//                                coordinate.setXmax(right);
//                                coordinate.setXmin(left);
//                                coordinate.setYmax(top);
//                                coordinate.setYmin(bottom);
//                                coordinate.setDistance(apiCoordinate.getDistance());
//                                coordinate.setLabel(apiCoordinate.getLabel());
//                                coordinate.setSteps(apiCoordinate.getSteps());
//                                coordinate.setPosition(apiCoordinate.getPosition());
//                                screenCoordinates.add(coordinate);
//
//
//                            }
//
//                            which_objects = screenCoordinates;
//                            updateOverlay(which_objects);
//                            isApiRequestInProgress = false;
//                            counter=counter+1;
//
//                        }
                        isApiRequestInProgress = false;
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





    public void captureAndSendImageForPerson(){
        if (isApiRequestInProgressPerson) {


            return; // An API request is already in progress, skip capturing this time
        }
        isApiRequestInProgressPerson=true;

        imageCapture.takePicture(getExecutor(), new ImageCapture.OnImageCapturedCallback() {
            @Override
            public void onCaptureSuccess(@NonNull ImageProxy image) {
                super.onCaptureSuccess(image);


                ByteBuffer buffer = image.getPlanes()[0].getBuffer();

                byte[] imageData = new byte[buffer.remaining()];
                buffer.get(imageData);

                Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);

                // Get the orientation information using ExifInterface
//                try {
//                    ExifInterface exif = new ExifInterface(new ByteArrayInputStream(imageData));
//                    int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
//
//                    int rotationDegrees = 0;
//                    switch (orientation) {
//                        case ExifInterface.ORIENTATION_ROTATE_90:
//                            rotationDegrees = 90;
//                            break;
//                        case ExifInterface.ORIENTATION_ROTATE_180:
//                            rotationDegrees = 180;
//                            break;
//                        case ExifInterface.ORIENTATION_ROTATE_270:
//                            rotationDegrees = 270;
//                            break;
//                        // Add other cases as needed
//                    }
//
//                    // Rotate the bitmap to adjust orientation
//                    if (rotationDegrees != 0) {
//                        Matrix matrix = new Matrix();
//                        matrix.postRotate(rotationDegrees);
//                        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }



//                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//                Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
//                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
//                byte[] compressedImageData = byteArrayOutputStream.toByteArray();
//
//                // Create a RequestBody from the captured image data
//                RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), compressedImageData);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
                byte[] adjustedImageData = byteArrayOutputStream.toByteArray();

                // Prepare a RequestBody from the adjusted image data
                RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), adjustedImageData);
                MultipartBody.Part imagePart = MultipartBody.Part.createFormData("img", "image.jpg", requestFile);
                tempheight = image.getHeight();
                tempwidth = image.getWidth();
                image.close();
                // Prepare a MultipartBody.Part using the RequestBody
                //MultipartBody.Part imagePart = MultipartBody.Part.createFormData("img", "image.jpg", requestFile);



                WebApi api=RetrofitClient.getInstance().getMyApi();

//                WebApi api = RetrofitClient.getInstance().getMyApi();
                RequestBody w = api.createPartFromString(tempwidth + "");
                RequestBody h = api.createPartFromString(tempheight + "");
//                RequestBody fl = api.createPartFromString("second floor");
                //imageSendingForObject(imagePart);


                try {
                    api.personFrame(h,w,imagePart).enqueue(new Callback<ArrayList<ApiResponseOfPerson>>() {

                        @Override
                        public void onResponse(Call<ArrayList<ApiResponseOfPerson>> call, Response<ArrayList<ApiResponseOfPerson>> response) {
                            if(response.isSuccessful()){
                                if(personsInFront.size()!=0){
                                    personsInFront.clear();
                                }

                                String v="";
                                ArrayList<ApiResponseOfPerson> persons=response.body();
                                ArrayList<Coordinate> allCoordinates = new ArrayList<>();

                                for (ApiResponseOfPerson person : persons) {

                                    v = person.getPerson();


                                    personsInFront.add(v);

//                                    Soundex s = new Soundex();
//
//                                    s.encodesSingleValue(v + " left");
//                                    s.encodesSingleValue(v + " right");
//                                    s.encodesSingleValue(v + " costume colour");

//                                    DoubleMetaphone doubleMetaphone=new DoubleMetaphone();
//                                    doubleMetaphonesArrayList.add(doubleMetaphone.doubleMetaphone(v+" left"));
//                                    doubleMetaphonesArrayList.add(doubleMetaphone.doubleMetaphone(v+" right"));
//                                    doubleMetaphonesArrayList.add(doubleMetaphone.doubleMetaphone(v+" costume colour"));



                                    Log.d(TAG, "Person saving list name: " + v);

                                    try {
                                        if(person!=null) {
                                            dictPosition.put(v + " left", person.left);
                                            dictPosition.put(v + " right", person.right);
                                            if(person.dress_colors!=null) {
                                                dictColor.put(v + " costume colour", person.dress_colors);
                                            }else{
                                                Log.d(TAG, "person.dress_colors is null for v: " + v);
                                            }

                                            Coordinate coordinate = person.getScreenCoordinates(overlayView.getWidth(), overlayView.getHeight(), tempheight, tempwidth);
                                            allCoordinates.add(coordinate);
                                        }
                                    }catch(Exception e){
                                        e.printStackTrace();
                                    }
                                }


                                which_objects=allCoordinates;
                                updateOverlay(which_objects,"person");

                                if (personsInFront.size() == 0) {
                                    textToSpeech.speak("There is no one in front of you", TextToSpeech.QUEUE_FLUSH, null, null);
                                } else {
                                    String txt = "";
                                    for (int k = 0; k < personsInFront.size(); k++) {
                                        txt += personsInFront.get(k);
                                    }
                                    threadToSpeak(txt);
                                    txt = "";
                                    //textToSpeech.speak("Person in front of you are"+txt,TextToSpeech.QUEUE_FLUSH,null,null);
                                }
                                //overlayView.setCoordinates(allCoordinates);
                                isApiRequestInProgressPerson=false;

                            }
                            else{
                                isApiRequestInProgressPerson=false;
                                Toast.makeText(getApplicationContext(), "response was unsuccessful from API", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ArrayList<ApiResponseOfPerson>> call, Throwable t) {
                            Toast.makeText(getApplicationContext(), "API CAll failded to recognize person", Toast.LENGTH_SHORT).show();
                            Log.e("api call failed of recognizing a person",t.getMessage());
                            isApiRequestInProgressPerson=false;
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }


                // Now you can use the imagePart in your network call or upload process
            }


            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                super.onError(exception);
            }
        });


    }


    public void threadToStartObjectDetection(){


        try {
            Thread t=new Thread(new Runnable() {
                @Override
                public void run() {
                            Handler handler=new Handler(Looper.getMainLooper());
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if(isNavigation || isObjectDetection){
                                        capturingImageForObject();
                                    }

                                }
                            });
                            handler.postDelayed(this, 2000);

                }
            });

            t.start();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("thread for object detection method exception",e.getMessage());
        }
    }



    private void captureImage() {


        try {
            if (isNavigation) {

                if(!isCapturing && !isApiRequestInProgress){
                    threadToStartObjectDetection();
                }


                //askForNavigation();

                //startSpeechRecognition();

                captureHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (isCapturing) {

                            Thread thread = new Thread(new Runnable() {
                                public void run() {
                                    capturingImageForNavigation();
                                }
                            });

                            thread.start();

                            captureHandler.postDelayed(this, 1000);
                        }
                    }
                });

                //isApiRequestInProgress=false;
            } else if (isObjectDetection) {
                threadToStartObjectDetection();

                //capturingImageForObject();
                //isApiRequestInProgress=false;
            }
            else if(isPersonDetection){
               // Toast.makeText(MainActivity.this, "In Person detecton if statement", Toast.LENGTH_SHORT).show();

//                Thread thread = new Thread(new Runnable() {
//                    public void run() {
//
//                                //Toast.makeText(MainActivity.this, "Person detection has been enabled", Toast.LENGTH_SHORT).show();
//
//
//                        captureHandler.post(new Runnable() {
//                            @Override
//                            public void run() {
//
//
//
//
//                                            captureAndSendImageForPerson();
//
//
//                                    captureHandler.postDelayed(this, 1000);
//                                }
//
//                        });
//
//
//
//
//
//                    }
//                });
//                    thread.start();


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
            e.printStackTrace();
            Log.e("exception has occured", e.getMessage());
        }

    }

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
                            Intent i = new Intent(getApplicationContext(), PlacesActivity.class);
                            startActivity(i);

                            break;

                        case R.id.menu_add_person:
                            Intent a = new Intent(getApplicationContext(), PersonActivity.class);
                            startActivity(a);

                            break;
                        case R.id.menu_link_places:
                            Intent j = new Intent(getApplicationContext(), MatrixActivity.class);
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
            e.printStackTrace();
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

    public void textToSpeechWaiting(String choice,String text){

        textToSpeech.speak(text,TextToSpeech.QUEUE_FLUSH,null,null);

        if(choice.equalsIgnoreCase("person")){

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startSpeechRecognitionForPerson();
                }
            }, 1500);

        }else if(choice.equalsIgnoreCase("navigation")){

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startSpeechRecognition();
                }
            }, 2000);

        }else{

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startSpeechRecognitionForObject();
                }
            }, 1500);
        }


    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        which_objects = new ArrayList<>();



        //  preferences=getSharedPreferences("pref",MODE_PRIVATE);
        try {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
            speechRecognizerForPerson = SpeechRecognizer.createSpeechRecognizer(this);
            speechRecognizerForObject = SpeechRecognizer.createSpeechRecognizer(this);
        }catch (Exception e){
            e.printStackTrace();
        }
        overlayView = findViewById(R.id.OverlayView);



        textToSpeech = new TextToSpeech(this, status -> {
            if (status != TextToSpeech.ERROR) {
                Toast.makeText(this, "Text to Speech Enabled Successfully", Toast.LENGTH_SHORT).show();
            }
        });
        textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String s) {
                isTTSSpeakingForNavigation=true;

            }

            @Override
            public void onDone(String s) {
                isTTSSpeakingForNavigation=false;

            }

            @Override
            public void onError(String s) {

            }
        });
        textToSpeechPerson=new TextToSpeech(this,status ->{
            if(status !=TextToSpeech.ERROR){
                Toast.makeText(this, "Text to Speech For Person has enabled successfully", Toast.LENGTH_SHORT).show();
            }
        });
        textToSpeechPerson.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String s) {
                isTTSSpeakingForPerson=true;

            }

            @Override
            public void onDone(String s) {
                isTTSSpeakingForPerson=false;

            }

            @Override
            public void onError(String s) {

            }
        });
        textToSpeech1 = new TextToSpeech(this, status -> {
            if (status != TextToSpeech.ERROR) {
                //Toast.makeText(this, "Text to Speech Enabled Successfully", Toast.LENGTH_SHORT).show();
            }
        });


        View v = findViewById(R.id.previewCamera);



//        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
//            @Override
//            public void onLongPress(MotionEvent e) {
//                try {
//                    if (isPersonDetection) {
//                        comingFromLongPressPerson = true;
//                        threadToSpeak("Listening for commands");
//                        Handler handler = new Handler();
//                        handler.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                startSpeechRecognitionForPerson();
//                            }
//                        }, 1500);
//                    } else if (!isCapturing && isNavigation) {
//                        isCapturing = true;
//                        if (fl == null) {
//                            isCapturing = false;
//                            textToSpeech.speak("please select the floor first by tapping on the screen three times", TextToSpeech.QUEUE_FLUSH, null, null);
//                            return;
//                        } else {
//                            captureImage();
//                        }
//                    }
//                } catch (Exception ex) {
//                    ex.printStackTrace();
//                }
//            }
//
//            @Override
//            public boolean onDoubleTap(MotionEvent e) {
//                // Handle double tap
//                if (isObjectDetection) {
//                    textToSpeech.speak("Listening for commands of objects", TextToSpeech.QUEUE_FLUSH, null, null);
//                    comingFromdoubletapPerson = true;
//                    Handler handler = new Handler();
//                    handler.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            startSpeechRecognitionForObject();
//                        }
//                    }, 2000);
//                } else if (isPersonDetection) {
//                    try {
//                        if (personsInFront.size() == 0) {
//                            textToSpeech.speak("Long Press", TextToSpeech.QUEUE_FLUSH, null, null);
//                        } else {
//                            threadToSpeak("listening for commands of persons");
//                            comingFromdoubletapPerson = true;
//                            Handler handler = new Handler();
//                            handler.postDelayed(new Runnable() {
//                                @Override
//                                public void run() {
//                                    startSpeechRecognitionForPerson();
//                                }
//                            }, 2000);
//                        }
//                    } catch (Exception ex) {
//                        ex.printStackTrace();
//                    }
//                }
//                return super.onDoubleTap(e);
//            }
//
//            @Override
//            public boolean onDoubleTapEvent(MotionEvent e) {
//                // Handle double tap event
//                return super.onDoubleTapEvent(e);
//            }
//
//            @Override
//            public boolean onSingleTapConfirmed(MotionEvent e) {
//                // Handle single tap
//                return super.onSingleTapConfirmed(e);
//            }
//
//            // Implement other necessary callbacks
//        });

        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public void onLongPress(MotionEvent e) {

                try {
                    isLongPressing = true;


                    if(isPersonDetection){
                        try {
                            clickCount=0;
                            comingFromLongPressPerson=true;
                            textToSpeechWaiting("person","Listening for commands");

//                            threadToSpeak("Listening for commands");
//
//                            Handler handler = new Handler();
//                            handler.postDelayed(new Runnable() {
//                                @Override
//                                public void run() {
//                                    startSpeechRecognitionForPerson();
//                                }
//                            }, 1500);
                        }catch (Exception tt){
                            //Log.e("exception in listening of commands",tt.getMessage());
                            tt.printStackTrace();
                        }

                    }


                    else if (!isCapturing && isNavigation) {
                        isCapturing = true;
                        clickCount=0;

                                if (fl == null) {
                                    isCapturing=false;
                                    //textToSpeechWaiting("navigation","please select the floor first by tapping on the screen three times");
                                    textToSpeech.speak("please select the floor first by tapping on the screen three times", TextToSpeech.QUEUE_FLUSH, null, null);
                                    clickCount=0;
                                    return;

//                                    Handler handler = new Handler();
//                                    handler.postDelayed(new Runnable() {
//                                        @Override
//                                        public void run() {
//
//                                        }
//                                    }, 1500);

                                } else {
                                    clickCount=0;

                                    Toast.makeText(getApplicationContext(), "Taking Pics", Toast.LENGTH_SHORT).show();
                                    captureImage();
                                }
                            }



                    // Long press detected, start capturing images


                } catch (Exception ex) {
                    ex.printStackTrace();
                    //Toast.makeText(MainActivity.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });



//        v.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View view) {
//                //String s="Where do you want to go";
//                //goingfornavigation=true;
//                //textToSpeech.speak(s,TextToSpeech.QUEUE_FLUSH,null,null);
//                flag=true;
//                return true;
//            }
//        });


        v.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                try {
                    gestureDetector.onTouchEvent(event);
                    boolean gestureEventHandled = gestureDetector.onTouchEvent(event);

                    //Toast.makeText(MainActivity.this, "on touch has been called", Toast.LENGTH_SHORT).show();
                    switch (event.getAction()) {

                        case MotionEvent.ACTION_DOWN:

                            break;
                        case MotionEvent.ACTION_UP:

                            if (!isLongPressing) {
                                long currentTime = SystemClock.elapsedRealtime() / 1000;

                                if (lastTouchTime == 0 || currentTime - lastTouchTime <= THRESHOLD_SECONDS) {
                                    clickCount++;
                                    lastTouchTime = currentTime;
                                } else {
                                    // Reset count and last touch time if the gap is larger than the threshold
                                    clickCount = 1; // Assuming you want to start counting from 1 for a new series of rapid touches
                                    lastTouchTime = currentTime;
                                }



                            }

                            isLongPressing=false;
                            if(clickCount==2 && isObjectDetection){
                                //textToSpeech.speak("Listening for commands of objects",TextToSpeech.QUEUE_FLUSH,null,null);

                                comingFromdoubletapPerson=true;
                                clickCount=0;
                                textToSpeechWaiting("object","Listening for commands of objects");
//                                Handler handler = new Handler();
//                                handler.postDelayed(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        startSpeechRecognitionForObject();
//                                    }
//                                }, 2000);


                            }
                            if(clickCount==2 && isPersonDetection){

                                        try {

                                            if(personsInFront.size()==0){
                                                clickCount=0;
                                                //threadToSpeak("Please Long Press on the screen And say who is in front");
                                                textToSpeech.speak("Long Press",TextToSpeech.QUEUE_FLUSH,null,null);

                                            }
                                            else {

                                                //threadToSpeak("listening for commands of persons");
                                                clickCount = 0;
                                                textToSpeechWaiting("person","Listening for commands of persons");
                                                comingFromdoubletapPerson=true;
//                                                Handler handler = new Handler();
//                                                handler.postDelayed(new Runnable() {
//                                                    @Override
//                                                    public void run() {
//                                                        startSpeechRecognitionForPerson();
//                                                    }
//                                                }, 2000);

                                            }
                                        }catch (Exception e){
                                            e.printStackTrace();
                                        }


                            }


                            if (clickCount == 3 && !isApiRequestInProgress && isNavigation) {
                                //textToSpeech.speak("Listening for floor name", TextToSpeech.QUEUE_FLUSH, null, null);
                                listeningForFloor=true;
                                clickCount = 0;
                                textToSpeechWaiting("navigation","Listening for floor name");

//                                Handler handler = new Handler();
//                                handler.postDelayed(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        listeningForFloor = true;
//                                        startSpeechRecognition();
//                                    }
//                                }, 2000);

                                //Toast.makeText(MainActivity.this, "User Clicked three times", Toast.LENGTH_SHORT).show();

                            }

                            if (isCapturing && isNavigation) {
                                isCapturing = false;

                                //if()

                                Handler mainHandler = new Handler(Looper.getMainLooper());
                                mainHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        // Perform background task directly within the main thread's Handler Runnable

                                        WebApi api = RetrofitClient.getInstance().getMyApi();
                                        w = api.createPartFromString(tempheight + "");
                                        h = api.createPartFromString(tempwidth + "");
                                        imageSendingForNavigtion(collectingImages, h, w, fl);

                                        collectingImages.clear();
                                        captureHandler.removeCallbacksAndMessages(null);
                                    }
                                });

                            }

                            break;
                    }
                } catch (Exception ex) {
                    Toast.makeText(MainActivity.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });

        loadToolbar();
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, PackageManager.PERMISSION_GRANTED);
        readyForSpeechRecognition();
        readyForSpeechRecognitionOfPerson();
        readyForSpeechRecognitionOfObject();




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

                    if(isNavigation){
                        startSpeechRecognition();
                    }
                    else if(isPersonDetection){
                        startSpeechRecognitionForPerson();
                    }
                    else if(isObjectDetection){
                        startSpeechRecognitionForObject();
                    }


                }
            });

            //askForModules();

        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }


    }

    private void askForModules() {
        try {

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    textToSpeech.speak("say either object module person module or navigation module", TextToSpeech.QUEUE_FLUSH, null, null);
                }
            }, 5000);
            Handler handler1=new Handler();
            handler1.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startSpeechRecognition();
                }
            },11000);
        }catch (Exception e){
            e.printStackTrace();
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



    }

    private void startSpeechRecognition() {
        binding.mic.setBackgroundDrawable(getDrawable(R.drawable.listening_mic));
        speechRecognizer.startListening(intentRecognizer);

    }
    private void startSpeechRecognitionForPerson() {
        binding.mic.setBackgroundDrawable(getDrawable(R.drawable.listening_mic));
        speechRecognizerForPerson.startListening(intentRecognizer);

    }

    private void startSpeechRecognitionForObject() {
        binding.mic.setBackgroundDrawable(getDrawable(R.drawable.listening_mic));
        speechRecognizerForObject.startListening(intentRecognizer);

    }



    public void threadToSpeak(String text){
        try {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
                }
            });
            thread.start();
//            if(textToSpeech.isSpeaking()){
//                Thread.sleep(4000);
//            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void readyForSpeechRecognitionOfObject(){

        intentRecognizer = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intentRecognizer.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        try{
            speechRecognizerForObject.setRecognitionListener(new RecognitionListener() {
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

                }

                @Override
                public void onError(int i) {
                    switch (i) {
                        case SpeechRecognizer.ERROR_NO_MATCH:
                            // Handle the case where no recognition result matched
                            textToSpeech.speak("Couldn't listen",TextToSpeech.QUEUE_FLUSH,null,null);
                            Log.e("SpeechRecognition", "No recognition match");
                            // Perform actions or notify the user accordingly
                            break;
                        case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                            // Handle the case where no speech input was detected
                            Log.e("SpeechRecognition", "Speech timeout: No speech input detected");
                            // Perform actions or notify the user accordingly
                            break;
                        // Handle other error cases if necessary
                        default:
                            Log.e("SpeechRecognition", "Error: " + i);
                            // Handle other types of errors
                            // Perform actions or notify the user accordingly
                            break;
                    }

                }

                @Override
                public void onResults(Bundle bundle) {

                    ArrayList<String> s = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                    String temp = "";
                    if(s!=null){
                        temp=s.get(0);
                        Log.e("all commands of objects",temp);
                        if(temp.equalsIgnoreCase("Objects in front")){
                            //Toast.makeText(MainActivity.this, temp, Toast.LENGTH_SHORT).show();
                            if (which_objects != null) {
                                for (Coordinate c : which_objects) {


                                    String textToRead = "" + c.getLabel();
                                    textToSpeech.speak(textToRead, TextToSpeech.QUEUE_ADD, null, null);


                                }
                            } else {
                                textToSpeech.speak("There is no object in front of you",TextToSpeech.QUEUE_FLUSH,null,null);
                            }


                        }
                         else if(temp.contains("where is")){

                            Enumeration<String> keys = objPosition.keys();
                            while (keys.hasMoreElements()) {


                                String key = keys.nextElement();
                                if (temp.equalsIgnoreCase(key)) {

                                    String value = objPosition.get(key);
                                    if (value.equalsIgnoreCase("") || value.equalsIgnoreCase(" ") || value==null) {
                                        textToSpeech.speak("Could not detect the object" + key, TextToSpeech.QUEUE_FLUSH, null, null);
                                    } else {



                                        threadToSpeak("It is on your "+value);

                                    }
                                }
                            }


                        }
                        else if(temp.equalsIgnoreCase("Objects on right")){
                            boolean flagObjectFound=false;
                            if (which_objects != null) {
                                for (Coordinate c : which_objects) {
                                    if(c.getPosition().equalsIgnoreCase("right")){
                                        flagObjectFound=true;
                                        String textToRead = "" + c.getLabel();
                                        textToSpeech.speak(textToRead, TextToSpeech.QUEUE_ADD, null, null);

                                    }


                                }
                                if(!flagObjectFound){
                                    textToSpeech.speak("There was no object on right of you",TextToSpeech.QUEUE_FLUSH,null,null);
                                }
                            } else {
                                textToSpeech.speak("The object list is null",TextToSpeech.QUEUE_FLUSH,null,null);
                            }


                        }
                        else if(temp.equalsIgnoreCase("objects on left")){
                            boolean flagObjectFound=false;
                            if (which_objects != null) {
                                for (Coordinate c : which_objects) {
                                    if(c.getPosition().equalsIgnoreCase("left")){
                                        flagObjectFound=true;
                                        String textToRead = "" + c.getLabel();
                                        textToSpeech.speak(textToRead, TextToSpeech.QUEUE_ADD, null, null);

                                    }


                                }
                                if(!flagObjectFound){
                                    textToSpeech.speak("There was no object on left of you",TextToSpeech.QUEUE_FLUSH,null,null);
                                }
                            } else {
                                textToSpeech.speak("The object list is null",TextToSpeech.QUEUE_FLUSH,null,null);
                            }

                        }
                        else{
                            textToSpeech.speak("No such command of object exists",TextToSpeech.QUEUE_FLUSH,null,null);
                        }

                    }

                }

                @Override
                public void onPartialResults(Bundle bundle) {

                }

                @Override
                public void onEvent(int i, Bundle bundle) {

                }
            });


        }catch (Exception e){
            e.printStackTrace();
        }


    }

    private void readyForSpeechRecognitionOfPerson(){

        intentRecognizer = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intentRecognizer.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        try{
            speechRecognizerForPerson.setRecognitionListener(new RecognitionListener() {
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

                }

                @Override
                public void onError(int i) {

                    switch (i) {
                        case SpeechRecognizer.ERROR_NO_MATCH:
                            // Handle the case where no recognition result matched
                            textToSpeech.speak("Couldn't listen",TextToSpeech.QUEUE_FLUSH,null,null);
                            Log.e("SpeechRecognition", "No recognition match");
                            // Perform actions or notify the user accordingly
                            break;
                        case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                            // Handle the case where no speech input was detected
                            Log.e("SpeechRecognition", "Speech timeout: No speech input detected");
                            // Perform actions or notify the user accordingly
                            break;
                        // Handle other error cases if necessary
                        default:
                            Log.e("SpeechRecognition", "Error: " + i);
                            // Handle other types of errors
                            // Perform actions or notify the user accordingly
                            break;
                    }
                }

                @Override
                public void onResults(Bundle bundle) {

                    ArrayList<String> s = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                    String temp = "";
                    if (!isTTSSpeakingForPerson) {
//                        if(s==null || s.isEmpty()){
//                            textToSpeech.speak("s was null",TextToSpeech.QUEUE_FLUSH,null,null);
//                            Log.e("command person ","was null");
//                        }
                         if (s != null) {
                            temp = s.get(0);
//                            if(temp.equalsIgnoreCase("") || temp.equalsIgnoreCase(" ") || temp==null){
//                                textToSpeech.speak("temp had nothing",TextToSpeech.QUEUE_FLUSH,null,null);
//                            }

                            Log.e("all commands of persons", temp);
                            //LogToast.makeText(MainActivity.this, temp, Toast.LENGTH_SHORT).show();
                            if (temp.equalsIgnoreCase("Who is in front") && comingFromLongPressPerson) {
                                comingFromLongPressPerson=false;
                                captureAndSendImageForPerson();
                                textToSpeech.speak("Please wait",TextToSpeech.QUEUE_FLUSH,null,null);




                            }else if(temp.contains("costume colour") && comingFromdoubletapPerson){
                                comingFromdoubletapPerson=false;

//                                for(int j=0;j<doubleMetaphonesArrayList.size();j++){
//                                    if(temp.equalsIgnoreCase(doubleMetaphonesArrayList.get(j))){
//                                        temp=doubleMetaphonesArrayList.get(j);
//                                    }
//
//                                }
//                                Log.e("decodedTemp",temp);





                                Enumeration<String> keys = dictColor.keys();

                                while (keys.hasMoreElements()) {


                                    String key = keys.nextElement();

                                    if(temp.equalsIgnoreCase("Syed costume color")){
                                        temp="tayyab costume color";
                                    }
                                    if(temp.equalsIgnoreCase("mohid costume colour") || temp.equalsIgnoreCase("mohit costume colour") ||
                                    temp.equalsIgnoreCase("mohed costume colour") || temp.equalsIgnoreCase("moheet costume colour")
                                    ){
                                        temp="moheed costume colour";
                                    }
                                    if(temp.equalsIgnoreCase("arsalan costume colour") || temp.equalsIgnoreCase("arssalaan costume colour") ||
                                    temp.equalsIgnoreCase("assalam costume colour")){
                                        temp="arslan costume colour";
                                    }
                                    Log.e("After change name",temp);
                                    if (temp.equalsIgnoreCase(key)) {

                                        ArrayList<String> value = dictColor.get(key);
                                        if (value.size() == 0) {
                                            textToSpeech.speak("Could not detect the color on" + key, TextToSpeech.QUEUE_FLUSH, null, null);
                                        } else {


                                            String txt = "";
//                                            for (int k = 0; k < value.size(); k++) {
//                                                txt += value.get(k);
//                                            }
                                            txt=value.get(0);
                                            threadToSpeak(temp+"is"+txt);
                                            txt = "";
                                            //textToSpeech.speak("Persons on"+key+"are"+txt,TextToSpeech.QUEUE_FLUSH,null,null);


                                        }


                                    }



                                }



                            }
                            else if ((temp.contains("left") || temp.contains("right")) && comingFromdoubletapPerson) {
                                comingFromdoubletapPerson=false;
                                Enumeration<String> keys = dictPosition.keys();

                                while (keys.hasMoreElements()) {

                                    String localvar="";
                                    if(temp.contains("left")){
                                        localvar="left";
                                    }
                                    else if(temp.contains("right")){
                                        localvar="right";
                                    }


                                    String key = keys.nextElement();
                                    if(temp.equalsIgnoreCase("mohid "+localvar) || temp.equalsIgnoreCase("mohit "+localvar) ||
                                            temp.equalsIgnoreCase("mohed "+localvar) || temp.equalsIgnoreCase("moheet "+localvar)
                                    ){
                                        temp="moheed "+localvar;
                                    }
                                    if(temp.equalsIgnoreCase("arsalan "+localvar) || temp.equalsIgnoreCase("arssalaan "+localvar) |
                                    temp.equalsIgnoreCase("assalam "+localvar)){
                                        temp="arslan "+localvar;
                                    }
                                    if(temp.equalsIgnoreCase("syed "+localvar)){
                                        temp="tayyab "+localvar;
                                    }

                                    Log.e("After change name",temp);
                                    if (temp.equalsIgnoreCase(key)) {


                                        ArrayList<String> value = dictPosition.get(key);
                                        if (value.size() == 0) {
                                            textToSpeech.speak("There is no one on" + key, TextToSpeech.QUEUE_FLUSH, null, null);
                                        } else {


                                            String txt = "";
                                            for (int k = 0; k < value.size(); k++) {
                                                txt += value.get(k);
                                            }
                                            threadToSpeak(txt);
                                            txt = "";
                                            //textToSpeech.speak("Persons on"+key+"are"+txt,TextToSpeech.QUEUE_FLUSH,null,null);


                                        }


                                    }


                                    // Do something with the key and value
                                    //System.out.println("Key: " + key);
                                    //System.out.println("Value: " + value);
                                }
                            }
                            else{
                                textToSpeech.speak("No such command exists",TextToSpeech.QUEUE_FLUSH,null,null);
                            }

                        }

                    }
                }




                @Override
                public void onPartialResults(Bundle bundle) {

                }

                @Override
                public void onEvent(int i, Bundle bundle) {

                }
            });




        }catch(Exception e){
            e.printStackTrace();
        }

    }




    public void capturingImagesForSource(){

        try{

            forGettingSourceRepeatedly=new Thread(new Runnable() {
                @Override
                public void run() {
                    if(isNavigation) {
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if(isNavigation) {
                                    capturingImageForNavigation();
                                    if (collectingImages.size() == 5) {
                                        isCapturing=true;
                                        WebApi api = RetrofitClient.getInstance().getMyApi();
                                        w = api.createPartFromString(tempheight + "");
                                        h = api.createPartFromString(tempwidth + "");
                                        imageSendingForNavigtion(collectingImages, h, w, fl);
                                        collectingImages.clear();
                                        Log.d(TAG, "current PLace "+getCurrentValuesForNavigation.getPlace().replace('_',' ')+" previous place"+previousPlace);

                                        if (!getCurrentValuesForNavigation.getPlace().replace('_', ' ').equalsIgnoreCase(previousPlace)) {
                                            GetPath(userSelectedDestination);
                                        }
                                        isCapturing=false;

                                    }
                                }
                            }

                        });
                        handler.postDelayed(this, 8000);
                    }
                }
            });

            forGettingSourceRepeatedly.start();

        }catch(Exception e){
            e.printStackTrace();
        }


    }


    public void GetPath(String dest){

        try {

            WebApi api=RetrofitClient.getInstance().getMyApi();
            PathGetter p=new PathGetter();
            p.source=getCurrentValuesForNavigation.getPlace().replace('_',' ');
            p.destination=dest;

            if(p.source.equalsIgnoreCase(p.destination)){

                textToSpeech.speak("You are standing at your destination",TextToSpeech.QUEUE_FLUSH,null,null);
                Handler handler=new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        counterForSpeakingSource=0;
                        goingfornavigation=false;
                        return;
                    }
                },1500);

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



                            }
                            // Toast.makeText(MainActivity.this, "new Path wala"+newPath.get(0).getPlaceName().replace('_',' '), Toast.LENGTH_LONG).show();
                            String t = " Next place is " + newPath.get(0).getPlaceName().replace('_', ' ') + " and direction is" + newPath.get(0).getDirectionName() + " and the steps are" + newPath.get(0).getSteps();
                            textToSpeech1.speak(t, TextToSpeech.QUEUE_ADD, null, null);




                            synchronized (textToSpeech1) {
                                if (textToSpeech1.isSpeaking()) {
                                    try {
                                        textToSpeech1.wait(3000);  // wait for 3 seconds or until notified
                                        // repeatGettingPath(true,p.destination,p.source);
                                    } catch (InterruptedException e) {
                                        // Handle InterruptedException
                                        e.printStackTrace();
                                    }
                                }
                            }


                            //GetPath(userSelectedDestination);
                            //capturingImagesForSource();

                        } else {

                            Log.e("no paths "," available");
                            //Toast.makeText(MainActivity.this, "No paths available.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Response unsuccessful", Toast.LENGTH_LONG).show();

                    }
                }

                @Override
                public void onFailure(Call<ArrayList<ArrayList<String>>> call, Throwable t) {
                    Log.e("Api failure in calling path",t.getMessage());
                    t.printStackTrace();

                }
            });
            //capturingImagesForSource();
        } catch (Exception e) {
            e.printStackTrace();
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
                    //startSpeechRecognition();
                }

                @Override
                public void onError(int i) {
                    switch (i) {
                        case SpeechRecognizer.ERROR_NO_MATCH:
                            if(goingfornavigation){
                                textToSpeech.speak("Couldn't listen destination",TextToSpeech.QUEUE_FLUSH,null,null);
                                goingfornavigation=false;
                            }
                            else if(listeningForFloor){
                                textToSpeech.speak("Couldn't listen floor",TextToSpeech.QUEUE_FLUSH,null,null);
                                listeningForFloor=false;
                                fl=null;
                            }
                            // Handle the case where no recognition result matched

                            Log.e("SpeechRecognition", "No recognition match");
                            // Perform actions or notify the user accordingly
                            break;
                        case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                            // Handle the case where no speech input was detected
                            Log.e("SpeechRecognition", "Speech timeout: No speech input detected");
                            // Perform actions or notify the user accordingly
                            break;
                        // Handle other error cases if necessary
                        default:
                            Log.e("SpeechRecognition", "Error: " + i);
                            // Handle other types of errors
                            // Perform actions or notify the user accordingly
                            break;
                    }

                }

                @Override
                public void onResults(Bundle bundle) {
                    try {
                        ArrayList<String> s = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                        String temp = "";
                        if (s != null) {
                            temp = s.get(0);

                            if (listeningForFloor) {
                                Toast.makeText(getApplicationContext(), temp, Toast.LENGTH_SHORT).show();
                                boolean floorFound=false;
                                for(int i=0;i<DataStorage.getInstance().getFloorsArrayList().size();i++){
                                    if(temp.equalsIgnoreCase(DataStorage.getInstance().getFloorsArrayList().get(i).name)){
                                        floorFound=true;
                                        break;
                                    }
                                }
                                if(!floorFound){
                                   textToSpeech.speak("The floor doesn't exist",TextToSpeech.QUEUE_FLUSH,null,null);
                                   return;
                                }
                                textToSpeech.speak(temp + "has been selected as floor", TextToSpeech.QUEUE_ADD, null, null);
                                WebApi api = RetrofitClient.getInstance().getMyApi();
                                fl = api.createPartFromString(temp);

                                listeningForFloor = false;

                            } else if (temp.equalsIgnoreCase("Object module")) {
                                Toast.makeText(MainActivity.this, temp, Toast.LENGTH_SHORT).show();
                                switchBetweenModules(true, false, false);

                                String textToRead = "object Detection Mode Enabled";
                                textToSpeech.speak(textToRead, TextToSpeech.QUEUE_FLUSH, null, null);


                            }  else if (temp.equalsIgnoreCase("Person module")) {
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


//                                Toast.makeText(MainActivity.this, temp, Toast.LENGTH_SHORT).show();
//                                if (which_objects != null) {
//                                    for (Coordinate c : which_objects) {
//
//
//                                        String textToRead = "" + c.getLabel();
//                                        textToSpeech.speak(textToRead, TextToSpeech.QUEUE_ADD, null, null);
//
//
//                                    }
//                                } else {
//                                    Toast.makeText(MainActivity.this, "Object list has null values", Toast.LENGTH_SHORT).show();
//                                }
                            } else {
                                try {
                                    if (goingfornavigation) {
                                        boolean flagToCheckPlaceExists=false;
                                        if(counterForSpeakingSource==0) {
                                            for(int i=0;i<DataStorage.getInstance().getPlacesArrayList().size();i++){
                                                if(temp.equalsIgnoreCase(DataStorage.getInstance().getPlacesArrayList().get(i).name)){
                                                   flagToCheckPlaceExists=true;
                                                   break;
                                                }

                                            }
                                            if(!flagToCheckPlaceExists){
                                                textToSpeech.speak("This destination does not exists as a place",TextToSpeech.QUEUE_FLUSH,null,null);
                                                goingfornavigation=false;
                                                return;
                                            }
                                            String forspeak = temp + "has been selected as your destination";
                                            textToSpeech.speak(forspeak, TextToSpeech.QUEUE_ADD, null, null);


                                            userSelectedDestination = temp;
                                            WebApi api = RetrofitClient.getInstance().getMyApi();
                                            String t = getCurrentValuesForNavigation.getPlace().replace('_', ' ');


                                            PathGetter p = new PathGetter();
                                            p.source = t;
                                            p.destination = temp;
                                            if (p.source.equalsIgnoreCase(p.destination)) {
                                                counterForSpeakingSource=0;
                                                goingfornavigation = false;
                                                textToSpeech.speak("You are standing at your destination", TextToSpeech.QUEUE_FLUSH, null, null);
                                                return;
                                            }

                                            Log.d(TAG, p.source + p.destination + " source and dest");

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


                                                            }
                                                            // Toast.makeText(MainActivity.this, "new Path wala"+newPath.get(0).getPlaceName().replace('_',' '), Toast.LENGTH_LONG).show();
                                                            String t = " Next place is " + newPath.get(0).getPlaceName().replace('_', ' ') + " and direction is" + newPath.get(0).getDirectionName() + " and the steps are" + newPath.get(0).getSteps();
                                                            textToSpeech1.speak(t, TextToSpeech.QUEUE_ADD, null, null);


                                                            synchronized (textToSpeech1) {
                                                                if (textToSpeech1.isSpeaking()) {
                                                                    try {
                                                                        textToSpeech1.wait(3000);  // wait for 3 seconds or until notified
                                                                        // repeatGettingPath(true,p.destination,p.source);
                                                                    } catch (
                                                                            InterruptedException e) {
                                                                        // Handle InterruptedException
                                                                        e.printStackTrace();
                                                                    }
                                                                }
                                                            }
                                                            isApiRequestForPathinProgress = false;
                                                            //GetPath(userSelectedDestination);
                                                            //capturingImagesForSource();

                                                        } else {
                                                            Log.e("no paths ", " available");
                                                            //Toast.makeText(MainActivity.this, "No paths available.", Toast.LENGTH_SHORT).show();
                                                        }
                                                    } else {
                                                        Toast.makeText(MainActivity.this, "Response unsuccessful", Toast.LENGTH_LONG).show();
                                                        isApiRequestForPathinProgress = false;
                                                    }
                                                }

                                                @Override
                                                public void onFailure(Call<ArrayList<ArrayList<String>>> call, Throwable t) {
                                                    Log.e("Api failure in calling path", t.getMessage());
                                                    t.printStackTrace();
                                                    isApiRequestForPathinProgress = false;
                                                }
                                            });
                                            counterForSpeakingSource=1;

                                        }


                                    }
                                } catch (Exception e) {
                                    Log.e("Speech recognition", e.getMessage());
                                }
                            }
                        }
                        //startSpeechRecognition();
                    } catch (Exception e) {
                        Log.e("problem  in speech recognition on complete results", e.getMessage());
                    }
                }

                @Override
                public void onPartialResults(Bundle bundle) {
                    //startSpeechRecognition();

                }

                @Override
                public void onEvent(int i, Bundle bundle) {

                }
            });
        } catch (Exception e) {
            Log.e("problem overall in speech recognition", e.getMessage());
        }

    }
    //source = azhar dest= umar

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





// Extra Code for data Storage




}