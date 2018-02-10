package com.example.vip.abdr_app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * Created by v i p on 3/1/2017.
 */

public class GridAdapterbooking extends BaseAdapter {

    String []ref;
    String []state;
    int [] driver;
    int [] id;
    String  [] time;
    String  [] date;
    double []fee;

    private Context context;
    private LayoutInflater inflater;
    public GridAdapterbooking(Context c,int []id, String []ref, String []stat, int []driver,String []time, String []date, double []fee ){

        this.context=c;
        this.ref=ref;
        this.driver=driver;
        this.id=id;
        this.time=time;
        this.state=stat;
        this.date=date;
        this.fee=fee;
    }
    public GridAdapterbooking(Context c){
        this.context=c;
    }
    @Override
    public int getCount() {
        return ref.length;
    }

    @Override
    public String  getItem(int position) {
        return ref[position];
    }
    public int  getid(int position) {
        return id[position];
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
            gridv=inflater.inflate(R.layout.bookingrow,null);
        }
        //get view
        TextView n=(TextView)gridv.findViewById(R.id.status);
        TextView r=(TextView)gridv.findViewById(R.id.time);
        TextView m=(TextView)gridv.findViewById(R.id.date);
        //set data
        n.setText(state[position]);
        r.setText(time[position]);
        m.setText(date[position]);
        return gridv;
    }

}
