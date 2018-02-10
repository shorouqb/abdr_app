package com.example.vip.abdr_app;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
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

public class barcodes extends BaseActivity {
    int[] id;
    GridView gv;
    String[] img;
    String[] number;

    int PID;
    String refe;
    TextView bref, lcount;
    GridAdapterbarcode ga;
    ProgressDialog prgDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcodes);

        PID = new localuser(this).getLoggedInUser().P_ID;

        bref = (TextView) findViewById(R.id.ref);
        lcount = (TextView) findViewById(R.id.lno);
        refe = getIntent().getStringExtra("ref");
        prgDialog = new ProgressDialog(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        prgDialog = ProgressDialog.show(this, "Wait Please...", "Loading Your Luggage Barcodes...", false, true);
        prgDialog.setCancelable(false);
        prgDialog.setCanceledOnTouchOutside(false);
        bref.setText("Booking Reference: " + refe);
        barcodes.BackGround b = new barcodes.BackGround();
        b.execute(refe);

    }

    class BackGround extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            String ref = params[0];
            String data = "";
            int tmp;

            try {
                URL url = new URL("http://abdr.000webhostapp.com/showbarcode.php");
                String urlParams = "ref=" + ref;

                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setDoOutput(true);
                OutputStream os = httpURLConnection.getOutputStream();
                os.write(urlParams.getBytes());
                os.flush();
                os.close();

                InputStream is = httpURLConnection.getInputStream();
                while ((tmp = is.read()) != -1) {
                    data += (char) tmp;
                }

                is.close();
                httpURLConnection.disconnect();

                return data;
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return "Exception: " + e.getMessage();
            } catch (IOException e) {
                e.printStackTrace();
                return "Exception: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            String err = null;
            prgDialog.dismiss();
            //Toast.makeText(familymain.this, s, Toast.LENGTH_LONG).show();
            if (s.equals("false")) {
                Toast.makeText(barcodes.this, "No barcodes", Toast.LENGTH_LONG).show();
            } else {

                //Toast.makeText(familymain.this,"in", Toast.LENGTH_LONG).show();
                try {
                    JSONObject root = new JSONObject(s);
                    JSONArray array = root.getJSONArray("user_data");
                    img = new String[array.length()];
                    number = new String[array.length()];
                    //id=new int[array.length()];
                    lcount.setText("Number Of Checked-in Luggage: " + array.length());
                    for (int i = 0; i < array.length(); i++) {
                        img[i] = array.getJSONObject(i).getString("luggage_barCode");
                       number[i] = array.getJSONObject(i).getString("barcodenumber");
                    }

                    gv = (GridView) findViewById(R.id.bargrid);
                    ga = new GridAdapterbarcode(barcodes.this, img,number);
                    gv.setAdapter(ga);
                    gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(barcodes.this);
                            builder.setTitle("Barcode");
                            ImageView image = new ImageView(barcodes.this);
                            if (!img[position].equals("null")) {
                                WebView wv = new WebView(barcodes.this);
                                String data = "<div align='center'> <img src='" + img[position] + "' style='width:150px; height:90px;' /></div>";
                                wv.loadData(data, "text/html", "utf-8");
                                wv.setWebViewClient(new WebViewClient() {
                                    @Override
                                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                                        view.loadUrl(url);

                                        return true;
                                    }
                                });

                                builder.setView(wv);
                            } else {
                                image.setImageResource(R.drawable.b);
                                builder.setView(image);
                            }
                            builder.setMessage("Number: \n" + number[position]);
                            builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }
                            });

                            AlertDialog dialog = builder.create();
                            dialog.getWindow().setLayout(200, 400);
                            dialog.show();

                        }
                    });
                } catch (JSONException e) {
                    //Toast.makeText(barcodes.this, "barcode " + e.getMessage(), Toast.LENGTH_LONG).show();

                }
            }


        }
    }
}

