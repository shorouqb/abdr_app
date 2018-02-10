package com.example.vip.abdr_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class supportinfo extends BaseActivity implements View.OnClickListener {
    ImageView add;
    int pos=0;
    TextView bref,title,stat,answ,des,line,anstv;
    String []titles;
    String []ref;
    String []desc;
    String []answer;
    String []state;
    int []id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supportinfo);
        add = (ImageView) findViewById(R.id.add);
        add.setOnClickListener(this);


        Intent i = getIntent();

        pos=i.getExtras().getInt("pos");
        titles=new String [i.getStringArrayExtra("titles").length];
        titles=i.getStringArrayExtra("titles");

        ref=new String [i.getStringArrayExtra("ref").length];
        ref=i.getStringArrayExtra("ref");

        desc=new String [i.getStringArrayExtra("desc").length];
        desc=i.getStringArrayExtra("desc");

        answer=new String [i.getStringArrayExtra("answer").length];
        answer=i.getStringArrayExtra("answer");

        state=new String [i.getStringArrayExtra("state").length];
        state=i.getStringArrayExtra("state");



        bref=(TextView)findViewById(R.id.bookref);
        title=(TextView)findViewById(R.id.title);
        stat=(TextView)findViewById(R.id.state);
        answ=(TextView)findViewById(R.id.ans);
        des=(TextView)findViewById(R.id.com);
        line=(TextView)findViewById(R.id.v);

        anstv=(TextView)findViewById(R.id.s4);
        bref.setText(ref[pos]);
        title.setText(titles[pos]);
        stat.setText(state[pos]);
        des.setText(desc[pos]);

        if(state[pos].equals("Answerd")){
            answ.setText(answer[pos]);
        }
        else{
          answ.setVisibility(View.GONE);
          line.setVisibility(View.GONE);
          anstv.setVisibility(View.GONE);
        }

    }
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.add:
                Intent intent = new Intent(supportinfo.this,addsupport.class);
                intent.putExtra("ref",ref[pos]);
                startActivity(intent);
                break;


        }

    }

}

