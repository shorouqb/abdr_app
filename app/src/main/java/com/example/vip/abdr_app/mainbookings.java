package com.example.vip.abdr_app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
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

public class mainbookings extends BaseActivity {
    String []ref;
    String []state;
    int [] driver;
    int [] sid;
    String  [] time;
    String  [] date;
    double []fee;
    GridView gv;
    int PID;
    GridAdapterbooking ga=new GridAdapterbooking(this);
    ProgressDialog prgDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainbookings);

        PID=new localuser(this).getLoggedInUser().P_ID;
        prgDialog = new ProgressDialog(this);

    }
    @Override
    protected void onStart() {
        super.onStart();
        prgDialog = ProgressDialog.show(this, "Wait Please...", "Loading Your Bookings...", false, true);
        prgDialog.setCancelable(false);
        prgDialog.setCanceledOnTouchOutside(false);
        mainbookings.BackGround b = new mainbookings.BackGround();
        b.execute(PID + "");

    }

    class BackGround extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            int P_ID = Integer.parseInt(params[0]);
            String data="";
            int tmp;
            try {//192.168.56.1
                URL url = new URL("http://abdr.000webhostapp.com/showbookings.php");
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
            }}
        @Override
        protected void onPostExecute(String s) {
            String err=null;
            prgDialog.dismiss();
            if(s.equals("false")){
                Toast.makeText(mainbookings.this, "No bookings", Toast.LENGTH_LONG).show();}
            else {
                try {
                    JSONObject root = new JSONObject(s);
                    JSONArray array = root.getJSONArray("user_data");
                    sid=new int[array.length()];
                    ref =new String[array.length()];
                    state =new String[array.length()];
                    driver=new int[array.length()];
                    time=new String [array.length()];
                    date=new String [array.length()];
                    fee=new double[array.length()];
                    for (int i=0;i<array.length();i++){
                        sid[i]=array.getJSONObject(i).getInt("service_id");
                        ref[i] =array.getJSONObject(i).getString("booking_reference");
                        state[i] =array.getJSONObject(i).getString("status");
                        driver[i]=array.getJSONObject(i).getInt("D_ID");
                        time[i]=array.getJSONObject(i).getString("time");
                        date[i]=array.getJSONObject(i).getString("date");
                        fee[i]=array.getJSONObject(i).getDouble("service_fee");
                    }
                    gv=(GridView)findViewById(R.id.bookinggrid);
                    ga=new GridAdapterbooking(mainbookings.this,sid,ref,state,driver,time,date,fee);
                    gv.setAdapter(ga);
                    gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                            Intent i=new Intent(mainbookings.this,bookinginfo.class);
                            i.putExtra("service_id",sid);
                            i.putExtra("booking_reference",ref);
                            i.putExtra("status",state);
                            i.putExtra("D_ID",driver);
                            i.putExtra("time",time);
                            i.putExtra("date",date);
                            i.putExtra("service_fee",fee);
                            i.putExtra("pos",position);
                            startActivity(i);}});
                } catch (JSONException e) {
                    //Toast.makeText(mainbookings.this, "booking load"+e.getMessage(), Toast.LENGTH_LONG).show();

                }
            }


        }
    }

}
