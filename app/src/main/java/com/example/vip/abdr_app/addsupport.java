package com.example.vip.abdr_app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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

public class addsupport extends BaseActivity implements View.OnClickListener {
    ImageView submit,cans;
    EditText bref,title,com; ProgressDialog prgDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addsupport);
        submit=(ImageView)findViewById(R.id.submit);
        submit.setOnClickListener(this);
        bref = (EditText)findViewById(R.id.bookref);

        cans= (ImageView)findViewById(R.id.cans);
        cans.setOnClickListener(this);

        Intent i=getIntent();
        String ref=i.getStringExtra("ref");
        if (ref != null) {
            bref.setText(ref);
        }
        title = (EditText)findViewById(R.id.title);
        com = (EditText)findViewById(R.id.com);
        prgDialog = new ProgressDialog(this);

    }
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.submit:
                prgDialog = ProgressDialog.show(this, "Wait Please...", "Adding Support Case...", false, true);
                prgDialog.setCancelable(false);
                prgDialog.setCanceledOnTouchOutside(false);
                addsupport.BackGround b = new addsupport.BackGround();
                b.execute(bref.getText().toString(),title.getText().toString(),com.getText().toString());
                break;
            case R.id.cans:
                startActivity(new Intent(this,mainsupport.class));
                break;

        }

    }

    class BackGround extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            String ref = params[0];
            String tit = params[1];
            String desc = params[2];
            String data="";
            int tmp;

            try {
                URL url = new URL("http://abdr.000webhostapp.com/addcomplain.php");
                String urlParams = "ref="+ref+"&tit="+tit+"&desc="+desc;
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
            //Toast.makeText(addsupport.this, s, Toast.LENGTH_LONG).show();
            prgDialog.dismiss();
            if (s.equals("true")){

            Toast.makeText(addsupport.this, "Submited", Toast.LENGTH_LONG).show();
            Intent i = new Intent(addsupport.this, mainsupport.class);
            startActivity(i);
        }
          else{
              Toast.makeText(addsupport.this, "Failed to submit ", Toast.LENGTH_LONG).show();
          }
        }
    }

}
