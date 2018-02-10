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
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
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

public class register extends AppCompatActivity implements View.OnClickListener{
    localuser LU;
    private Bitmap bitmap;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri filePath;
    ImageView reg,btnTakePhoto,cans;
    EditText username,Pass,fname,lname,id,nationality,phone,email;
    ImageView imgTakenPhoto;
    Spinner city;
    private static final int CAM_REQUEST = 1313;
    String img_btm=" ";
    ProgressDialog prgDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        username = (EditText) findViewById(R.id.username);
        Pass = (EditText) findViewById(R.id.password);
        fname = (EditText) findViewById(R.id.firstn);
        lname = (EditText) findViewById(R.id.lastn);
        id = (EditText) findViewById(R.id.idno);
        nationality = (EditText) findViewById(R.id.nationality);
        city = (Spinner) findViewById(R.id.city);
        phone = (EditText) findViewById(R.id.phone);
        email = (EditText) findViewById(R.id.email);
        btnTakePhoto = (ImageView) findViewById(R.id.button1);
        imgTakenPhoto = (ImageView) findViewById(R.id.imageview1);

        btnTakePhoto.setOnClickListener(new btnTakePhotoClicker());
        reg = (ImageView) findViewById(R.id.btnregister);
        reg.setOnClickListener(this);

        cans= (ImageView)findViewById(R.id.cans);
        cans.setOnClickListener(this);

        LU = new localuser(this);
        prgDialog = new ProgressDialog(this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAM_REQUEST) {

            bitmap = (Bitmap) data.getExtras().get("data");
            imgTakenPhoto.setImageBitmap(bitmap);
            img_btm = getStringImage(bitmap);

        }
        else if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imgTakenPhoto.setImageBitmap(bitmap);
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

    class btnTakePhotoClicker implements Button.OnClickListener {

        @Override
        public void onClick(View v) {

            AlertDialog.Builder builder = new AlertDialog.Builder(register.this);

            builder.setTitle("Id image");
            ImageView image = new ImageView(register.this);
            builder.setMessage("Would you like to upload image from gallery, or take new image ");
            builder.setPositiveButton("Camera", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Intent cameraintent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    //cameraintent.setType("image/*");
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

    }

    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btnregister:

                if (fname.getText().toString().equals("")||lname.getText().toString().equals("")||city.getSelectedItem().toString().equals("City:")||
                        email.getText().toString().equals("")||nationality.getText().toString().equals("")||phone.getText().toString().equals("")
                    ||id.getText().toString().equals("")||username.getText().toString().equals("")||Pass.getText().toString().equals("")||img_btm.equals(" ")) {
                    Toast.makeText(register.this, "Fill the missing field", Toast.LENGTH_LONG).show();

                }
                else{
                    int atpos = email.getText().toString().indexOf("@");
                    int dotpos = email.getText().toString().lastIndexOf(".");
                    if (atpos < 1 || dotpos < atpos + 2 || dotpos + 2 >= email.getText().toString().length()) {
                        Toast.makeText(register.this, "Not a valid e-mail address", Toast.LENGTH_LONG).show();
                    }
                    else if (!phone.getText().toString().startsWith("5")||phone.getText().toString().length()!=9){
                        Toast.makeText(register.this, "Phone format is wrong ", Toast.LENGTH_LONG).show();
                    }
                    else if (id.getText().toString().length()!=10){
                        Toast.makeText(register.this, "National id is not complete ", Toast.LENGTH_LONG).show();
                    }

                    else{
                        prgDialog = ProgressDialog.show(this, "Wait Please...", "Checking Email and Username...", false, true);
                        prgDialog.setCancelable(false);
                        prgDialog.setCanceledOnTouchOutside(false);
                        BackGround1 b = new BackGround1(this);
                        b.execute(email.getText().toString(),username.getText().toString());
                    }
                }


                    break;


        case R.id.cans:
       startActivity(new Intent(this,mainpage.class));
        break;
    }
    }

