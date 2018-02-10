package com.example.vip.abdr_app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class rating extends BaseActivity  implements View.OnClickListener{
    int sid;
    String ref;
    TextView bref;
    EditText rev;
    RatingBar rate;
    ImageView sub;
    ProgressDialog prgDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);


        bref=(TextView)findViewById(R.id.ref);

        rev=(EditText) findViewById(R.id.review);
        rate=(RatingBar)findViewById(R.id.ratingBar);
        sub=(ImageView) findViewById(R.id.submit);
        sub.setOnClickListener(this);
        prgDialog = new ProgressDialog(this);

    }
    @Override
    protected void onStart(){
        super.onStart();
        ref=getIntent().getStringExtra("ref");
        bref.setText("Booking Reference: "+ref);

        sid=getIntent().getExtras().getInt("s_id");
    }
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submit:
                prgDialog = ProgressDialog.show(this, "Wait Please...", "Adding Your Review...", false, true);
                prgDialog.setCancelable(false);
                prgDialog.setCanceledOnTouchOutside(false);
                rating.BackGround b=new rating.BackGround();
                b.execute(sid+"",Float.toString(rate.getRating()),rev.getText().toString());

                break;
        }
    }

    class BackGround extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            int sid = Integer.parseInt(params[0]);
            float  rat = Float.parseFloat(params[1]);
            String rev = params[2];
            String data="";
            int tmp;

            try {//192.168.56.1
                URL url = new URL("http://abdr.000webhostapp.com/addrev.php");
                String urlParams = "sid="+sid+"&rat="+rat+"&rev="+rev;

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
                Toast.makeText(rating.this, "Rating not added", Toast.LENGTH_LONG).show();
            }

            else {

                Intent intent = new Intent(rating.this, mainbookings.class);
                startActivity(intent);
            }


        }
    }

}
