package com.example.vip.abdr_app;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class bookinginfo extends BaseActivity implements View.OnClickListener{
    String ref;
    String state;
    int driver;
    int id;
    Date dateObj=null,dateObj1=null;
    String time;
    String date;
    double fee;
    int pos;
    Intent intent;
    TextView status,time_,date_,fees,finfo,minfo,linfo,tv;
    ImageButton rat,bar,can,sup,em;
    Button dri;
String msg="";

    ProgressDialog prgDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookinginfo);
        rat = (ImageButton) findViewById(R.id.rating);
        rat.setOnClickListener(this);

        bar = (ImageButton) findViewById(R.id.barcode);
        bar.setOnClickListener(this);

        can = (ImageButton) findViewById(R.id.cancel);
        can.setOnClickListener(this);

        sup = (ImageButton) findViewById(R.id.support);
        sup.setOnClickListener(this);

        em = (ImageButton) findViewById(R.id.email);
        em.setOnClickListener(this);

        dri = (Button) findViewById(R.id.driver);
        dri.setOnClickListener(this);
        prgDialog = new ProgressDialog(this);
        // Set Cancelable as False

        prgDialog.onBackPressed();
        finfo = (TextView) findViewById(R.id.flightinfo);
        minfo = (TextView) findViewById(R.id.passengersinfo);
        linfo = (TextView) findViewById(R.id.luggageinfo);
    }
    @Override
    protected void onStart(){
        super.onStart();
        // info from query
        prgDialog = ProgressDialog.show(this, "Wait Please...", "Loading Booking...", false, true);
        prgDialog.setCancelable(false);
        prgDialog.setCanceledOnTouchOutside(false);
        Intent i = getIntent();

        pos=i.getExtras().getInt("pos");
        ref=i.getStringArrayExtra("booking_reference")[pos];
        state=i.getStringArrayExtra("status")[pos];
        driver=i.getIntArrayExtra("D_ID")[pos];
        id=i.getIntArrayExtra("service_id")[pos];
        time=i.getStringArrayExtra("time")[pos];
        date=i.getStringArrayExtra("date")[pos];
        fee=i.getDoubleArrayExtra("service_fee")[pos];

        bookinginfo.BackGround2 bb=new bookinginfo.BackGround2(this);
        bb.execute(ref);
        status=(TextView)findViewById(R.id.status);
        time_=(TextView)findViewById(R.id.time);
        date_=(TextView)findViewById(R.id.date);
        fees=(TextView)findViewById(R.id.fee);
        status.setText(state);
        time_.setText(time);
        date_.setText(date);
        fees.setText(fee+" SAR");
        tv = (TextView)findViewById(R.id.priceD);
        tv.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rating:
                if(state.equals("Rated")){

                    Toast.makeText(bookinginfo.this,"You rated this booking thanks", Toast.LENGTH_LONG).show();
                }
                else if(!state.equals("Done")){

                    Toast.makeText(bookinginfo.this,"You cannot add rating now booking is not done", Toast.LENGTH_LONG).show();
                }
                else {
                    intent = new Intent(bookinginfo.this, rating.class);
                    intent.putExtra("s_id", id);
                    intent.putExtra("ref", ref);
                    startActivity(intent);
                }
                break;
            case R.id.cancel:
                try {
                    SimpleDateFormat curFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    dateObj = curFormater.parse(date+" "+time);
                    String d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
                    dateObj1 = curFormater.parse(d);
                } catch (ParseException e) {
                    //Toast.makeText(bookinginfo.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
                long diff = dateObj.getTime() - dateObj1.getTime();

                int dayCount = (int)(diff/(60*60*1000));
                if (dayCount<0){
                    Toast.makeText(bookinginfo.this, "This booking is done cannot cancel", Toast.LENGTH_LONG).show();
                }
                else if (dayCount<24&&dayCount>0){
                    Toast.makeText(bookinginfo.this, "You cannot cancel now to late", Toast.LENGTH_LONG).show();
                }
                else {
                    prgDialog = ProgressDialog.show(this, "Wait Please...", "Deleting Booking...", false, true);
                    prgDialog.setCancelable(false);
                    prgDialog.setCanceledOnTouchOutside(false);
                    bookinginfo.BackGround b = new bookinginfo.BackGround();
                    b.execute(id + "");
                }
                break;
            case R.id.driver:
                prgDialog = ProgressDialog.show(this, "Wait Please...", "Getting Driver Information's...", false, true);
                prgDialog.setCancelable(false);
                prgDialog.setCanceledOnTouchOutside(false);
                bookinginfo.BackGround1 bb=new bookinginfo.BackGround1();
                bb.execute(driver+"");
                break;
            case R.id.barcode:
                if(!state.equals("Done")&&!state.equals("Rated") && !state.equals("Scanned")){
                    Toast.makeText(bookinginfo.this, "You cannot see barcode's yet booking is not done", Toast.LENGTH_LONG).show();
                }
                else {
                    intent = new Intent(bookinginfo.this, barcodes.class);
                    intent.putExtra("ref", ref);
                    startActivity(intent);
                }
                break;
            case R.id.support:
                    intent = new Intent(bookinginfo.this, addsupport.class);
                    intent.putExtra("ref", ref);
                    startActivity(intent);

                break;
            case R.id.email:
                prgDialog = ProgressDialog.show(this, "Wait Please...", "Sending Booking Information's...", false, true);
                prgDialog.setCancelable(false);
                prgDialog.setCanceledOnTouchOutside(false);
                bookinginfo.EmailBackGround bbb=new bookinginfo.EmailBackGround();
                bbb.execute(ref,new localuser(this).getLoggedInUser().P_ID+"");

                break;
            case R.id.priceD:
                AlertDialog.Builder builder = new AlertDialog.Builder(bookinginfo.this);
                builder.setMessage(msg);
                builder.setTitle("Price Details");
                builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
                break;

        }

    }
    class BackGround extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            int sid = Integer.parseInt(params[0]);
            String data="";
            int tmp;

            try {
                URL url = new URL("http://abdr.000webhostapp.com/deletebooking.php");
                String urlParams = "sid="+sid;

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
            String err=null;
            prgDialog.dismiss();
            if(s.equals("false")){
                Toast.makeText(bookinginfo.this, "Error in deleting ", Toast.LENGTH_LONG).show();
            }


            else {
                Intent i = new Intent(bookinginfo.this, mainbookings.class);
                startActivity(i);
            }


        }
    }

    class BackGround1 extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            int did = Integer.parseInt(params[0]);
            String data="";
            int tmp;
            try {
                URL url = new URL("http://abdr.000webhostapp.com/driverinfo.php");
                String urlParams = "D_ID="+did;
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
                Toast.makeText(bookinginfo.this, "No driver found", Toast.LENGTH_LONG).show();
            }
            else {
                JSONObject root = null;
                try {
                    root = new JSONObject(s);
                    JSONArray array = root.getJSONArray("user_data");
                    JSONObject c=array.getJSONObject(0);
                    AlertDialog.Builder builder = new AlertDialog.Builder(bookinginfo.this);
                    builder.setMessage("Driver Name: "+c.getString("name")+"\n"+"Driver Nationality: "+c.getString("nationality")
                            +"\n"+"Car Brand: "+c.getString("car_brand")+"\n"+"Car Color: "+c.getString("car_color")
                            +"\n"+"Car Plate: "+c.getString("plate_number"));
                    builder.setTitle("Driver informations");
                    WebView wv = new WebView(bookinginfo.this);
                    String data="<div align='center'> <img src='"+c.getString("photo")+"' style='width:100px; height:100px;' /></div>";
                    wv.loadData(data, "text/html", "utf-8");
                    wv.setWebViewClient(new WebViewClient() {
                        @Override
                        public boolean shouldOverrideUrlLoading(WebView view, String url) {
                            view.loadUrl(url);

                            return true;
                        }
                    });

                    builder.setView(wv);
                    builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                } catch (JSONException e) {
                    //Toast.makeText(bookinginfo.this, "driver info "+e.getMessage(), Toast.LENGTH_LONG).show();
                }


            }


        }


    }
    class BackGround2 extends AsyncTask<String, String, String> {
private bookinginfo bi;
        public BackGround2(bookinginfo b){
           this.bi=b;
        }
        @Override
        protected String doInBackground(String... params) {
            String  refer = params[0];
            String data="";
            int tmp;

            try {
                URL url = new URL("http://abdr.000webhostapp.com/showbookinginfo.php");
                String urlParams = "ref="+refer;

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
            String err=null;
            prgDialog.dismiss();
            if(s.equals("false")){
                Toast.makeText(bookinginfo.this, "Error in loading booking ", Toast.LENGTH_LONG).show();
            }
            else {
                JSONArray array;
                String info="";
                String info1;
                String info2="";
                String info3;
                JSONObject root;
                try {
                    //flight
                 root = new JSONObject(s);
                 array = root.getJSONArray("user_data");
                    if(array.length()>0){
                    for (int i=0;i<array.length();i++){
                    info="Flight booking reference: "+array.getJSONObject(i).getString("booking_refrence")+"\n";
                    info+="Departing city: "+array.getJSONObject(i).getString("departure_city")+"\n";
                    info+="Arrival city: "+array.getJSONObject(i).getString("arrival_city")+"\n";
                    info+="Flight time: "+array.getJSONObject(i).getString("flight_time")+"\n";
                    info+="Flight date: "+array.getJSONObject(i).getString("flight_date")+"\n";
                    }
                        finfo.setText(info);
                    }
                    else{
                        finfo.setText("No flight found");
                    }
                    //luggage
                    array = root.getJSONArray("user_data1");
                    if(array.length()>0){
                        info1="Total luggage count: "+array.length()+"\n";
                        for (int i=0;i<array.length();i++){
                            info1+="Luggage brand: "+array.getJSONObject(i).getString("L_brand")+"\n";
                            info1+="Luggage color: "+array.getJSONObject(i).getString("L_color")+"\n";
                            info1+="Luggage description: "+array.getJSONObject(i).getString("L_description")+"\n";
                            info1+="Fragile luggage: "+array.getJSONObject(i).getString("fragile")+"\n";
                            info1+="Wrap luggage: "+array.getJSONObject(i).getString("wrap")+"\n\n";
                        }
                        linfo.setText(info1);
                    }
                    else{
                        linfo.setText("No luggage found");
                    }
                    //family
                    array = root.getJSONArray("user_data2");
                    if(array.length()>0){
                        for (int i=0;i<array.length();i++){
                            info2 +="Member name: "+array.getJSONObject(i).getString("name")+"\n";
                        }
                        minfo.setText(info2);
                    }
                    else{
                        minfo.setText("No family found");
                    }
                    //price
                    array = root.getJSONArray("user_data3");
                    if(array.length()>0){
                        info3="Price Details: \n\n";
                        for (int i=0;i<array.length();i++){
                            info3+="Luggage Price: "+array.getJSONObject(i).getString("luggage_price")+" SAR\n";
                            info3+="Distance Price: "+array.getJSONObject(i).getString("distance_price")+" SAR\n";
                            info3+="Wrapping Price: X"+((int)Double.parseDouble(array.getJSONObject(i).getString("wrapping_price"))/30)+
                                    " bag "+" "+array.getJSONObject(i).getString("wrapping_price")+" SAR\n";
                            info3+="Extra Price: "+array.getJSONObject(i).getString("extra_price")+" SAR\n";
                            info3+="Reason For Extra Price: "+array.getJSONObject(i).getString("why_extra")+"\n\n";
                            info3+="Total Price: "+fee+" SAR\n";
                        }
                       bi.msg=info3;
                    }
                    else{
                        info3="No Price";
                        bi.msg=info3;
                    }


                } catch (JSONException e) {
                   // Toast.makeText(bookinginfo.this, "booking info "+e.getMessage(), Toast.LENGTH_LONG).show();

                }

            }
        }
    }

    class EmailBackGround extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            String r=params[0];
            int pid = Integer.parseInt(params[1]);
            String data="";
            int tmp;

            try {//192.168.56.1
                URL url = new URL("http://abdr.000webhostapp.com/emailbooking.php");
                String urlParams = "P_Id="+pid+"&b_refernce="+r+"&change=no"+"&user=p"+"&d_Id= ";

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
            if(s.equals("true")){
                Toast.makeText(bookinginfo.this, "Sent", Toast.LENGTH_LONG).show();
            }
            else{
                Toast.makeText(bookinginfo.this, "Not sent", Toast.LENGTH_LONG).show();
            }
        }


    }
}
