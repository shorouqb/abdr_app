package com.example.vip.abdr_app;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
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

public class familymain extends BaseActivity implements View.OnClickListener {
    ImageView add;

    String []names=null;
    String []images;
    String []rel;
    GridView gv;
    long []id;
    int PID;
    ProgressDialog prgDialog;
    GridAdapterfamily ga=new GridAdapterfamily(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_familymain);
        add = (ImageView) findViewById(R.id.add);
        add.setOnClickListener(this);
        PID=new localuser(this).getLoggedInUser().P_ID;
        prgDialog = new ProgressDialog(this);


    }
    @Override
    protected void onStart(){
        super.onStart();
        prgDialog = ProgressDialog.show(this, "Wait Please...", "Loading Your Family Profiles...", false, true);
        prgDialog.setCancelable(false);
        prgDialog.setCanceledOnTouchOutside(false);
        familymain.BackGround b = new familymain.BackGround();

          b.execute(PID+"");


    }
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.add:
                startActivity(new Intent(familymain.this,addfamily.class));
                break;

        }
    }

    class BackGround extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            int P_ID = Integer.parseInt(params[0]);
            String data="";
            int tmp;

            try {//192.168.56.1
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
            if(s.equals("false")){
                Toast.makeText(familymain.this, "No family", Toast.LENGTH_LONG).show();
            }
            else {
                try {
                    JSONObject root = new JSONObject(s);
                    JSONArray array = root.getJSONArray("user_data");
                    if(array.length()>0){
                    names = new String[array.length()];
                    rel = new String[array.length()];
                    images = new String[array.length()];
                    id = new long[array.length()];
                    for (int i = 0; i < array.length(); i++) {
                        names[i] = array.getJSONObject(i).getString("name");
                        id[i] = array.getJSONObject(i).getInt("id_number");
                        rel[i] = array.getJSONObject(i).getString("relationship");
                        images[i] = array.getJSONObject(i).getString("id_card_img");
                    }
                    gv = (GridView) findViewById(R.id.familygrid);
                    ga = new GridAdapterfamily(familymain.this, id, names, rel, images);
                    gv.setAdapter(ga);
                    gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(familymain.this);
                            builder.setTitle("Delete family member");
                            WebView wv = new WebView(familymain.this);
                            String data = "<div align='center'> <img src='" + images[position] + "' style='width:200px; height:150px;' /></div>";
                            wv.loadData(data, "text/html", "utf-8");
                            wv.setWebViewClient(new WebViewClient() {
                                @Override
                                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                                    view.loadUrl(url);
                                    return true;
                                }
                            });
                            builder.setView(wv);
                            builder.setMessage("would like to delete family member (name): " + ga.getItem(position));
                            builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    prgDialog = ProgressDialog.show(familymain.this, "Wait Please...", "Deleting Family Member...", false, true);
                                    familymain.BackGround1 b = new familymain.BackGround1();
                                    b.execute(PID + "", ga.getItem(position));
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
                }
                    else{
                        Toast.makeText(familymain.this,"No Family added in your account", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    //Toast.makeText(familymain.this, "family load "+e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }


        }
    }


    class BackGround1 extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            String pid = params[0];
            String name=params[1];
            String data="";
            int tmp;

            try {//192.168.56.1
                URL url = new URL("http://abdr.000webhostapp.com/deletefamily.php");
                String urlParams = "P_ID="+pid+"&name="+name;

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
                Toast.makeText(familymain.this, "Family member cannot delete", Toast.LENGTH_LONG).show();
            }


            else {
                Intent i = new Intent(familymain.this, familymain.class);
                startActivity(i);
            }


        }
    }
}
