package com.example.vip.abdr_app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
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

public class login extends AppCompatActivity implements View.OnClickListener {
    localuser LU;TextView tv;
ImageView login;
     EditText username, etPass;

    ProgressDialog prgDialog;
    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        tv = (TextView)findViewById(R.id.passrecover);
        tv.setOnClickListener(this);
         LU=new localuser(this);

       username = (EditText)findViewById(R.id.username);
        etPass = (EditText)findViewById(R.id.password);
        login=(ImageView)findViewById(R.id.btnlogin);
        login.setOnClickListener(this);
        prgDialog = new ProgressDialog(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.passrecover:
                Intent intent = new Intent(login.this,recover.class);
                startActivity(intent);
                break;
            case R.id.btnlogin:
                prgDialog = ProgressDialog.show(this, "Wait Please...", "Logging You in...", false, true);
                prgDialog.setCancelable(false);
                prgDialog.setCanceledOnTouchOutside(false);
                String uname=username.getText().toString();
                String password=etPass.getText().toString();
                BackGround b = new BackGround();
                b.execute(uname, password);

                break;

        }

    }

    private void loguser(passenger p) {
        LU.storeData(p);
        LU.setLoggedInUser(true);
        startActivity(new Intent(this,userinfo.class));
    }
    class BackGround extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            String name = params[0];
            String password = params[1];
            String data="";
            int tmp;
            try {//192.168.56.1
                URL url = new URL("http://abdr.000webhostapp.com/login.php");
                String urlParams = "username="+name+"&password="+password;
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
            }}
        @Override
        protected void onPostExecute(String s) {
            prgDialog.dismiss();

            if (s.equals("false")) {
                Toast.makeText(login.this, "Wrong username or password", Toast.LENGTH_LONG).show();
            }
            else{
            try {
                JSONObject root = new JSONObject(s);
                JSONArray array = root.getJSONArray("user_data");
                JSONObject c=array.getJSONObject(0);
                loguser(new passenger(c.getString("first_name"), c.getString("last_name"), c.getString("city"), c.getString("email"),
                        c.getString("nationality"), c.getInt("phone"), c.getInt("id_number"), c.getString("username"),
                        c.getString("password"), c.getInt("P_ID"),c.getString("id_card_img")));
               startActivity(new Intent(login.this, home.class));
            } catch (JSONException e) {
                //Toast.makeText(login.this, "login"+e.getMessage(), Toast.LENGTH_LONG);

            }

        }
        }
    }
}
