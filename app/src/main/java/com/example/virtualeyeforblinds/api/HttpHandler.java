package com.example.virtualeyeforblinds.api;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpHandler {

    private static final String TAG = HttpHandler.class.getSimpleName();
    private static final String BOUNDARY ="";

    public HttpHandler() {
    }



    public String makeRequest(String reqUrl, String requestMethod, byte[] imageData) {
        String response = null;
        try {
            URL url = new URL(reqUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(requestMethod);
            conn.setDoOutput(true);

            // Set content type to multipart/form-data
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);

            // Get the output stream of the connection
            OutputStream outputStream = conn.getOutputStream();
            DataOutputStream dos = new DataOutputStream(outputStream);

            // Write image data
            dos.writeBytes("--" + BOUNDARY + "\r\n");
            dos.writeBytes("Content-Disposition: form-data; name=\"image\"; filename=\"image.png\"\r\n");
            dos.writeBytes("Content-Type: image/png\r\n\r\n");
            dos.write(imageData);
            dos.writeBytes("\r\n");

            dos.writeBytes("--" + BOUNDARY + "--\r\n");
            dos.flush();
            dos.close();

            // Read the response
            InputStream in = new BufferedInputStream(conn.getInputStream());
            response = convertStreamToString(in);
        } catch (IOException e) {
            Log.e(TAG, "IOException: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
        return response;
    }

    private String makeRequestWithBody(String reqUrl, String requestMethod, String postData) {
        String response = null;
        try {
            URL url = new URL(reqUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(requestMethod);
            conn.setDoOutput(true); // Enable sending data in the request body

            // Write the POST data to the request body
            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(postData.getBytes());
            outputStream.close();

            // read the response
            InputStream in = new BufferedInputStream(conn.getInputStream());
            response = convertStreamToString(in);
        } catch (IOException e) {
            Log.e(TAG, "IOException: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
        return response;
    }

    private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return sb.toString();
    }
}






