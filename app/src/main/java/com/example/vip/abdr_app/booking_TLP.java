package com.example.vip.abdr_app;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
public class booking_TLP extends BaseActivity implements View.OnClickListener {
    Spinner time_option ;
    booking_Info bF ;
    ImageView done;
    TextView price , address ,p_detail;
    List<String> times_list =  new ArrayList<String>();
    String f_date ,distance;
    Double total_pri=0.0,dis_price ,lug_pric , w_price;
    localuser LU;
    ProgressDialog prgDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking__tlp);

        Intent i = getIntent();
        bF= (booking_Info) i.getSerializableExtra("bookingInfo");

        done= (ImageView)findViewById(R.id.done_btn);
        done.setOnClickListener(this);

        time_option = (Spinner)findViewById(R.id.time);

        price =(TextView) findViewById(R.id.textViewprice);

        address= (TextView) findViewById(R.id.textViewAddress);

        p_detail= (TextView) findViewById(R.id.textViewp_detail);
        p_detail.setOnClickListener(this);

        LU=new localuser(this);

        prgDialog = new ProgressDialog(this);
        prgDialog.setCancelable(false);
        prgDialog.setCanceledOnTouchOutside(false);
        //-----------------------------------------------------------------------------------------------
        // time options.....
        try {
            times_list.add("Select booking time");
            f_date = bF.flight_date +" "+ bF.getflight_time();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date d = null;
            d = df.parse(f_date);
            Calendar cal = Calendar.getInstance();
            cal.setTime(d);
            cal.add(Calendar.HOUR, -2);
            String end_Rtime = cal.getTime()+"";
            cal.add(Calendar.HOUR, -3);
            String newTime ;
            newTime = df.format(cal.getTime());
            times_list.add(newTime);
            while(!cal.getTime().toString().equals(end_Rtime)){
                cal.add(Calendar.MINUTE, 30);
                newTime = df.format(cal.getTime());
                times_list.add(newTime);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //-----------------------------------------------------------------------------------------------
        // add time options to Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, times_list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        time_option.setAdapter(adapter);
        //-----------------------------------------------------------------------------------------------

        address.setText(bF.getAddress());
        distance= bF.getDistance();
        total_pri=display_price();
        price.setText(total_pri+"SAR");
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.done_btn:
                if(time_option.getSelectedItem().toString().equals("Select booking time") || total_pri == 0.0 ){

                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Warning");
                    builder.setMessage("Please you have to Choose time to pickup your luggage and drop your current location ..");
                    builder.setCancelable(false);
                    builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();

                }
                else{
                    showAlert();
                }
                break;

            case R.id.textViewp_detail:
                showpriceAlert();
                break;

        }
    }

    //----------------------------------------------------------------------------------------------------
    // Calculate price .....
    public Double display_price(){
        String  dis = (distance.replaceAll("[\\s+a-zA-Z :]",""));
        Double distance = Double.parseDouble(dis);
        int W_count = 0 ;
        String[][]Lug_info = bF.getLuggage();
        for(int i = 0 ; i < Lug_info.length;i++ ){
            if(Lug_info[i][5].equals("true"))
                W_count++;
        }
        dis_price= distance*2;
        lug_pric= bF.getL_count()*20.0;
        w_price= W_count*30.0;

        Double p = (lug_pric)+(dis_price)+(w_price);
        return p;

    }
    //---------------------------------------------------------------------------------------------

    /* private String getDistanceOnRoad(double prelatitute, double prelongitude) {}*/
    //-------------------------------------------------------------------------------------
    public void showpriceAlert(){
        AlertDialog.Builder builder = new AlertDialog.Builder(booking_TLP.this);
        builder.setMessage("price details:\n\n" +
                "Luggage Price (20 SAR for each bag): "+20+"X" +(int)(lug_pric/20)+" = " + lug_pric+" SAR\n"+
                "Distance Price (2 SAR for each km): "+2+"X" +distance+" = " + dis_price+" SAR\n"+
                "Extra Price : " + 0 + "SAR\n"+
                "Total price (with out Wrapping Service): " +(total_pri-w_price)+ "\n"+
                "Wrapping Price (30 SAR for each bag): "+30+"X" + (int)(w_price/30)+" = " + w_price+" SAR\n"+
                "Final Total price : " +total_pri);
        builder.setTitle("Price Details");
        builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }
    //----------------------------------------------------------------------------------------------------

    // dialoge box to display instructions to complete booking service and add info to DB....
    public void showAlert(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Luggage Instructions"); // title of the dialog
        builder.setMessage(getResources().getString(R.string.instrauctions));
        builder.setCancelable(false);
        builder.setPositiveButton("Accept", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which) {
                LU.setstat("Reserved",bF.getB_refernce());
                //-------------------------------------------
                prgDialog = ProgressDialog.show(booking_TLP.this, "Wait Please...", "Creating Your Booking...", false, true);
                prgDialog.setCancelable(false);
                prgDialog.setCanceledOnTouchOutside(false);
                //---------------------------------------------
                bF.setbooking_date((time_option.getSelectedItem().toString().split(" "))[0]);
                bF.setbooking_time((time_option.getSelectedItem().toString().split(" "))[1]);
                bF.setDis_price(dis_price);
                bF.setLug_pric(lug_pric);
                bF.setW_price(w_price);
                bF.setPrice(total_pri);
                booking_TLP.BackGround b = new booking_TLP.BackGround();
                b.execute(bF.getp_id(),bF.getbooking_date(),bF.getbooking_time(),bF.getFrom(),bF.getTo(),bF.getflight_time(),
                        bF.getflight_Date(),bF.getB_refernce(), bF.getL_count(),
                        bF.getPrice(), bF.getDis_price(), bF.getLug_pric(), bF.getW_price(),
                        bF.getAddress(),  bF.getLatitude(),bF.getLongitude());
                ////-------------------------------------------------------------------
                ArrayList<String> selectedFamily = bF.getSelectedFamily();
                for(int i = 0 ; i< selectedFamily.size();i++){
                    booking_TLP.BackGroundF bf = new booking_TLP.BackGroundF();
                    bf.execute(bF.getB_refernce(),bF.getp_id(),selectedFamily.get(i) );
                }
                /////-------------------------------------------------------------------
                String [][] lu = bF.getLuggage();
                for(int i = 0 ; i< lu.length;i++){
                    booking_TLP.BackGroundL bl = new booking_TLP.BackGroundL();
                    bl.execute(bF.getB_refernce(),lu[i][0],lu[i][1],lu[i][2],lu[i][3],lu[i][4],lu[i][5] );
                }
                /////-------------------------------------------------------------------
                booking_TLP.BackGroundE be = new booking_TLP.BackGroundE();
                be.execute(bF.getp_id(),bF.getB_refernce());
                //----------------------------------------------------------------------
                prgDialog.dismiss();
                Intent i=new Intent(booking_TLP.this,mainbookings.class);
                startActivity(i);
            }
        });
        builder.setNegativeButton("Declined", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }


        });
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    //------------------------------------------------------------------------

    class BackGround extends AsyncTask<Object, Void, String > {
        int P_Id ;
        String booking_date ;
        String booking_time ;
        String from ;
        String to ;
        String flight_time ;
        String flight_date;
        String b_refernce ;
        int l_count ;
        double price;
        double Dis_price;
        double Lug_pric;
        double W_price ;
        String Address ;
        double Latitude;
        double Longitude;

        String data="";
        @Override
        protected String doInBackground(Object... params) {

            try {
                P_Id = (int)params[0];
                booking_date = (String)params[1];
                booking_time = (String)params[2];
                from = (String)params[3];
                to = (String)params[4];
                flight_time = (String)params[5];
                flight_date = (String)params[6];
                b_refernce = (String)params[7];
                l_count = (int)params[8];
                price = (double)params[9];
                Dis_price=(double)params[10];
                Lug_pric=(double)params[11];
                W_price = (double)params[12];
                Address = (String)params[13];
                Latitude=(double)params[14];
                Longitude=(double)params[15];
                //-----------------------------------------------------------------

                int tmp;

                URL url = new URL("http://abdr.000webhostapp.com/bookingfps.php");
                String urlParams = "P_Id="+P_Id+"&b_refernce="+b_refernce+"&booking_date="+booking_date+
                        "&booking_time="+booking_time+"&Address="+Address+"&price="+price+
                        "&Latitude="+Latitude+"&Longitude="+Longitude+
                        "&from="+from+"&to="+to+"&flight_time="+flight_time+"&flight_date="+flight_date+
                        "&Dis_price="+Dis_price+"&Lug_pric="+Lug_pric+"&W_price="+W_price
                        +"&l_count="+l_count;

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
            if(s.equals("no driver")){
                Toast.makeText(booking_TLP.this, "Choose another time ", Toast.LENGTH_LONG).show();
            }
            else if(s.equals("false")){
                Toast.makeText(booking_TLP.this, "Booking not completed", Toast.LENGTH_LONG).show();
            }

        }
    }

    class BackGroundF extends AsyncTask<Object, Void, String > {

        String b_refernce ;
        int P_Id ;
        String name ;
        String data="";
        @Override
        protected String doInBackground(Object... params) {

            try {
                b_refernce = (String)params[0];
                P_Id= (int)params[1];
                name = (String)params[2];
                //-----------------------------------------------------------------

                int tmp;

                URL url = new URL("http://abdr.000webhostapp.com/bookingf.php");
                String urlParams = "b_refernce="+b_refernce+"&P_Id="+P_Id+"&name="+name;

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
            if(s.equals("false")){
                Toast.makeText(booking_TLP.this, "Inter family info not completed", Toast.LENGTH_LONG).show();
            }

        }
    }

    class BackGroundL extends AsyncTask<Object, Void, String > {
        String b_refernce ;
        String L_brand;
        String L_color;
        String L_description;
        String Lweight;
        String fragile;
        String wrap;
        String data="";
        @Override
        protected String doInBackground(Object... params) {

            try {
                b_refernce = (String)params[0];
                L_brand= (String)params[1];
                L_color = (String)params[2];
                L_description= (String)params[3];
                Lweight= (String)params[4];
                fragile= (String)params[5];
                wrap= (String)params[6];
                //-----------------------------------------------------------------

                int tmp;

                URL url = new URL("http://abdr.000webhostapp.com/bookingl.php");
                String urlParams = "b_refernce="+b_refernce+"&L_brand="+L_brand+"&L_color="+L_color+"&L_description="+L_description+
                        "&Lweight="+Lweight+"&fragile="+fragile+"&wrap="+wrap;


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
                Toast.makeText(booking_TLP.this, "Inter luggage info not completed", Toast.LENGTH_LONG).show();
            }

        }
    }

    class BackGroundE extends AsyncTask<Object, Void, String > {

        String b_refernce ;
        int P_Id ;
        String data="";
        @Override
        protected String doInBackground(Object... params) {

            try {
                P_Id= (int)params[0];
                b_refernce = (String)params[1];

                //-----------------------------------------------------------------

                int tmp;

                URL url = new URL("http://abdr.000webhostapp.com/emailbooking.php");
                String urlParams = "b_refernce="+b_refernce+"&P_Id="+P_Id+"&change=no"+"&user=p"+"&d_Id= ";

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
            if(s.equals("false")){
                Toast.makeText(booking_TLP.this, "Booking not completed", Toast.LENGTH_LONG).show();
            }

        }
    }



}

