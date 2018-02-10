package com.example.vip.abdr_app;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
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

public class payment extends BaseActivity implements View.OnClickListener {

    EditText name , card_number,ccv,exd,ct;
    Spinner card_type;
    ImageButton done,can;


    ProgressDialog prgDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        name=(EditText)findViewById(R.id.name);
        card_number=(EditText)findViewById(R.id.cn);
        ccv=(EditText)findViewById(R.id.ccv);
        exd=(EditText)findViewById(R.id.ex);
        ct=(EditText)findViewById(R.id.ct);
        card_type=(Spinner)findViewById(R.id.spinner);
        done=(ImageButton)findViewById(R.id.don);
        done.setOnClickListener(this);
        can=(ImageButton)findViewById(R.id.can);
        can.setOnClickListener(this);
        prgDialog=new ProgressDialog(this);

        prgDialog = ProgressDialog.show(this, "Wait Please...", "Loading your card...", false, true);
        prgDialog.setCancelable(false);
        prgDialog.setCanceledOnTouchOutside(false);
        payment.BackGround b = new payment.BackGround();
        // bring card info.
        b.execute(new localuser(this).getLoggedInUser().P_ID+"");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.don:
                if(name.getText().toString() != " "&& card_number.getText().toString()!= " "&&
                    ccv.getText().toString()!= " "&& exd.getText().toString()!= " "&&
                        card_type.getSelectedItem().toString()!= "SELECT"){
                prgDialog = ProgressDialog.show(this, "Wait Please...", "adding your card...", false, true);
                prgDialog.setCancelable(false);
                prgDialog.setCanceledOnTouchOutside(false);
                payment.BackGround1 b = new payment.BackGround1();
                b.execute(name.getText().toString(),card_number.getText().toString(),
                        ccv.getText().toString(),exd.getText().toString(),card_type.getSelectedItem().toString(),
                        new localuser(this).getLoggedInUser().P_ID+"");
                }else{
                    showAlert();
                }
                break;
            case R.id.can:
                super.onBackPressed();
                break;
        }
    }

    public void showAlert(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Warning");
        builder.setMessage("Please you have to fill all information about your cade ..");

        builder.setCancelable(false);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });


        AlertDialog dialog = builder.create();
        dialog.show();

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
//                Toast.makeText(register.this, urlParams, Toast.LENGTH_LONG).show();
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
                Toast.makeText(payment.this, "Failed to add card ", Toast.LENGTH_LONG).show();
            }else{
                try {
                    JSONObject root = new JSONObject(s);
                    JSONArray array = root.getJSONArray("user_data");
                    if(array.length()>0) {
                        JSONObject c = array.getJSONObject(0);
                        name.setText(c.getString("credit_card_holder_name"));
                        ccv.setText(c.getString("CCV"));
                        exd.setText(c.getString("credit_card_expriy_date"));
                        card_number.setText(c.getString("credit_card_number"));
                        ct.setText(c.getString("credit_card_type"));
                        done.setVisibility(View.GONE);
                        can.setVisibility(View.GONE);
                        card_type.setVisibility(View.GONE);
                        name.setEnabled(false);
                        ccv.setEnabled(false);
                        exd.setEnabled(false);
                        ct.setEnabled(false);
                        card_number.setEnabled(false);
                    }
                    else{
                        ct.setVisibility(View.GONE);
                    }
                } catch (JSONException e) {
                    //Toast.makeText(payment.this, "payment card "+e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    //***********************************
    class BackGround1 extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {

            String name = params[0];
            String card_number = params[1];
            String ccv = params[2];
            String exd = params[3];
            String card_type = params[4];
            int P_ID = Integer.parseInt(params[5]);
            String data="";
            int tmp;
            try {
                URL url = new URL("http://abdr.000webhostapp.com/addusercard.php");
                String urlParams = "P_ID="+P_ID+"&type="+card_type+"&num="+card_number+"&name="+name+"&ccv="+ccv+"&exd="+exd;
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
                Toast.makeText(payment.this, "Failed to add card ", Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(payment.this, "Card added ", Toast.LENGTH_LONG).show();
            }
        }
    }

}
