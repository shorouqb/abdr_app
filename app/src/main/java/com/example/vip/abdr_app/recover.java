package com.example.vip.abdr_app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class recover extends AppCompatActivity implements View.OnClickListener {
    ImageView cancel,send;EditText email;ProgressDialog prgDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recover);
        email = (EditText)findViewById(R.id.email);
        send=(ImageView)findViewById(R.id.send);
        send.setOnClickListener(this);
        cancel=(ImageView)findViewById(R.id.cancel);
        cancel.setOnClickListener(this);
        prgDialog = new ProgressDialog(this);

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cancel:
                Intent intent = new Intent(recover.this,mainpage.class);
                startActivity(intent);
                break;
            case R.id.send:
                String em=email.getText().toString();
                if (em == "") {
                    Toast.makeText(recover.this, "Email must be filled out", Toast.LENGTH_LONG).show();

                }
                else{
                    int atpos = em.indexOf("@");
                    int dotpos = em.lastIndexOf(".");
                    if (atpos < 1 || dotpos < atpos + 2 || dotpos + 2 >= em.length()) {
                        Toast.makeText(recover.this, "Not a valid e-mail address", Toast.LENGTH_LONG).show();
                                           }
                    else{

                        prgDialog = ProgressDialog.show(this, "Wait Please...", "Sending Your Information...", false, true);
                        prgDialog.setCancelable(false);
                        prgDialog.setCanceledOnTouchOutside(false);
                        recover.BackGround b = new recover.BackGround();
                        b.execute(em);
                    }
                }

                break;

        }

    }



    class BackGround extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            String email = params[0];
            String data="";
            int tmp;
            try {//192.168.56.1
                URL url = new URL("http://abdr.000webhostapp.com/recoveremail.php");
                String urlParams = "email="+email+"&flag=p";
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
            if(s.equals("false")){
                Toast.makeText(recover.this, "Email not found", Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(recover.this, "Sent", Toast.LENGTH_LONG).show();
                Intent i = new Intent(recover.this, mainpage.class);
                startActivity(i);
            }
        }
    }
}

