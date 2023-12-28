package com.example.virtualeyeforblinds.extraClasses;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkUtils {
    public interface NetworkChangeListener {
        void onNetworkChanged(boolean isConnected);
    }

    private static NetworkChangeListener networkChangeListener;

    public static void setNetworkChangeListener(NetworkChangeListener listener) {
        networkChangeListener = listener;
    }

    // Method to check network availability
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }

        return false; // Return false if connectivity manager is null or other issues occur
    }

    // Trigger the network change callback explicitly
    public static void notifyNetworkChanged(Context context) {
        boolean isConnected = isNetworkAvailable(context);
        if (networkChangeListener != null) {
            networkChangeListener.onNetworkChanged(isConnected);
        }
    }

    // BroadcastReceiver to listen for network changes
    public static class NetworkChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Trigger the network change callback when network status changes
            notifyNetworkChanged(context);
        }
    }
}
