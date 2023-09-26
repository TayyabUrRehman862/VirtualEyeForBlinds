package com.example.virtualeyeforblinds;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.virtualeyeforblinds.api.RetrofitClient;
import com.example.virtualeyeforblinds.api.WebApi;
import com.example.virtualeyeforblinds.databinding.ActivityMainBinding;
import com.example.virtualeyeforblinds.extraClasses.ApiResponeOfFrameObject;
import com.example.virtualeyeforblinds.extraClasses.ApiResponseOfFrame;
import com.example.virtualeyeforblinds.extraClasses.Coordinate;
import com.example.virtualeyeforblinds.extraClasses.OverlayView;
import com.google.android.material.navigation.NavigationView;
import com.google.common.util.concurrent.ListenableFuture;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private OverlayView overlayView;


    private ArrayList<Coordinate> which_objects;
    private SpeechRecognizer speechRecognizer;
    private Intent intentRecognizer;
    int SPEECH_REQUEST_CODE = 100;
    public boolean isObjectDetection = false;
    public boolean isPersonDetection = false;
    public boolean isNavigation = false;
    private boolean isApiRequestInProgress = false;
    final int PERMISSION_REQUEST_CODE = 1001;
    final int CAMERA_RESULT_CODE = 1002;
    ActivityMainBinding binding;
    private ListenableFuture<ProcessCameraProvider> cameraProviderListenableFuture;
    NavigationView nav;
    ActionBarDrawerToggle toggle;
    DrawerLayout drawerLayout;
    ImageCapture imageCapture;
    private TextToSpeech textToSpeech;

    public void openCamera() {
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
    }

    public void requestPermission() {
        if (checkPermissions() == false) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CODE);
        } else {

            openCamera();
        }

    }

    public boolean checkPermissions() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            //if permission  granted
            return true;
        } else {

            //if permission not granted
            return false;
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
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
    }

    private void startCameraX(ProcessCameraProvider processCameraProvider) {
        processCameraProvider.unbindAll();

        CameraSelector cameraSelector = new CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build();
        Preview p = new Preview.Builder().build();
        p.setSurfaceProvider(binding.previewCamera.getSurfaceProvider());

        imageCapture =
                new ImageCapture.Builder()
                        .build();

        processCameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, p, imageCapture);
        //startSpeechRecognition();

        // Create a handler to capture images periodically
        Handler handler = new Handler(Looper.getMainLooper());
        Runnable captureRunnable = new Runnable() {
            @Override
            public void run() {
                // Capture the image and provide a callback
                captureImage();
                handler.postDelayed(this,100);
                startSpeechRecognition();
            }
        };

        handler.postDelayed(captureRunnable, 100); // Start capturing

        // ... Rest of the code ...
    }
    private void updateOverlay(ArrayList<Coordinate> coordinates) {
        if (overlayView != null) {
            overlayView.setCoordinates(coordinates);
        }
    }



    private void captureImage() {
        if (isApiRequestInProgress) {
            //Toast.makeText(this, "Another Api request in Process", Toast.LENGTH_SHORT).show();
            return; // An API request is already in progress, skip capturing this time
        }
        isApiRequestInProgress = true;
        imageCapture.takePicture(getExecutor(), new ImageCapture.OnImageCapturedCallback() {
            @Override
            public void onCaptureSuccess(@NonNull ImageProxy image) {
                super.onCaptureSuccess(image);

                ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                byte[] imageData = new byte[buffer.remaining()];
                buffer.get(imageData);

                // Create a RequestBody from the captured image data
                RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), imageData);

                // Prepare a MultipartBody.Part using the RequestBody
                MultipartBody.Part imagePart = MultipartBody.Part.createFormData("img", "image.jpg", requestFile);
                int tempheight=image.getHeight();
                int tempwidth=image.getWidth();

                WebApi api = RetrofitClient.getInstance().getMyApi();
                RequestBody w=api.createPartFromString(image.getWidth()+"");
                RequestBody h=api.createPartFromString(image.getHeight()+"");
                RequestBody fl=api.createPartFromString("first floor");
                image.close();

                if(isNavigation) {

                    api.frame(imagePart, h, w, fl).enqueue(new Callback<ApiResponseOfFrame>() {

                        @Override
                        public void onResponse(Call<ApiResponseOfFrame> call, Response<ApiResponseOfFrame> response) {

                            if (response.isSuccessful()) {
                                ApiResponseOfFrame s = response.body();
                                String p = s.getPlace();
                                String t = s.getTurn();
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

                                    Coordinate coordinate=new Coordinate();
                                    coordinate.setXmax(right);
                                    coordinate.setXmin(left);
                                    coordinate.setYmax(top);
                                    coordinate.setYmin(bottom);
                                    coordinate.setDistance(apiCoordinate.getDistance());
                                    coordinate.setLabel(apiCoordinate.getLabel());
                                    screenCoordinates.add(coordinate);


                                }

                                which_objects=screenCoordinates;
                                updateOverlay(which_objects);
//                                for (Coordinate c : which_objects) {
//                                    double confidence = c.getConfidence();
//                                    String label = c.getLabel();
//                                    int xmax = c.getXmax();
//                                    int xmin = c.getXmin();
//                                    int ymax = c.getYmax();
//                                    int ymin = c.getYmin();
//                                    //    String textToRead = "object: "+c.getLabel();
//                                    //     textToSpeech.speak(textToRead, TextToSpeech.QUEUE_FLUSH, null, null);
//
//                                }

                                String textToRead = "Place is " + p + ".Turn" + t;
                                textToSpeech.speak(textToRead, TextToSpeech.QUEUE_FLUSH, null, null);




                                 // Start capturing

                                isApiRequestInProgress = false;
                                //Toast.makeText(MainActivity.this, "response succesful", Toast.LENGTH_SHORT).show();

                            }

                        }

                        @Override
                        public void onFailure(Call<ApiResponseOfFrame> call, Throwable t) {
                            Toast.makeText(MainActivity.this, t.toString(), Toast.LENGTH_SHORT).show();
                            isApiRequestInProgress = false;
                        }
                    });
                }
                else if(isObjectDetection){
                    api.objectFrame(imagePart).enqueue(new Callback<ApiResponeOfFrameObject>() {
                        @Override
                        public void onResponse(Call<ApiResponeOfFrameObject> call, Response<ApiResponeOfFrameObject> response) {
                            if(response.isSuccessful()) {
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
                }
                // Now you can use the imagePart in your network call or upload process
            }


            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                super.onError(exception);
            }
        });
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



    public void loadToolbar(){
        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        nav=(NavigationView)findViewById(R.id.navmenu);
        drawerLayout=(DrawerLayout)findViewById(R.id.drawer);


        toggle=new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        nav.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem)
            {
                switch (menuItem.getItemId())
                {
                    case R.id.menu_add_home:
                        Toast.makeText(getApplicationContext(),"Home Panel is Open",Toast.LENGTH_LONG).show();
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;

                    case R.id.menu_add_place:
                        Intent intent=new Intent(getApplicationContext(),PlacesActivity.class);
                        startActivity(intent);

                        break;

                    case R.id.menu_add_person:
                        Intent i=new Intent(getApplicationContext(),PersonActivity.class);
                        startActivity(i);
                        break;
                    case R.id.menu_link_places:
                        Intent j=new Intent(getApplicationContext(),LinkPlacesActivity.class);
                        startActivity(j);
                        break;
                    case R.id.menu_exit:
                        callAlert();
                }

                return true;
            }
        });

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
        which_objects=new ArrayList<>();

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        overlayView=findViewById(R.id.OverlayView);
        textToSpeech = new TextToSpeech(this, status -> {
            if (status != TextToSpeech.ERROR) {
                Toast.makeText(this, "Text to Speech Enabled Successfully", Toast.LENGTH_SHORT).show();
            }
        });

        loadToolbar();
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO},PackageManager.PERMISSION_GRANTED);
        readyForSpeechRecognition();


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



    }
    private void switchBetweenModules(Boolean object,Boolean person,Boolean navigation){
        isNavigation=navigation;
        isObjectDetection=object;
        isPersonDetection=person;
        if(isPersonDetection){
            binding.btnPersonDetection.setBackgroundColor(getColor(R.color.blue_p));
            binding.btnNavigation.setBackground(getDrawable(R.drawable.circle));
            binding.btnObjectDetection.setBackground(getDrawable(R.drawable.circle));
        }
        else if(isNavigation){
            binding.btnPersonDetection.setBackground(getDrawable(R.drawable.circle));
            binding.btnNavigation.setBackgroundColor(getColor(R.color.blue_p));
            binding.btnObjectDetection.setBackground(getDrawable(R.drawable.circle));
        }
        else if(isObjectDetection){
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
    private void startSpeechRecognition(){
        binding.mic.setBackgroundDrawable(getDrawable(R.drawable.listening_mic));
        speechRecognizer.startListening(intentRecognizer);

    }
    private void readyForSpeechRecognition() {
       intentRecognizer=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
       intentRecognizer.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
       speechRecognizer=SpeechRecognizer.createSpeechRecognizer(this);
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
               ArrayList<String> s=bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
               String temp="";
               if(s!=null){
                   temp=s.get(0);
                   if(temp.equalsIgnoreCase("Object Detection")){
                       Toast.makeText(MainActivity.this, temp, Toast.LENGTH_SHORT).show();
                        switchBetweenModules(true,false,false);

                        String textToRead = "object Detection Mode Enabled";
                        textToSpeech.speak(textToRead, TextToSpeech.QUEUE_FLUSH, null, null);





                   }
                   else if(temp.equalsIgnoreCase("Person Recognition")){
                       Toast.makeText(MainActivity.this, temp, Toast.LENGTH_SHORT).show();
                        switchBetweenModules(false,true,false);
                       String textToRead = "Person Recognition Mode Enabled";
                       textToSpeech.speak(textToRead, TextToSpeech.QUEUE_FLUSH, null, null);
                   }
                   else if(temp.equalsIgnoreCase("Navigation Module")){
                       Toast.makeText(MainActivity.this, temp, Toast.LENGTH_SHORT).show();
                        switchBetweenModules(false,false,true);
                       String textToRead = "Navigation Mode Enabled";
                       textToSpeech.speak(textToRead, TextToSpeech.QUEUE_FLUSH, null, null);
                   }
                   else if(temp.equalsIgnoreCase("Objects in front")){


                       Toast.makeText(MainActivity.this, temp, Toast.LENGTH_SHORT).show();
                       if(which_objects!=null){
                           for (Coordinate c : which_objects) {



                                       String textToRead = ""+c.getLabel();
                                       textToSpeech.speak(textToRead, TextToSpeech.QUEUE_FLUSH, null, null);








                           }
                       }
                       else{
                           Toast.makeText(MainActivity.this, "Object list has null values", Toast.LENGTH_SHORT).show();
                       }
                   }
                   else{
                       Toast.makeText(MainActivity.this, temp, Toast.LENGTH_SHORT).show();
                   }
               }
               startSpeechRecognition();
           }

           @Override
           public void onPartialResults(Bundle bundle) {
               startSpeechRecognition();

           }

           @Override
           public void onEvent(int i, Bundle bundle) {

           }
       });

    }


    private void callAlert() {
        AlertDialog.Builder  builder=new AlertDialog.Builder(this);
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
        AlertDialog d=builder.create();
        d.show();
    }


}