    private void loguser(passenger p) {
        LU.storeData(p);
        LU.setLoggedInUser(true);
        startActivity(new Intent(this,userinfo.class));
    }
    class BackGround extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            String fname = params[0];
            String lname = params[1];
            long id=Long.parseLong(params[6]);
            String nationality = params[4];
            String city = params[2];
            long phone=Long.parseLong(params[5]);
            String email = params[3];
            String username = params[7];
            String password = params[8];
            String img = params[9];
            String data="";
            int tmp;
            try {
                URL url = new URL("http://abdr.000webhostapp.com/register.php");
                String urlParams = "first_name="+fname+"&last_name="+lname+"&id_number="+id+"&nationality="+nationality+"&city="+city+
                        "&phone="+phone+"&email="+email+"&username="+username+"&password="+password+"&idimg="+URLEncoder.encode(img, "UTF-8");
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setDoOutput(true);
                OutputStream os = httpURLConnection.getOutputStream();
                os.write(urlParams.getBytes());
                os.flush();
                os.close();
                InputStream is = httpURLConnection.getInputStream();
                while((tmp=is.read())!=-1){
                    data+= (char)tmp;}
                is.close();
                httpURLConnection.disconnect();
                return data;
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return "Exception: "+e.getMessage();
            } catch (IOException e) {
                e.printStackTrace();
                return "Exception: "+e.getMessage();
            }}
        @Override
        protected void onPostExecute(String s) {
            prgDialog.dismiss();
            if (s.equals("false")){
                Toast.makeText(register.this, "failed to add user ", Toast.LENGTH_LONG).show();
            }
            else {
                try {
                    JSONObject root = new JSONObject(s);
                    JSONArray array = root.getJSONArray("user_data");
                    JSONObject c = array.getJSONObject(0);
                    showAlert();
                    loguser(new passenger(c.getString("first_name"), c.getString("last_name"), c.getString("city"), c.getString("email"),
                            c.getString("nationality"), c.getInt("phone"), c.getInt("id_number"),
                            c.getString("username"), c.getString("password"), c.getInt("P_ID"), c.getString("id_card_img")));
                } catch (JSONException e) {
                    //Toast.makeText(register.this, "add user "+e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }
        public void showAlert(){

            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(register.this);
            builder.setTitle("Warning");
            builder.setMessage("Do you want to fill your family information  ..");

            builder.setCancelable(false);
            builder.setPositiveButton("Fill Now", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(register.this,addfamily.class));
                }
            });
            builder.setNegativeButton("Not Now", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(register.this, home.class));
                }
            });

            android.support.v7.app.AlertDialog dialog = builder.create();
            dialog.show();

        }
    }
    class BackGround1 extends AsyncTask<String, String, String> {
        register r;
        public BackGround1(register r){
            this.r=r;
        }
        @Override

        protected String doInBackground(String... params) {
            String email = params[0];
            String username = params[1];
            String data="";
            int tmp;
            try {
                URL url = new URL("http://abdr.000webhostapp.com/checkuseremail.php");
                String urlParams = "email="+email+"&username="+username+"&flag=p";
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
            }}
        @Override
        protected void onPostExecute(String s) {
            prgDialog.dismiss();
            if (s.equals("email")){
                Toast.makeText(register.this, "Failed to add user: duplicated email  ", Toast.LENGTH_LONG).show();
            }
            else if (s.equals("user")){
                Toast.makeText(register.this, "Failed to add user: duplicated username ", Toast.LENGTH_LONG).show();
            }
            else {
                prgDialog = ProgressDialog.show(register.this, "Wait Please...", "Creating Your Account...", false, true);
                prgDialog.setCancelable(false);
                prgDialog.setCanceledOnTouchOutside(false);
                BackGround b = new BackGround();
                b.execute(r.fname.getText().toString(), r.lname.getText().toString(), r.city.getSelectedItem().toString()
                        , r.email.getText().toString(), r.nationality.getText().toString(),r.phone.getText().toString(),
                        r.id.getText().toString(), r.username.getText().toString(), r.Pass.getText().toString(),r.img_btm);
            }
        }
    }

}
