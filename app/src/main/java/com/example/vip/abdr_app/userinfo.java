package com.example.vip.abdr_app;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class userinfo extends BaseActivity implements View.OnClickListener {
    EditText username,Pass,fname,lname,id,nationality,city,phone,email;
    localuser LU;
    passenger p;
    String img_btm="null";
    private static final int CAM_REQUEST = 1313;
    int p_id;
    ImageView imgShowPhoto,addimg;
    ImageButton im,editdb;
    ProgressDialog prgDialog;
    private Bitmap bitmap;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri filePath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo);

        username = (EditText)findViewById(R.id.username);
        Pass = (EditText)findViewById(R.id.password);
        fname = (EditText)findViewById(R.id.firstn);
        lname = (EditText)findViewById(R.id.lastn);
        id = (EditText)findViewById(R.id.idno);
        nationality = (EditText)findViewById(R.id.nationality);
        city = (EditText)findViewById(R.id.city);
        phone = (EditText)findViewById(R.id.phone);
        email = (EditText)findViewById(R.id.email);

        imgShowPhoto=(ImageView)findViewById(R.id.idimg);

        im = (ImageButton)findViewById(R.id.edit);
        im.setOnClickListener(this);

        addimg = (ImageView)findViewById(R.id.img);
        addimg.setOnClickListener(this);;
        addimg.setEnabled(false);

        editdb = (ImageButton)findViewById(R.id.editdb);
        editdb.setOnClickListener(this);
        editdb.setVisibility(View.GONE);

        username.setEnabled(false);
        Pass.setEnabled(false);
        fname.setEnabled(false);
        lname.setEnabled(false);
        id.setEnabled(false);
        nationality.setEnabled(false);
        city.setEnabled(false);
        phone.setEnabled(false);
        email.setEnabled(false);

        LU=new localuser(this);

        prgDialog = new ProgressDialog(this);

    }
    @Override
    protected void onStart() {
        super.onStart();
        if(LU.getIfLoggedInUser()) {
            p=LU.getLoggedInUser();

            p_id=p.P_ID;
            username.setText(p.uname);
            Pass.setText(p.pass);
            fname.setText(p.firstn);
            lname.setText(p.lastn);
            id.setText(p.idNo+"");
            nationality.setText(p.nationality);
            city.setText(p.city);
            phone.setText(p.phone+"");
            email.setText(p.email);
            imgShowPhoto.setVisibility(View.GONE);

        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAM_REQUEST) {

            bitmap = (Bitmap) data.getExtras().get("data");
            imgShowPhoto.setVisibility(View.VISIBLE);
            imgShowPhoto.setImageBitmap(bitmap);
            img_btm = getStringImage(bitmap);

        }
        else if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imgShowPhoto.setVisibility(View.VISIBLE);
                imgShowPhoto.setImageBitmap(bitmap);
                img_btm = getStringImage(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }


        public void take() {

            AlertDialog.Builder builder = new AlertDialog.Builder(userinfo.this);

            builder.setTitle("Id image");
            ImageView image = new ImageView(userinfo.this);
            builder.setMessage("Would you like to upload image from gallery, or take new image ");
            builder.setPositiveButton("Camera", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Intent cameraintent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraintent, CAM_REQUEST);
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton("Gallery", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
                    dialog.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.getWindow().setLayout(200, 400);
            dialog.show();

        }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.edit:
                username.setEnabled(true);
                Pass.setEnabled(true);
                fname.setEnabled(true);
                lname.setEnabled(true);
                id.setEnabled(true);
                nationality.setEnabled(true);
                city.setEnabled(true);
                phone.setEnabled(true);
                email.setEnabled(true);
                addimg.setEnabled(true);
                im.setVisibility(View.GONE);
                editdb.setVisibility(View.VISIBLE);
                break;

            case R.id.editdb:
                prgDialog = ProgressDialog.show(this, "Wait Please...", "Updating Your Profile...", false, true);
                prgDialog.setCancelable(false);
                prgDialog.setCanceledOnTouchOutside(false);
                userinfo.BackGround b = new userinfo.BackGround();
                b.execute(fname.getText().toString(), lname.getText().toString(), city.getText().toString()
                        , email.getText().toString(), nationality.getText().toString(),phone.getText().toString(),
                        id.getText().toString(), username.getText().toString(), Pass.getText().toString(),p_id+"",img_btm);
                editdb.setVisibility(View.GONE);
                im.setVisibility(View.VISIBLE);
                username.setEnabled(false);
                Pass.setEnabled(false);
                fname.setEnabled(false);
                lname.setEnabled(false);
                id.setEnabled(false);
                addimg.setEnabled(false);
                nationality.setEnabled(false);
                city.setEnabled(false);
                phone.setEnabled(false);
                email.setEnabled(false);
                break;
            case R.id.img:
            AlertDialog.Builder builder = new AlertDialog.Builder(userinfo.this);
            builder.setTitle("Id image");
            ImageView image = new ImageView(userinfo.this);
            if(!p.idImage.equals("null")&&!p.idImage.equals("")){
                WebView wv = new WebView(userinfo.this);
                String data="<div align='center'> <img src='"+p.idImage+"' style='width:200px; height:150px;' /></div>";
                wv.loadData(data, "text/html", "utf-8");
                wv.setWebViewClient(new WebViewClient() {
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        view.loadUrl(url);

                        return true;
                    }
                });

                builder.setView(wv);
            }
            builder.setPositiveButton("Retake", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                   take();
                    dialog.dismiss();
                }
            });
                builder.setNegativeButton("Fine", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
            AlertDialog dialog = builder.create();
            dialog.getWindow().setLayout(200, 400);
            dialog.show();
            break;
        }
    }


    class BackGround extends AsyncTask<Object, String, String> {
        @Override
        protected String doInBackground(Object... params) {
            String fname = (String)params[0];
            String lname = (String)params[1];
            int id=Integer.parseInt((String)params[6]);
            String nationality = (String)params[4];
            String city = (String)params[2];
            long phone=  Long.parseLong((String)params[5]);
            String email = (String)params[3];
            String username = (String)params[7];
            String password = (String)params[8];
            int pid=Integer.parseInt((String)params[9]);
            String img = (String)params[10];
            String data = "";
            int tmp;
            try {
                URL url = new URL("http://abdr.000webhostapp.com/editpassenger.php");
                String urlParams = "first_name="+fname+"&last_name="+lname+"&id_number="+id+"&nationality="+nationality+"&city="+city+
                "&phone="+phone+"&email="+email+"&username="+username+"&password="+password+"&P_ID="+pid+"&idimg="+ URLEncoder.encode(img, "UTF-8");

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
                return "Exception: " + e.getMessage();
            } catch (IOException e) {
                e.printStackTrace();
                return "Exception: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            prgDialog.dismiss();
            if (s.equals("false")) {
                Toast.makeText(userinfo.this, "Failed to edit", Toast.LENGTH_LONG).show();
            }
            else{
            try {
                JSONObject root = new JSONObject(s);
                JSONArray array = root.getJSONArray("user_data");
                JSONObject c=array.getJSONObject(0);
                p = new passenger(c.getString("first_name"), c.getString("last_name"), c.getString("city"), c.getString("email"),
                        c.getString("nationality"), c.getInt("phone"), c.getInt("id_number"), c.getString("username"),
                        c.getString("password"), c.getInt("P_ID"),c.getString("id_card_img"));

                LU.storeData(p);
                Toast.makeText(userinfo.this, "Profile updated ", Toast.LENGTH_LONG).show();
            } catch (JSONException e) {
                Toast.makeText(userinfo.this, "User update "+e.getMessage(), Toast.LENGTH_LONG).show();

            }
        }

        }
    }
}
