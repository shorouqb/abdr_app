package com.example.vip.abdr_app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class GridAdapterbarcode extends BaseAdapter {

    static String []img;
    static String []number;
     int []id;
    private Context context;
    private LayoutInflater inflater;
    //int [] id,
    public GridAdapterbarcode(Context c, String []img,String []number){

        this.context=c;
        this.img=img;
        this.number=number;

    }

    @Override
    public int getCount() {
        return img.length;
    }

    @Override
    public String  getItem(int position) {
        return img[position];
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
            gridv=inflater.inflate(R.layout.barcoderow,null);
        }
        TextView n=(TextView)gridv.findViewById(R.id.num);
        n.setText(number[position]);

        return gridv;
    }

}
