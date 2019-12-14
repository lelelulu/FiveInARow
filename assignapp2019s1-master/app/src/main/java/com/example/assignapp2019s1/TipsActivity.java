package com.example.assignapp2019s1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/*
author:Yajing Wang
uid:u6565980
*/
public class TipsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tips_activity);

    }

    public void Back(View v)
    {
        Intent intent = new Intent(TipsActivity.this, ModeSelect.class);
        startActivity(intent);
        TipsActivity.this.finish();
    }
}