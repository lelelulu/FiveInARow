package com.example.assignapp2019s1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

/*
author:Yajing Wang
uid:u6565980
*/

public class FaceActivity extends Activity {
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.face_activity);

    }

    public void Start(View v)
    {
        Intent intent = new Intent(FaceActivity.this, WelcomeActivity.class);
        //intent.putExtra("username",username);
        startActivity(intent);
        FaceActivity.this.finish();

    }

}
