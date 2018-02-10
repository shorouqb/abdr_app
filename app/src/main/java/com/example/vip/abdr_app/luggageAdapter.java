package com.example.vip.abdr_app;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class luggageAdapter extends ArrayAdapter<Luggage_info> {

    private Activity context;
    private int id ;
    ArrayList<Luggage_info> array;

    public luggageAdapter(Activity context, int resource, ArrayList<Luggage_info> objects) {
        super(context, resource, objects);
        this.context= context;
                this.id=resource;
                        this.array=objects;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView==null){
            LayoutInflater inflater=context.getLayoutInflater();
            convertView=inflater.inflate(id,null);
        }
        final Luggage_info Luggage_info = array.get(position);
        TextView L_N = (TextView)convertView.findViewById(R.id.textViewL_N);
        TextView DES = (TextView)convertView.findViewById(R.id.textViewDES);
        TextView S_R = (TextView)convertView.findViewById(R.id.textViewS_R);


        L_N.setText(Luggage_info.getLn()+"");
        DES.setText("Luggage color : "+ Luggage_info.getColor()+"\nLuggage brand : "+ Luggage_info.getBrand()
                +"\nLuggage wight : "+ Luggage_info.getWight() +"\nLuggage description : "+ Luggage_info.getDiscription());
        String temp = "";
        if(Luggage_info.isFragile()&&Luggage_info.isWrap())
            temp = "Fragile ,Wrap ";
        else if(Luggage_info.isWrap())
            temp ="Wrap";
        else if(Luggage_info.isFragile())
            temp = "Fragile";
        else {
            temp= "No thing";
        }
        S_R.setText(temp);

        return convertView ;

    }
}
