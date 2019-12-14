package com.example.assignapp2019s1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/*
author:Yajing Wang
uid:u6565980
*/

public class ModeSelect extends Activity {
    //String username;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mode_menu);
         }
    public void clickH2CE(View v)
    {
        Intent intent = new Intent(ModeSelect.this, H2CActivity.class);
        intent.putExtra("flag",0);
        startActivity(intent);
        ModeSelect.this.finish();
    }
    public void clickH2CD(View v)
    {
        Intent intent = new Intent(ModeSelect.this, H2CActivity.class);
        intent.putExtra("flag",1);
        startActivity(intent);
        ModeSelect.this.finish();
    }


    public void clickH2HClient(View v)
    {
        Intent intent = new Intent(ModeSelect.this, H2HClient.class);
        startActivity(intent);
        ModeSelect.this.finish();

    }

    public void clickH2HServer(View v)
    {
        Intent intent = new Intent(ModeSelect.this, H2HServer.class);
        startActivity(intent);
        ModeSelect.this.finish();

    }
    public void clickTips(View v)
    {
        Intent intent = new Intent(ModeSelect.this, TipsActivity.class);
        startActivity(intent);
        ModeSelect.this.finish();
    }


}

