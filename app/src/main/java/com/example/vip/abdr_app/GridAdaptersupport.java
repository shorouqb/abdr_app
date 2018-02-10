package com.example.vip.abdr_app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


public class GridAdaptersupport extends BaseAdapter {

    String []titles;
    String []ref;
    String []desc;
    String []answer;
    String []state;
    int []id;
    private Context context;
    private LayoutInflater inflater;
    public GridAdaptersupport(Context c, String []titles, String []state, String []ref, String []desc, String []answer,int []id){

        this.context=c;
        this.titles=titles;
        this.ref=ref;
        this.desc=desc;
        this.id=id;
        this.answer=answer;
        this.state=state;

    }
    public GridAdaptersupport(Context c){

        this.context=c;

    }
    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public String  getItem(int position) {
        return titles[position];
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
            gridv=inflater.inflate(R.layout.supportrow,null);
        }
        //get view
        TextView n=(TextView)gridv.findViewById(R.id.stat);
        TextView r=(TextView)gridv.findViewById(R.id.title);
        //set data
        n.setText(state[position]);
        r.setText(titles[position]);

        return gridv;
    }

}
