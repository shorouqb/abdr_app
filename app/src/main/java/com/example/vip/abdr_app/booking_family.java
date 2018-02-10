package com.example.vip.abdr_app;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
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
import java.util.ArrayList;

public class booking_family extends BaseActivity implements View.OnClickListener {
    booking_Info bF ;
    int PID;
    ImageView next ;
    EditText lug_count;
    ProgressDialog prgDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_family);
        Intent i = getIntent();
        bF= (booking_Info) i.getSerializableExtra("bookingInfo");
        next= (ImageView)findViewById(R.id.next_btn);
        next.setOnClickListener(this);
        PID = bF.getp_id();
        lug_count = (EditText)findViewById(R.id.luggages_count);
        prgDialog = new ProgressDialog(this);

    }

    public void onStart(){
        super.onStart();
        prgDialog = ProgressDialog.show(this, "Wait Please...", "Loading Your Family members...", false, true);
        prgDialog.setCancelable(false);
        prgDialog.setCanceledOnTouchOutside(false);
        booking_family.BackGround b = new booking_family.BackGround(this);
        b.execute(PID+"");

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.next_btn:
                if(lug_count.getText().toString().equals("")||lug_count.getText().toString().equals("0")){
                    showAlert();
                }else{
                bF.setL_count(Integer.parseInt(lug_count.getText().toString()));
                Intent i=new Intent(booking_family.this,booking_luggage.class);
                i.putExtra("bookingInfo" , bF);
                startActivity(i);}

                break;

        }
    }

    public void showAlert(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Warning");
        builder.setMessage("Please enter number of luggage's you have  ..");

        builder.setCancelable(false);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }
    class BackGround extends AsyncTask<String, String, String> {
        String []name ;
        ArrayList<String> selectedItems;
        private booking_family bbf;

        public BackGround(booking_family bbf) {
            this.bbf = bbf;
        }
        protected void onPreExecute() {

            super.onPreExecute();
        }

        protected String doInBackground(String... params) {
            int P_ID = Integer.parseInt(params[0]);
            String data="";
            int tmp;

            try {
                URL url = new URL("http://abdr.000webhostapp.com/showfamily.php");
                String urlParams = "P_ID="+P_ID;

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
            String err=null;
            if(s.equals("false")){
                Toast.makeText(booking_family.this, "No family", Toast.LENGTH_LONG).show();
            }
            else {
                try {
                    JSONObject root = new JSONObject(s);

                    JSONArray array = root.getJSONArray("user_data");
                    name=new String[array.length()];

                    for (int i=0;i<array.length();i++){
                        name[i]=array.getJSONObject(i).getString("name");

                    }
                    ListView chl=(ListView) findViewById(R.id.checkable_list);
                    chl.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                    selectedItems=new ArrayList<String>();
                    ArrayAdapter<String> aa=new ArrayAdapter(bbf, R.layout.familyrow_booking, R.id.txt_title,name);
                    chl.setAdapter(aa);
                    chl.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        TextView tv = (TextView)view.findViewById(R.id.txt_title);
                        CheckedTextView checkbox = (CheckedTextView) view.findViewById(R.id.txt_title);
                        checkbox.setChecked(!checkbox.isChecked());
                        String selectedItem = tv.getText().toString();
                        if(selectedItems.contains(selectedItem))
                            selectedItems.remove(selectedItem);
                        else
                            selectedItems.add(selectedItem);

                        }

                    });
                    bbf.bF.setSelectedFamily(selectedItems);


                } catch (JSONException e) {
                   // Toast.makeText(booking_family.this, e.getMessage() +"000000", Toast.LENGTH_LONG).show();

                }
            }


        }
    }

    }
