package com.example.virtualeyeforblinds.api;

import android.content.Context;
import android.net.Uri;


import androidx.appcompat.resources.Compatibility;
import androidx.camera.core.ImageProxy;

import com.example.virtualeyeforblinds.Person;
import com.example.virtualeyeforblinds.extraClasses.ApiResponeOfFrameObject;
import com.example.virtualeyeforblinds.extraClasses.ApiResponseOfFrame;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface WebApi {


    String BASE_URL = "http://192.168.100.6:5000/api/";





    @GET("person/get_all")
    public Call<ArrayList<Person>> getAllPersons();

    @Multipart
    @POST("navigation/recognize_place")
    public Call<ApiResponseOfFrame> frame(
            @Part MultipartBody.Part img,
            @Part("height") RequestBody height,
            @Part("width") RequestBody width,
            @Part("floor") RequestBody floor
    );

    @Multipart
    @POST("object/detect_object")
    public Call<ApiResponeOfFrameObject> objectFrame(
            @Part MultipartBody.Part img
    );

    @Multipart
    @POST("VirtualEye/uploadPlaces")
    public Call<String> uploadPlaces(
            @Part ArrayList<MultipartBody.Part> images,
            @Part("ID") RequestBody id,
            @Part("Name") RequestBody name,
            @Part("FloorName") RequestBody floorid,
            @Part("TypeName") RequestBody typeid
    );

    public default MultipartBody.Part prepareFilePart(String partName, Uri fileUri, Context context) throws IOException {
        File file = FileUtil.from(context, fileUri);
        RequestBody requestFile =
                RequestBody.create(
                        MediaType.parse(context.getContentResolver().getType(fileUri)),
                        file
                );
        return MultipartBody.Part.createFormData(partName,
                file.getName(),
                requestFile);
    }


    public default RequestBody createPartFromString(String descriptionString){
        RequestBody description =
                RequestBody.create(
                        okhttp3.MultipartBody.FORM, descriptionString);
        return  description;

    }

}
