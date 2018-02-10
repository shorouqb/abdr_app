package com.example.vip.abdr_app;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class home extends BaseActivity implements View.OnClickListener {
    ImageView newb;

    ProgressDialog prgDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        newb = (ImageView) findViewById(R.id.newb);
        newb.setOnClickListener(this);
        VideoView videoview = (VideoView) findViewById(R.id.videoView);
        Uri uri = Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.startv);
        videoview.setVideoURI(uri);

        videoview.requestFocus();
        videoview.start();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.newb:
                prgDialog=new ProgressDialog(this);

                prgDialog = ProgressDialog.show(this, "Wait Please...", "Loading your card...", false, true);
                prgDialog.setCancelable(false);
                prgDialog.setCanceledOnTouchOutside(false);
                BackGround b = new BackGround();
                b.execute(new localuser(this).getLoggedInUser().P_ID+"");

                break;
        }
    }

    class BackGround extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {

            int P_ID = Integer.parseInt(params[0]);

            String data="";
            int tmp;

            try {
                URL url = new URL("http://abdr.000webhostapp.com/usercard.php");
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

        @Override
        protected void onPostExecute(String s) {
            prgDialog.dismiss();
            if (s.equals("false")){
                Toast.makeText(home.this, "Failed to add card ", Toast.LENGTH_LONG).show();
            }else{
                try {
                    JSONObject root = new JSONObject(s);
                    JSONArray array = root.getJSONArray("user_data");
                    if(array.length()>0) {
                        startActivity(new Intent(home.this, booking.class));
                    }
                    else{
                        showAlert();
                    }
                } catch (JSONException e) {
                    //Toast.makeText(home.this, "home card "+e.getMessage(), Toast.LENGTH_LONG).show();

                }
            }
        }
    }
    public void showAlert(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Warning");
        builder.setMessage("Please fill credit card information to make new booking ..");

        builder.setCancelable(false);
        builder.setPositiveButton("Fill Now", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(home.this, payment.class));
            }
        });
        builder.setNegativeButton("Not Now", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }


}
