package com.example.vip.abdr_app;

import android.content.Context;
import android.net.ConnectivityManager;


public class inertnetcheck {

    public boolean chkStatus(Context c) {
        final ConnectivityManager connMgr = (ConnectivityManager)c.getSystemService(Context.CONNECTIVITY_SERVICE);
        final android.net.NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        final android.net.NetworkInfo mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        return wifi.isConnectedOrConnecting ()||mobile.isConnectedOrConnecting ();
    }
}
