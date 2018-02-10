package com.example.vip.abdr_app;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TimePicker;

import java.util.Calendar;

/**
 * Created by Sarah on 07/03/2017.
 */

@SuppressLint("ValidFragment")
public class TimeDialog extends DialogFragment implements TimePickerDialog.OnTimeSetListener{
    EditText txttime;
    String time;

    public TimeDialog(View view){

        txttime=(EditText)view;
    }


    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Calendar c = Calendar.getInstance();
        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);
        return new TimePickerDialog(getActivity(), this, mHour, mMinute, false);


    }
    public String gettime (){
        return time ;
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        //show to the selected date in the text box
        time=hourOfDay+":"+minute;
        txttime.setText(time);
    }
}
