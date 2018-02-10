package com.example.vip.abdr_app;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;



public class GridAdapterfamily extends BaseAdapter {

    static String []names;
     String [] image;
     long []id;
     String []rel;
     Bitmap []b;
    private Context context;
    private LayoutInflater inflater;
    public GridAdapterfamily(Context c, long [] id, String []names, String []rel, String []img){

        this.context=c;
        this.names=names;
        this.id=id;
        this.rel=rel;

        this.image=img;

    }
    public GridAdapterfamily(Context c){

        this.context=c;

    }
    @Override
    public int getCount() {
        return names.length;
    }

    @Override
    public String  getItem(int position) {
        return names[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View gridv=convertView;

        if (convertView==null){
            inflater=(LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            gridv=inflater.inflate(R.layout.familyrow,null);
        }
        //get view
        TextView n=(TextView)gridv.findViewById(R.id.name);
        TextView r=(TextView)gridv.findViewById(R.id.rel);
        TextView i=(TextView)gridv.findViewById(R.id.idn);
        //set data
        n.setText(names[position]);
        r.setText(rel[position]);
        i.setText(id[position]+"");

        return gridv;
    }

    }
