package com.example.vip.abdr_app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class Location extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener {
    booking_Info bF ;
    double latitude = -1.0,longitude=-1.0;
    String Toatal_Address;

    ImageButton search;
    EditText et;
    GoogleMap mGoogleMap;
    Marker marker;

    ImageView next ;
    double jeddah_lat =21.543486;
    double jeddah_lng=39.172989;

    ProgressDialog prgDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (googleServicesAvailable()) {

            setContentView(R.layout.activity_location);
            // CHECK IF GPS enabled or not...
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                // notify user
                AlertDialog.Builder dialog = new AlertDialog.Builder(Location.this);
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
                        Toast.makeText(Location.this, "Cant find your location because location is OFF", Toast.LENGTH_LONG).show();

                    }
                });
                dialog.show();
            }
            // initialize map fragment..
            MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapFragment);
            mapFragment.getMapAsync(this);

            prgDialog = new ProgressDialog(this);
            prgDialog.setCancelable(false);
            prgDialog.setCanceledOnTouchOutside(false);
        }

        Intent i = getIntent();
        bF= (booking_Info) i.getSerializableExtra("bookingInfo");
        et = (EditText) findViewById(R.id.editTextSearch);
        search = (ImageButton) findViewById(R.id.Search_btn);
        search.setOnClickListener(this);
        next =(ImageView) findViewById(R.id.next_btn);
        next.setOnClickListener(this);
    }

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

    public void onMapReady(GoogleMap googleMap) {

        mGoogleMap = googleMap;

        // current location icon
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mGoogleMap.setMyLocationEnabled(true);

        // zoom in to jeddah in map
        goToLocationZoom(jeddah_lat,jeddah_lng,10);

        if(mGoogleMap != null) {

            mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    try {
                        Geocoder gc = new Geocoder(Location.this);
                        double lat = latLng.latitude;
                        double lng = latLng.longitude;
                        List<Address> list = gc.getFromLocation(lat, lng, 1);

                        if (list.size() > 0) {
                            Address address = list.get(0);
                            latitude =address.getLatitude();
                            longitude =address.getLongitude();
                            Toatal_Address=address.getCountryCode()+" , "+address.getLocality() + " , " + address.getFeatureName();
                            setMarker(address.getLocality(),address.getFeatureName(), lat, lng);

                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            //--------------------------------------------------------------------------------------
            mGoogleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                @Override
                public void onMarkerDragStart(Marker marker) {
                }

                @Override
                public void onMarkerDrag(Marker marker) {
                }

                @Override
                public void onMarkerDragEnd(Marker marker) {
                    try {
                        Geocoder gc = new Geocoder(Location.this);
                        LatLng ll = marker.getPosition();
                        double lat = ll.latitude;
                        double lng = ll.longitude;
                        List<Address> list = gc.getFromLocation(lat, lng, 1);
                        if (list.size() > 0) {
                            Address address = list.get(0);
                            latitude =address.getLatitude();
                            longitude =address.getLongitude();
                            Toatal_Address=address.getCountryCode()+" , "+address.getLocality() + " , " + address.getFeatureName();
                            setMarker(address.getLocality(),address.getFeatureName(), lat, lng);

                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            //--------------------------------------------------------------------------------------
            mGoogleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                @Override
                public View getInfoWindow(Marker marker) {
                    return null;
                }

                @Override
                public View getInfoContents(Marker marker) {
                    View v = getLayoutInflater().inflate(R.layout.marker_info, null);

                    TextView tvLocality = (TextView) v.findViewById(R.id.tv_locality);
                    TextView tvLat = (TextView) v.findViewById(R.id.tv_lat);
                    TextView tvLng = (TextView) v.findViewById(R.id.tv_lng);
                    TextView tvSnippet = (TextView) v.findViewById(R.id.tv_snippet);

                    LatLng ll = marker.getPosition();
                    tvLocality.setText(marker.getTitle());
                    tvLat.setText("Latitude: " + ll.latitude);
                    tvLng.setText("Longitude: " + ll.longitude);
                    tvSnippet.setText(marker.getSnippet());

                    return v;
                }
            });
        }


    }

    private void setMarker(String tital ,String snip, double lat, double lng){
        if (marker != null){
            marker.remove();
        }
        MarkerOptions options = new MarkerOptions()
                .title(tital)
                .snippet(snip)
                .draggable(true)
                .icon(BitmapDescriptorFactory.fromBitmap((resizeMapIcons("man",80,80))))
                .position(new LatLng(lat, lng));

        marker = mGoogleMap.addMarker(options);

    }


    public Bitmap resizeMapIcons(String iconName, int width, int height){
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(),getResources().getIdentifier(iconName, "drawable", getPackageName()));
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return resizedBitmap;
    }

    private void goToLocationZoom(double lat, double lng, float zoom) {
        LatLng ll = new LatLng(lat, lng);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, zoom);
        mGoogleMap.moveCamera(update);

    }

    public void geoLocate()  {
        try {
            String location = et.getText().toString();

            Geocoder gc = new Geocoder(this);
            List<Address> list = gc.getFromLocationName(location, 1);
            if (list.size()>0){
                Address address = list.get(0);
                double lat = address.getLatitude();
                double lng = address.getLongitude();
                goToLocationZoom(lat, lng, 15);
                latitude =address.getLatitude();
                longitude =address.getLongitude();
                Toatal_Address=address.getCountryCode()+" , "+address.getLocality() + " , " + address.getFeatureName();
                setMarker(address.getLocality(),address.getFeatureName(), lat, lng);
            }else{
                Toast.makeText(this, "Cant find "+location+" try again ", Toast.LENGTH_LONG).show();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.Search_btn:
                geoLocate();
                break;
            case R.id.next_btn:
                if(Toatal_Address != null && latitude != -1.0 && longitude != -1.0 && marker != null){
                    bF.setLongitude(longitude);
                    bF.setLatitude(latitude);
                    bF.setAddress(Toatal_Address);
                    Location.BackGroundD bD = new Location.BackGroundD(this);
                    bD.execute(bF.getLongitude(),bF.getLongitude());

                }
                else{

                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
                    builder.setTitle("Warning");
                    builder.setMessage("Please you have to specify location using marker ..");
                    builder.setCancelable(false);
                    builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });

                    android.support.v7.app.AlertDialog dialog = builder.create();
                    dialog.show();
                }
                break;
        }

    }

    class BackGroundD extends AsyncTask<Object, Void, String > {

        double Latitude;
        double Longitude;
        double airport_lat=21.670233;
        double airport_lng=39.150577999999996;
        String result_in_kms = "";

        private Location Lo;

        public BackGroundD(Location Lo) {
            this.Lo = Lo;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            prgDialog = ProgressDialog.show(Location.this, "Wait Please...","", false, true);
            prgDialog.setCancelable(false);
            prgDialog.setCanceledOnTouchOutside(false);
        }

        @Override
        protected String doInBackground(Object... params) {
            Latitude=(double)params[0];
            Longitude=(double)params[1];
            //key=AIzaSyC5bY2Za7rMgAwyVr8HmJ1oj5YzPWZJnVA&
            //-----------------------------------------------------------------
            String url = "http://maps.google.com/maps/api/directions/xml?origin="
                    + latitude + "," + longitude + "&destination=" + airport_lat
                    + "," + airport_lng + "&sensor=false&units=metric";
            String tag[] = { "text" };
            HttpResponse response = null;
            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpContext localContext = new BasicHttpContext();
                HttpPost httpPost = new HttpPost(url);
                response = httpClient.execute(httpPost, localContext);
                InputStream is = response.getEntity().getContent();
                DocumentBuilder builder = DocumentBuilderFactory.newInstance()
                        .newDocumentBuilder();
                Document doc = builder.parse(is);
                if (doc != null) {
                    NodeList nl;
                    ArrayList args = new ArrayList();
                    for (String s : tag) {
                        nl = doc.getElementsByTagName(s);
                        if (nl.getLength() > 0) {
                            Node node = nl.item(nl.getLength() - 1);
                            args.add(node.getTextContent());
                        } else {
                            args.add(" - ");
                        }
                    }
                    result_in_kms = String.format("%s", args.get(0));
                }
            } catch (Exception e) {
                e.printStackTrace();}
            return result_in_kms;}
        @Override
        protected void onPostExecute(String s) {
            prgDialog.dismiss();
            Lo.bF.setDistance(s);
            Intent in=new Intent(Location.this,booking_TLP.class);
            in.putExtra("bookingInfo" , bF);
            startActivity(in);
        }
    }



}
