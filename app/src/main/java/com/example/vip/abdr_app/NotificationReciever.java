package com.example.vip.abdr_app;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

public class NotificationReciever extends Service {
    Context c=null;
    String s;

        private static final String LOG_TAG = "NotificationReciever";

        @Override
        public void onCreate() {
            super.onCreate();

        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            this.c = getApplicationContext();
            Timer timer = new Timer ();

            inertnetcheck in = new inertnetcheck();
            if (in.chkStatus(c)) {
                if(new localuser(c).getIfLoggedInUser()){
                TimerTask hourlyTask1 = new TimerTask() {

                    @Override
                    public void run() {

                        BackGround1 b = new BackGround1();
                        b.execute();
                    }
                };

                timer.schedule(hourlyTask1, 0l, 10000 * 60 * 60);//15000*60*60
            }
            }
            return START_STICKY;
        }


        @Override
        public void onDestroy() {
            super.onDestroy();
            Log.i(LOG_TAG, "In onDestroy");
        }

        @Override
        public IBinder onBind(Intent intent) {
            // Used only in case of bound services.
            return null;
        }
    class BackGround1 extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {

            String data = "";
            int tmp;

            try {//192.168.56.1
                URL url = new URL("http://abdr.000webhostapp.com/usernoti.php");
                String urlParams = "pid=" + new localuser(c).getLoggedInUser().P_ID;

                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setDoOutput(true);
                OutputStream os = httpURLConnection.getOutputStream();
                os.write(urlParams.getBytes());
                os.flush();
                os.close();

                InputStream is = httpURLConnection.getInputStream();
                while ((tmp = is.read()) != -1) {
                    data += (char) tmp;
                }

                is.close();
                httpURLConnection.disconnect();

                return data;
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return "Exception: " + e.getMessage();
            } catch (IOException e) {
                e.printStackTrace();
                return "Exception: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String s) {

            if (s.equals("false")) {
                Toast.makeText(c, "Not found", Toast.LENGTH_LONG).show();
            } else {

                JSONObject root = null;
                try {
                    root = new JSONObject(s);
                    JSONArray array = root.getJSONArray("user_data");
                    if (root.has("user_data")){
                    String []st=new localuser(c).getstat(array.getJSONObject(0).getString("booking_reference")).split(",");
                    if(st[1].equals(array.getJSONObject(0).getString("booking_reference"))) {
                        if (!array.getJSONObject(0).getString("status").equals(st[0])) {
                            //set new status
                            new localuser(c).setstat(array.getJSONObject(0).getString("status"), array.getJSONObject(0).getString("booking_reference"));
                            Intent intent1 = new Intent(c, current_booking.class);
                            intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                            PendingIntent pendingIntent = PendingIntent.getActivity(c, 100, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
                            NotificationManager notificationManager = (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
                            NotificationCompat.Builder builder = new NotificationCompat.Builder(c).
                                    setSmallIcon(R.drawable.logo).
                                    setContentIntent(pendingIntent).
                                    setContentText("booking " + array.getJSONObject(0).getString("booking_reference") +
                                            " status is changed to " + array.getJSONObject(0).getString("status")).
                                    setContentTitle("change in booking").
                                    setAutoCancel(true)
                                    .setSound(alarmSound);
                            notificationManager.notify(100, builder.build());
                        }
                    }
                }
                } catch (JSONException e) {
                    //Toast.makeText(c, "user service "+e.getMessage(), Toast.LENGTH_LONG).show();

                }


            }


        }
    }
    }