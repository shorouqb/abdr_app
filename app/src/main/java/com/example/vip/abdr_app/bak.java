package com.example.vip.abdr_app;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import java.util.List;


public class bak extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent arg1) {
        if (isAppRunning(context)){
            int[] type = {ConnectivityManager.TYPE_MOBILE, ConnectivityManager.TYPE_WIFI};
            if (newtworkconn(context, type)) {
                return;
            } else {

                Toast.makeText(context, "No internet Connection", Toast.LENGTH_LONG).show();
            }
        }
    }
    protected boolean isAppRunning(Context context){
        String activity = context.getClass().getName();
        ActivityManager activityManager = (ActivityManager)context.
                getSystemService(Context.ACTIVITY_SERVICE);

        List<ActivityManager.RunningTaskInfo> tasks = activityManager.
                getRunningTasks(Integer.MAX_VALUE);

        for(ActivityManager.RunningTaskInfo task : tasks){
            if(activity.equals(task.baseActivity.getClassName())){
                return true;
            }
        }
        return false;
    }
    private boolean newtworkconn(Context context,int[]type){
        try {


            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            for (int typen : type) {
                NetworkInfo ni = cm.getNetworkInfo(typen);
                if (ni != null && ni.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }catch (Exception e){
            return false;
        }
        return false;
    }
}
