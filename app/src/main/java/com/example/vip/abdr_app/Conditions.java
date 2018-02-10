package com.example.vip.abdr_app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class Conditions extends BaseActivity {

    TextView con , txt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_conditions);
        con = (TextView)findViewById(R.id.textView_con);
        txt = (TextView)findViewById(R.id.textView_conrul);
        txt.setText("Rules and Conditions");
        con.setText(R.string.Rules);

    }
}
