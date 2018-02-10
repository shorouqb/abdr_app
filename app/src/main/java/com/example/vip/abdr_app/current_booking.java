package com.example.vip.abdr_app;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

public class current_booking extends BaseActivity implements OnMapReadyCallback, View.OnClickListener {
    TextView status;
    TextView status1;
    TextView TestViewFR;
    TextView TestViewFR1;
    boolean nobooking;
    ImageButton next;
    Button driver_contact;
    int PID;
    String b_r;
    String date;
    String time;
    long phone;
    String stat;
    Double lat;
    Double lng;
    TextView textView_FT,duration;
    SupportMapFragment mapFragment;
    GoogleMap mGoogleMap;
    Polyline polyline;

    double p_lng;
    double p_lat;
    TimerTask timerTask;
    Timer timer;
    ProgressDialog prgDialog;
    Marker marker;
    Boolean check= false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_current_booking);

        PID = new localuser(this).getLoggedInUser().P_ID;
        prgDialog = new ProgressDialog(this);
        status1 = (TextView) findViewById(R.id.tv1);
        status1.setVisibility(View.INVISIBLE);
        duration = (TextView) findViewById(R.id.duration);
        duration.setVisibility(View.INVISIBLE);
        status = (TextView) findViewById(R.id.textViewstatus);
        status.setVisibility(View.INVISIBLE);
        TestViewFR1 = (TextView) findViewById(R.id.tv);
        TestViewFR1.setVisibility(View.INVISIBLE);
        TestViewFR = (TextView) findViewById(R.id.TestViewFR);
        TestViewFR.setVisibility(View.INVISIBLE);
        next = (ImageButton) findViewById(R.id.finish_btn);
        next.setOnClickListener(this);
        next.setVisibility(View.INVISIBLE);
        driver_contact = (Button) findViewById(R.id.contact_btn);
        driver_contact.setVisibility(View.INVISIBLE);
        textView_FT =(TextView) findViewById(R.id.textView_FT);
        textView_FT.setVisibility(View.INVISIBLE);
        if (googleServicesAvailable()) {

            // CHECK IF GPS enabled or not...
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                // notify user
                AlertDialog.Builder dialog = new AlertDialog.Builder(current_booking.this);
                dialog.setMessage("Location not enabled!");
                dialog.setPositiveButton("Open location settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                });
                dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Toast.makeText(current_booking.this, "Cant find your location because location is OFF", Toast.LENGTH_LONG).show();

                    }
                });
                dialog.show();
            }
            // initialize map fragment..
            mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.Fram);
            mapFragment.getMapAsync(this);
            mapFragment.getView().setVisibility(View.INVISIBLE);
        }
        prgDialog = ProgressDialog.show(this, "Wait Please...", "Loading Your Current Booking...", false, true);
        prgDialog.setCancelable(false);
        prgDialog.setCanceledOnTouchOutside(false);

        set_info();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;

        goToLocationZoom(21.563819, 39.170807, 10);


        PolylineOptions lineOptions =new PolylineOptions()
                .add(new LatLng(21.632784, 39.182421))
                .add(new LatLng(21.635307, 39.160893))
                .add(new LatLng(21.651630, 39.139687))
                .add(new LatLng(21.679345, 39.126101))
                .add(new LatLng(21.711056, 39.115830))
                .add(new LatLng(21.727371, 39.145982))
                .add(new LatLng(21.751896, 39.149647))
                .add(new LatLng(21.765837, 39.150109))
                .add(new LatLng(21.773773, 39.173434))
                .add(new LatLng(21.697189, 39.201840))
                .add(new LatLng(21.656025, 39.203853))
                .add(new LatLng(21.637758, 39.207720))
                .add(new LatLng(21.632784, 39.182421))
                .color(Color.GRAY)
                .width(4);
        mGoogleMap.addPolyline(lineOptions);

        //-------------------------------
        if(!nobooking) {
            timer = new Timer();

            timerTask = new TimerTask() {
                public void run() {
                    runOnUiThread(new Runnable() {

                        public void run() {
                            map_trak();
                        }
                    });
                }
            };

            timer.schedule(timerTask, 0, 2000);
        }
    }

    private String downloadUrl(String strUrl) throws IOException{
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while( ( line = br.readLine()) != null){
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        }catch(Exception e){
            //Toast.makeText(this, "Exception while downloading url" + e.toString(), Toast.LENGTH_LONG).show();
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    private class DownloadTask extends AsyncTask<String, Void, String>{

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try{
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            }catch(Exception e){
                //Toast.makeText(current_booking.this, "Background Task"+e.toString(), Toast.LENGTH_LONG).show();
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    /** A class to parse the Google Places in JSON format */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try{
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            }catch(Exception e){
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();
if(result.size()>0){
            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);
                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    String durations = point.get("duration");
                    durations = ((durations.substring(durations.indexOf("\"text\":\"") + 8, durations.indexOf("m") - 1))) + " Minute's Remaining";
                    duration.setText(durations);
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(8);
                lineOptions.color(Color.BLACK);
            }
            if (lineOptions != null) {
                // Drawing polyline in the Google Map for the i-th route
                if (polyline != null) {
                    polyline.remove();
                }
                polyline = mGoogleMap.addPolyline(lineOptions);
            }
        }
        }
    }

    private String getDirectionsUrl(LatLng origin,LatLng dest){

        // Origin of route
        String str_origin = "origin="+origin.latitude+","+origin.longitude;

        // Destination of route
        String str_dest = "destination="+dest.latitude+","+dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin+"&"+str_dest+"&"+sensor;

        //key=AIzaSyC5bY2Za7rMgAwyVr8HmJ1oj5YzPWZJnVA&
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/json?"+parameters;

        return url;
    }

    //**************************************************************************************************************


    public void set_info() {
        BackGround b = new BackGround();
        String err = null;
        String s = null;
        try {
            s = b.execute(PID + "").get();
        } catch (InterruptedException e) { e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();}
        prgDialog.dismiss(); //Toast.makeText(current_booking.this, s , Toast.LENGTH_LONG).show();
            if (s.equals("false")) {
                Toast.makeText(current_booking.this, "Error in getting current booking " , Toast.LENGTH_LONG).show();
            } else { try {
                    JSONObject root = new JSONObject(s);
                    JSONArray array = root.getJSONArray("user_data");
                    if (array.length() > 0) {
                        stat = array.getJSONObject(0).getString("status");
                        phone = array.getJSONObject(0).getLong("phone");
                        b_r = array.getJSONObject(0).getString("booking_reference");
                        date = array.getJSONObject(0).getString("date");
                        time = array.getJSONObject(0).getString("time");
                        p_lat = array.getJSONObject(0).getDouble("Latitude");
                        p_lng = array.getJSONObject(0).getDouble("Longitude");
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                        SimpleDateFormat tf = new SimpleDateFormat("HH:mm:ss");
                        Date c_date = df.parse(df.format(new Date()));
                        Date b_date = df.parse(date);
                        Date c_time = tf.parse(tf.format(new Date()));
                        Date b_time = tf.parse(time);
                        if (c_date.equals(b_date) && !stat.equals("Done") && !stat.equals("Reserved") && !stat.equals("Rated") && !stat.equals("Scanned")
                                && (b_time.equals(c_time) || c_time.after(b_time))) {
                            call(driver_contact, phone + "");
                            status.setText(stat);
                            TestViewFR.setText(b_r);
                            TestViewFR.setVisibility(View.VISIBLE);
                            TestViewFR1.setVisibility(View.VISIBLE);
                            status.setVisibility(View.VISIBLE);
                            status1.setVisibility(View.VISIBLE);
                            driver_contact.setVisibility(View.VISIBLE);
                            next.setVisibility(View.VISIBLE);
                            check = true;
                        }else {
                            check = false;
                            textView_FT.setText("your don't have any running booking at the moment ...");
                            textView_FT.setVisibility(View.VISIBLE);
                            mapFragment.getView().setVisibility(View.INVISIBLE);
                            duration.setVisibility(View.INVISIBLE);
                            nobooking = true;
                        }
                    }
                } catch (JSONException e) {
            e.printStackTrace();
            err = "Exception1: " + e.getMessage();
        } catch (ParseException e) {
            e.printStackTrace();
            err = "Exception2: " + e.getMessage();
        }
            }


        prgDialog.dismiss();


    }

    //****************************************************************************

    public void map_trak() {
        if(check) {
            if (stat.equals("Pickedup")) {
                // frame visiable for traking ...
                mapFragment.getView().setVisibility(View.VISIBLE);
                duration.setVisibility(View.VISIBLE);
                try {
                    //background get location og driver
                    BackGround_D_L b_D_L = new BackGround_D_L();
                    String s = null;
                    s = b_D_L.execute(b_r).get();
                    if (s.equals("false")) {
                        Toast.makeText(current_booking.this, "Wrong ....", Toast.LENGTH_LONG).show();
                    } else {
                        JSONObject root = new JSONObject(s);
                        JSONArray array = root.getJSONArray("user_data");
                        if (array.length()>0) {
                            JSONObject c = array.getJSONObject(0);
                            lat = c.getDouble("driverlat");
                            lng = c.getDouble("driverlong");
                            stat = c.getString("status");
                            status.setText(stat);
                            prgDialog.dismiss();
                        }}
                } catch (JSONException e) {
                    //Toast.makeText(current_booking.this, "ex 5 "+e.getMessage(), Toast.LENGTH_LONG);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                //------------------------------------
                // from to airport
                double airport_lat=21.692192;
                double airport_lng=39.165276;
                LatLng dest = new LatLng(airport_lat,airport_lng);
                LatLng origin = new LatLng(lat,lng);
                // Getting URL to the Google Directions API
                String url = getDirectionsUrl(origin, dest);
                DownloadTask downloadTask = new DownloadTask();
                // Start downloading json data from Google Directions API
                downloadTask.execute(url);
                setMarker(lat, lng);
            } else if (stat.equals("Arrived")) {
                // frame visiable for traking ...
                mapFragment.getView().setVisibility(View.GONE);
                duration.setVisibility(View.GONE);
                textView_FT.setVisibility(View.VISIBLE);
                textView_FT.setText("Your driver has arrived to the airport ..\n please wait for the payment withdrawal and your barcode ..");

            } else if (stat.equals("Scanned")) {
                Toast.makeText(current_booking.this, "Please go to your bookings to check the barcodes for your luggage.. booking is finish", Toast.LENGTH_LONG);
                if(timer !=null) {
                    timer.cancel();
                }
            }else {

                if(timer !=null) {
                    timer.cancel();
                    Toast.makeText(current_booking.this, "Wait until the luggage is picked up to start tracking", Toast.LENGTH_LONG).show();
                }}
        }
        else{
            if(timer !=null) {
                timer.cancel();
            }
        }

        }

    private void setMarker(double lat, double lng){
        if (marker != null){
            marker.remove();
        }
        MarkerOptions options = new MarkerOptions()
                .title("Driver here")
                .icon(BitmapDescriptorFactory.fromBitmap((resizeMapIcons("driver",70,70))))
                .position(new LatLng(lat, lng));

        marker = mGoogleMap.addMarker(options);
        //goToLocationZoom(lat,lng,15);
    }

    public Bitmap resizeMapIcons(String iconName, int width, int height){
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(),getResources().getIdentifier(iconName, "drawable", getPackageName()));
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return resizedBitmap;
    }
    //*********************************************************************************************************************

    public boolean googleServicesAvailable() {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int isAvailable = api.isGooglePlayServicesAvailable(this);
        if (isAvailable == ConnectionResult.SUCCESS) {
            return true;
        } else if (api.isUserResolvableError(isAvailable)) {
            Dialog dialog = api.getErrorDialog(this, isAvailable, 0);
            dialog.show();
        } else {
            Toast.makeText(this, "Cant connect to play services", Toast.LENGTH_LONG).show();
        }
        return false;
    }

    private void call(final Button btn, final String str) {
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", str, null)));
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.finish_btn:
                startActivity(new Intent(current_booking.this, mainbookings.class));
                break;
        }
    }

    private void goToLocationZoom(double lat, double lng, float zoom) {
        LatLng ll = new LatLng(lat, lng);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, zoom);
        mGoogleMap.moveCamera(update);

    }

    @Override
    protected void onStop() {
        super.onStop();
        if(timer !=null){
        timer.cancel();}
    }

    class BackGround extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {

            int P_ID = Integer.parseInt(params[0]);
            String data="";
            int tmp;
            try {
                URL url = new URL("http://abdr.000webhostapp.com/currentbooking.php");
                String urlParams = "P_ID="+P_ID;

                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setDoOutput(true);
                OutputStream os = httpURLConnection.getOutputStream();
                os.write(urlParams.getBytes());
                os.flush();
                os.close();

                InputStream is = httpURLConnection.getInputStream();
                while((tmp=is.read())!=-1){
                    data+= (char)tmp;
                }

                is.close();
                httpURLConnection.disconnect();

                return data;
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return "Exception: "+e.getMessage();
            } catch (IOException e) {
                e.printStackTrace();
                return "Exception: "+e.getMessage();
            }
        }

     }

    class BackGround_D_L extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            String ref = params[0];
            String data="";
            int tmp;

            try {
                URL url = new URL("http://abdr.000webhostapp.com/getdriverlonglat.php");
                String urlParams = "ref="+ref;

                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setDoOutput(true);
                OutputStream os = httpURLConnection.getOutputStream();
                os.write(urlParams.getBytes());
                os.flush();
                os.close();

                InputStream is = httpURLConnection.getInputStream();
                while((tmp=is.read())!=-1){
                    data+= (char)tmp;
                }

                is.close();
                httpURLConnection.disconnect();

                return data;
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return "Exception: "+e.getMessage();
            } catch (IOException e) {
                e.printStackTrace();
                return "Exception: "+e.getMessage();
            }
        }


    }
}

