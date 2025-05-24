package com.example.marketplaceandroid.utils;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkUtils {
    private static Context context;

    public static void init(Context appContext) {
        context = appContext.getApplicationContext();
    }
    public static boolean isNetworkAvailable() {
        if (context == null) return true; // Par défaut, on assume que le réseau est disponible

        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }

        return false;
    }
}
