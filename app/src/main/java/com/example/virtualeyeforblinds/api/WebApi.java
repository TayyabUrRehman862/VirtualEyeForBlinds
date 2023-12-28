package com.example.virtualeyeforblinds.api;

import android.content.Context;
import android.net.Uri;
import android.util.Log;


import com.example.virtualeyeforblinds.apiresponses.ApiResponseOfPerson;
import com.example.virtualeyeforblinds.Person;
import com.example.virtualeyeforblinds.Place;
import com.example.virtualeyeforblinds.apiresponses.ApiResponeOfFrameObject;
import com.example.virtualeyeforblinds.apiresponses.ApiResponseOfFrame;
import com.example.virtualeyeforblinds.models.Coordinate;
import com.example.virtualeyeforblinds.models.Details;
import com.example.virtualeyeforblinds.models.Direction;
import com.example.virtualeyeforblinds.models.Images;
import com.example.virtualeyeforblinds.models.SimpleResponse;
import com.example.virtualeyeforblinds.models.Types;
import com.example.virtualeyeforblinds.models.floors;
import com.example.virtualeyeforblinds.models.DetailsSpinner;
import com.example.virtualeyeforblinds.models.Links;
import com.example.virtualeyeforblinds.models.PathGetter;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface WebApi {


    String BASE_URL = "http://192.168.100.6:5000/api/";



//    @PUT("link/put_all")
//    public Call<String> UpdateMatrix(@Body ArrayList<DetailsSpinner> detailsSpinners);

    @GET("image/get_person_image")
    Call<ResponseBody> getImageByName(@Query("name") String a);


    @DELETE("floor/delete")
    Call<SimpleResponse> deleteFloorById(@Query("id") Integer a);

    @DELETE("type/delete")
    Call<SimpleResponse> deleteTypeById(@Query("id") Integer a);

    @DELETE("link/delete")
    Call<SimpleResponse> deleteLinkById(@Query("id") Integer a);


    @PUT("link/put")
    Call<SimpleResponse> updateLink(@Query("id") Integer a, @Body Links l);



    @POST("type/post")
    Call<SimpleResponse> createType(@Body Types t);

    @POST("floor/post")
    Call<SimpleResponse> createFloor(@Body floors f);


    @POST("link/post_all")
    public Call<SimpleResponse> insertData(@Body ArrayList<Details> data);

    @POST("navigation/get_path")
    public Call<ArrayList<ArrayList<String>>> path(
            @Body PathGetter p
    );

    @GET("floor/get_all")
    public Call<ArrayList<floors>> getAllFloors();


    @GET("direction/get_all")
    public Call<ArrayList<Direction>> getAllDirections();


    @GET("type/get_all")
    public Call<ArrayList<Types>> getAllTypes();


    @GET("link/get_all")
    public Call<ArrayList<Links>> getAllLinks();


    @GET("place/get_all")
    public Call<ArrayList<Place>> getAllplaces();

    @GET("person/get_all")
    public Call<ArrayList<Person>> getAllPersons();

    @Multipart
    @POST("navigation/recognize_place_with_tags")
    public Call<ApiResponseOfFrame> frame(
            @Part ArrayList<MultipartBody.Part> img,
            @Part("height") RequestBody height,
            @Part("width") RequestBody width,
            @Part("floor") RequestBody floor
    );

    @Multipart
    @POST("object/detect_object")
    public Call<ArrayList<Coordinate>> objectFrame(
            @Part MultipartBody.Part img
    );


    @Multipart
    @POST("person/recognize")
    public Call<ArrayList<ApiResponseOfPerson>> personFrame(
            @Part("height") RequestBody height,
            @Part("width") RequestBody width,
            @Part MultipartBody.Part img
    );


    @Multipart
    @POST("place/post")
    public Call<SimpleResponse> uploadPlaces(


            @Part("name") RequestBody name,
            @Part("floorId") RequestBody floorid,
            @Part("typeId") RequestBody typeid,
            @Part("doorDirectionId") RequestBody doorDirectionId,
            @Part ArrayList<MultipartBody.Part> east,
            @Part ArrayList<MultipartBody.Part> west,
            @Part ArrayList<MultipartBody.Part> north,
            @Part ArrayList<MultipartBody.Part> south
    );

    @Multipart
    @POST("person/post")
    public Call<SimpleResponse> uploadPerson(
            @Part("name") RequestBody name,
            @Part ArrayList<MultipartBody.Part> image
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
//    public default ArrayList<MultipartBody.Part> prepareFileParts(String partName, ArrayList<Uri> fileUris, Context context) throws IOException {
//        ArrayList<MultipartBody.Part> parts = new ArrayList<>();
//
//        for (Uri fileUri : fileUris) {
//            File file = FileUtil.from(context, fileUri);
//            String fileType = context.getContentResolver().getType(fileUri);
//
//            if (fileType == null) {
//                // Set a default MIME type for files when type cannot be retrieved
//                fileType = "image/jpeg"; // Replace with an appropriate default MIME type
//                Log.e("fileType is null", "Default MIME type set: " + fileType);
//            }
//
//            RequestBody requestFile = RequestBody.create(MediaType.parse(fileType), file);
//            MultipartBody.Part part = MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
//            parts.add(part);
//        }
//
//        return parts;
//    }
public default ArrayList<MultipartBody.Part> prepareFileParts(String partName, ArrayList<Uri> fileUris, Context context) throws IOException {
    ArrayList<MultipartBody.Part> parts = new ArrayList<>();

    for (Uri fileUri : fileUris) {
        File file = FileUtil.from(context, fileUri);
        String fileType = context.getContentResolver().getType(fileUri);
        Log.e("file type",fileType);
        if (fileType == null) {
            // Set a default MIME type for files when type cannot be retrieved
            fileType = "image/jpeg"; // Replace with an appropriate default MIME type
            Log.e("fileType is null", "Default MIME type set: " + fileType);
        }

        RequestBody requestFile = RequestBody.create(MediaType.parse(fileType), file);
        MultipartBody.Part part = MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
        parts.add(part);
    }

    return parts;
}



    public default RequestBody createPartFromString(String descriptionString){
        RequestBody description =
                RequestBody.create(
                        okhttp3.MultipartBody.FORM, descriptionString);
        return  description;

    }


}
