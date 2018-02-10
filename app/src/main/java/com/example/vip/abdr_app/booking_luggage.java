package com.example.vip.abdr_app;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;

public class booking_luggage extends BaseActivity implements View.OnClickListener{
    ListView Lview;
    Spinner color ;
    Spinner wight ;
    Spinner brand ;
    EditText discription ;
    CheckBox fragile ;
    CheckBox wrap ;
    booking_Info bF ;
    ImageView next,add ;


    ArrayList<Luggage_info> array;
    luggageAdapter adapter;
    int count =0;
    String[][]luggage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_luggage);
        Intent i = getIntent();
        bF = (booking_Info) i.getSerializableExtra("bookingInfo");
        //**********************************************************************
        next = (ImageView) findViewById(R.id.next_btn);
        next.setOnClickListener(this);
        //***********************************************************************
        add = (ImageView) findViewById(R.id.add_btn);
        add.setOnClickListener(this);
        //***********************************************************************
        array = new ArrayList<Luggage_info>();
        adapter = new luggageAdapter(booking_luggage.this, R.layout.luggagerow ,array);
        //***********************************************************************
        Lview = (ListView) findViewById(R.id.luggages_list);
        Lview.setAdapter(adapter);
        Lview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(booking_luggage.this);
                builder.setMessage("would like to cancel this luggage information ");
                builder.setTitle("Cancel luggage information");
                builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if(array.size()>0)
                            array.remove(position);
                        count--;
                        for(int ii = count-1 ; ii < -1 ;ii++){
                            array.get(ii).setLn(ii+1);
                        }

                        adapter.notifyDataSetChanged();
                        if(count < bF.l_count) {
                            add.setEnabled(true);
                        }
                    }
                });
                builder.setNegativeButton("no", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });
        //***********************************************************************
        color = (Spinner) findViewById(R.id.color);
        //***********************************************************************
        wight = (Spinner) findViewById(R.id.wight);
        //************************************************************************
        brand = (Spinner) findViewById(R.id.brand);
        //************************************************************************
        discription = (EditText) findViewById(R.id.editTextdis);
        fragile = (CheckBox) findViewById(R.id.checkBoxfragile);
        wrap = (CheckBox) findViewById(R.id.checkBoxwrap);
        //---------------------------------

    }

    public void AddLuggage(){

        String col =color.getSelectedItem().toString();
        String wi =wight.getSelectedItem().toString();
        String br =brand.getSelectedItem().toString();
        String dis =discription.getText().toString();
        boolean fr =fragile.isChecked();
        boolean w =wrap.isChecked();
        if(col.equals("Select Color"))
            col="Not specified";
        if(wi.equals("Select Wight"))
            wi="Not specified";
        if(dis.equals(""))
            dis="Not specified";
        if(br.equals("Select Brand"))
            br="Not specified";
        Luggage_info li = new Luggage_info((++count),col ,wi,br,dis,fr,w);
        array.add(0,li);
        adapter.notifyDataSetChanged();
        color.setSelection(0);
        wight.setSelection(0) ;
        brand.setSelection(0) ;
        discription.setText("");
        fragile.setChecked(false);
        wrap.setChecked(false);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.next_btn:
                luggage=new String[bF.l_count][6];
                for(int i = 0 ;i<bF.l_count;i++){
                    luggage[i][0]="Not specified";
                    luggage[i][1]="Not specified";
                    luggage[i][2]="Not specified";
                    luggage[i][3]="Not specified";
                    luggage[i][4]="false";
                    luggage[i][5]="false";
                }
                for(int i = 0 ;i<count;i++){
                    luggage[i][0]=array.get(i).getBrand();
                    luggage[i][1]=array.get(i).getColor();
                    luggage[i][2]=array.get(i).getDiscription();
                    luggage[i][3]=array.get(i).getWight();
                    luggage[i][4]=array.get(i).isFragile()+"";
                    luggage[i][5]=array.get(i).isWrap()+"";
                }

                bF.setLuggage(luggage);
                Intent in=new Intent(booking_luggage.this,Location.class);
                in.putExtra("bookingInfo" , bF);
                startActivity(in);
                break;
            case R.id.add_btn:
                AddLuggage();
                if(count >= bF.l_count) {
                    add.setEnabled(false);
                }
                break;

        }
    }
}
