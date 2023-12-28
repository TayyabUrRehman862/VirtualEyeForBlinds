package com.example.virtualeyeforblinds.extraClasses;

import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.example.virtualeyeforblinds.globalClass.DataStorage;

public class NetworkInitializer {
    private static boolean isListenerSetUp = false;

    public static void initialize(Context context) {
        if (!isListenerSetUp && context != null) {
            Handler handler=new Handler(Looper.getMainLooper());
            NetworkUtils.setNetworkChangeListener(new NetworkUtils.NetworkChangeListener() {
                @Override
                public void onNetworkChanged(boolean isConnected) {
                    if (isConnected) {



                        handler.post(new Runnable() {
                            @Override
                            public void run() {

                                DataStorage dataStorage = DataStorage.getInstance();
                                dataStorage.setContext(context);

                                dataStorage.getAllFloors();
                                dataStorage.getAllTypes();
                                dataStorage.getPersonsList();
                                dataStorage.getPlacesList();
                                dataStorage.getAllDirections();
                                dataStorage.getAllLinks();



                            }
                        });
                        // Network is available
                        // Perform actions when network becomes available
                    } else {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context, "Network is not available DataStorage", Toast.LENGTH_SHORT).show();
                                // Handle the case when network is not available
                            }
                        });
                    }

                }
            });
            IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
            context.getApplicationContext().registerReceiver(new NetworkUtils.NetworkChangeReceiver(), intentFilter);

            isListenerSetUp = true;
        }
    }
}

