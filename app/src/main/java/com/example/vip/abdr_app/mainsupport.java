package com.example.vip.abdr_app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
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

public class mainsupport extends BaseActivity  {


    String []titles;
    String []ref;
    String []desc;
    String []answer;
    String []state;
    int []cid;
    GridView gv;
    int PID;
    ProgressDialog prgDialog;
    GridAdaptersupport ga=new GridAdaptersupport(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainsupport);

        PID=new localuser(this).getLoggedInUser().P_ID;
        prgDialog = new ProgressDialog(this);

    }

    @Override
    protected void onStart(){
        super.onStart();
        prgDialog = ProgressDialog.show(this, "Wait Please...", "Loading Your Support Cases...", false, true);
        prgDialog.setCancelable(false);
        prgDialog.setCanceledOnTouchOutside(false);
        mainsupport.BackGround b = new mainsupport.BackGround();
        b.execute(PID+"");

    }
    class BackGround extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            int P_ID = Integer.parseInt(params[0]);
            String data="";
            int tmp;

            try {
                URL url = new URL("http://abdr.000webhostapp.com/showsupport.php");
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
            String err=null;
            prgDialog.dismiss();
            if(s=="false"){
                Toast.makeText(mainsupport.this, "No support issued", Toast.LENGTH_LONG).show();
            }
            else {
                try {
                    JSONObject root = new JSONObject(s);
                    JSONArray array = root.getJSONArray("user_data");
                    titles=new String[array.length()];
                    state=new String[array.length()];
                    ref=new String[array.length()];
                    desc=new String[array.length()];
                    answer=new String[array.length()];
                    cid=new int[array.length()];
                    if(array.length()>0){
                    for (int i=0;i<array.length();i++){
                        titles[i]=array.getJSONObject(i).getString("title");
                        state[i]=array.getJSONObject(i).getString("complain_status");
                        ref[i]=array.getJSONObject(i).getString("booking_reference_c");
                        desc[i]=array.getJSONObject(i).getString("complain_descreption");
                        answer[i]=array.getJSONObject(i).getString("admin_answer");
                        cid[i]=array.getJSONObject(i).getInt("complain_id");
                    }}else
                        Toast.makeText(mainsupport.this, "No complaints", Toast.LENGTH_LONG).show();
                    gv=(GridView)findViewById(R.id.supportgrid);
                    ga=new GridAdaptersupport(mainsupport.this,titles,state,ref,desc,answer,cid);
                    gv.setAdapter(ga);
                    gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                            Intent i=new Intent(mainsupport.this,supportinfo.class);
                            i.putExtra("titles",titles);
                            i.putExtra("state",state);
                            i.putExtra("ref",ref);
                            i.putExtra("desc",desc);
                            i.putExtra("answer",answer);
                            i.putExtra("id",cid);
                            i.putExtra("pos",position);
                            startActivity(i);
                        }
                    });
                } catch (JSONException e) {
                    //Toast.makeText(mainsupport.this, "support load "+e.getMessage(), Toast.LENGTH_LONG).show();

                }
            }


        }
    }

}
