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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class addfamily extends BaseActivity implements View.OnClickListener {
    localuser LU;
    ImageView reg,cans,btnTakePhoto;
    EditText  name, id;
    Spinner rel;
    ImageView imgTakenPhoto;
    private static final int CAM_REQUEST = 1313;
    private static final int PICK_IMAGE_REQUEST = 1;
    String img_btm;
    ProgressDialog prgDialog;
    private Bitmap bitmap;
    private Uri filePath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addfamily);

        name = (EditText) findViewById(R.id.name);

        id = (EditText) findViewById(R.id.idno);
        rel = (Spinner) findViewById(R.id.rel);

        btnTakePhoto = (ImageView) findViewById(R.id.button1);
        imgTakenPhoto = (ImageView) findViewById(R.id.imageview1);

        cans= (ImageView)findViewById(R.id.cans);
        cans.setOnClickListener(this);

        btnTakePhoto.setOnClickListener(new btnTakePhotoClicker());
        reg = (ImageView) findViewById(R.id.btnadd);
        reg.setOnClickListener(this);
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

            AlertDialog.Builder builder = new AlertDialog.Builder(addfamily.this);

            builder.setTitle("Id image");
            ImageView image = new ImageView(addfamily.this);
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

            case R.id.btnadd:
                if (name.getText().toString().equals("")||id.getText().toString().equals("")||rel.getSelectedItem().toString().equals("Relationship:")
                ||img_btm.equals(" ")) {
                    Toast.makeText(addfamily.this, "Fill the missing field", Toast.LENGTH_LONG).show();

                }
                else{

                     if (id.getText().toString().length()!=10){
                        Toast.makeText(addfamily.this, "National id wrong ", Toast.LENGTH_LONG).show();
                    }

                    else{
                        prgDialog = ProgressDialog.show(addfamily.this, "Wait Please...", "Adding Family Member...", false, true);
                         prgDialog.setCancelable(false);
                         prgDialog.setCanceledOnTouchOutside(false);
                        BackGround b = new BackGround();
                        b.execute(name.getText().toString(),
                                id.getText().toString(), rel.getSelectedItem().toString(),img_btm);
                    }
                }

                break;
            case R.id.cans:
                startActivity(new Intent(this,familymain.class));
                break;

        }


    }
    class BackGround extends AsyncTask<Object, String, String> {

        @Override
        protected String doInBackground(Object... params) {
            String name = (String)params[0];
            long id = Long.parseLong((String)params[1]);
            String rel = (String)params[2];
            String img = (String) params[3];

            String data = "";
            int tmp;

            try {
                URL url = new URL("http://abdr.000webhostapp.com/addfamily.php");
                String urlParams = "name=" + name + "&id_number=" + id + "&rel=" + rel + "&p_id=" +
                        LU.getLoggedInUser().P_ID+"&idimg="+URLEncoder.encode(img, "UTF-8");
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
            if (prgDialog!=null)
            prgDialog.dismiss();
           //Toast.makeText(addfamily.this, s, Toast.LENGTH_LONG).show();
            if(s.equals("false")) {
                Toast.makeText(addfamily.this, "Error in adding family", Toast.LENGTH_LONG).show();


            } else {
                Intent i = new Intent(addfamily.this, familymain.class);
                startActivity(i);


            }
        }

    }

}
