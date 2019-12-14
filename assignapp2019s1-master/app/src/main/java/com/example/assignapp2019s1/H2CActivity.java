package com.example.assignapp2019s1;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

/*
author:Yajing Wang
uid:u6565980
*/

public class H2CActivity extends AppCompatActivity {


    static String username;
    TextView uwin ;
    TextView ulose;
    int flag = 0;
    Boardview bv;
    private boolean run = false;
    private final Handler handler = new Handler();

    private final Runnable task = new Runnable() {
        @Override
        public void run() {
            if (run) {
                setwin_lose();
                handler.postDelayed(this, 1000);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_h2c);

        run = true;
        handler.postDelayed(task, 1000);

        bv = (Boardview)findViewById(R.id.boardView);
        Intent intent = getIntent();
        flag = intent.getIntExtra("flag",0);
        bv.flag = flag;
        FileIO io = new FileIO(getApplicationContext());
        String read = io.load("currentUser");
        username = read;
        TextView tv = (TextView) findViewById(R.id.userinfo);
        uwin = (TextView) findViewById(R.id.userwin);
        ulose = (TextView) findViewById(R.id.userlose);

        setwin_lose();
        if(flag == 0){
        tv.setText(read + " VS Computer" + "(easy)");
        }
        else
        {
            tv.setText(read + " VS Computer" + "(difficult)");
        }
    }

    public void history_click(View v) {
        Intent intent = new Intent(H2CActivity.this, History.class);
        startActivity(intent);
        H2CActivity.this.finish();
    }

    //refresh the win_lose pattern
    public void setwin_lose() {
        FileIO io = new FileIO(getApplicationContext());
        String read = io.load("userHistoryData");
        if (read.length() != 0) {
            String[] info = read.split("#");
            for (int i = 0; i < info.length - 1; i += 4)//store the information of user'name and password to check new or old user
            {
                String name = info[i];
                if (username.equals(name)) {
                    uwin.setText("WIN:"+info[i + 2]);
                    ulose.setText("LOSE:"+info[i + 3]);
                }
            }
        }
    }

    //back to H2CActivity
    public void Back(View v)
    {
        Intent intent = new Intent(H2CActivity.this,ModeSelect.class);
        startActivity(intent);
        H2CActivity.this.finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        bv.start();
    }
}
