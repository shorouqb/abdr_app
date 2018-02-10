package com.example.vip.abdr_app;

import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class booking extends BaseActivity implements View.OnClickListener {

    EditText FR ;
    ImageView  next ;
    TextView date ,time ;
    Spinner from ,to ;
    String flightR , familyN ,froms ,tos ,dates ,times;
    int PID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);
        FR = (EditText)findViewById(R.id.trip_reference);

        next = (ImageView)findViewById(R.id.next_btn);
        next.setOnClickListener(this);
        from = (Spinner)findViewById(R.id.from);
        to = (Spinner)findViewById(R.id.to);
        date = (EditText)findViewById(R.id.editTextdate);
        time = (EditText)findViewById(R.id.editTexttime);
        PID=new localuser(this).getLoggedInUser().P_ID;


    }

    public void onStart(){
        super.onStart();

        date.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            public void onFocusChange(View view, boolean hasfocus){
                if(hasfocus){
                    DateDialog dialog=new DateDialog(view);
                    FragmentTransaction ft =getFragmentManager().beginTransaction();
                    dialog.show(ft, "DatePicker");

                }
            }

        });

        time.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            public void onFocusChange(View view, boolean hasfocus){
                if(hasfocus){
                    TimeDialog dialog=new TimeDialog(view);
                    FragmentTransaction ft =getFragmentManager().beginTransaction();
                    dialog.show(ft, "TimePicker");

                }
            }

        });
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.next_btn:
                flightR=FR.getText().toString();
                froms=from.getSelectedItem().toString();
                tos=to.getSelectedItem().toString();
                dates=date.getText().toString();
                times=time.getText().toString();

                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                SimpleDateFormat x = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                Date c_date = null;
                Date f_date = null;
                try {
                    c_date = df.parse(df.format(new Date()));
                    final Calendar calendar = Calendar.getInstance();

                    calendar.setTime(c_date);
                    calendar.add(Calendar.HOUR, 24);
                    c_date=calendar.getTime();

                f_date = x.parse(dates + " " + times);


                if(flightR.equals("")||froms.equals("Choose the city")|| tos.equals("Choose the city")
                        || dates.equals("")|| times.equals("")){
                    showAlert();
                }else if ( f_date.before(c_date)){
                    showAlerttd();
                }else{
                booking_Info bF = new booking_Info(froms,tos,times,dates,flightR,PID);
                Intent i=new Intent(booking.this,booking_family.class);
                i.putExtra("bookingInfo" , bF);
                startActivity(i);}
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;

        }
    }
    public void showAlert(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Warning");
        builder.setMessage("Please you have to fill all information about your flight ..");

        builder.setCancelable(false);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    public void showAlerttd(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Warning");
        builder.setMessage("Your flight data and time must be 24 hours after current time ..");

        builder.setCancelable(false);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

}
