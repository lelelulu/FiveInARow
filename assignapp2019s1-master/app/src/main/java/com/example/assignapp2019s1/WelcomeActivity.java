package com.example.assignapp2019s1;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/*
author:Yajing Wang
uid:u6565980
*/

public class WelcomeActivity extends Activity {

    EditText pwd;
    EditText name;
    CheckBox checkBox;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_activity);

        pwd = (EditText) findViewById(R.id.pwd);
        name = (EditText) findViewById(R.id.name);
        checkBox = (CheckBox) findViewById(R.id.checkBox);

        pwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pwd.setHint(null);
            }
        });
        name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name.setHint(null);
            }
        });


        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //show  password
                    pwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    //hide password
                    pwd.setTransformationMethod(PasswordTransformationMethod.getInstance());

                }

            }
        });

    }


    public boolean recordCurrentUser(String username) {
        FileIO io = new FileIO(getApplicationContext());
        boolean status = io.privatewrite("currentUser", username);
        if (status) return true;
        else return false;
    }

    public void login(View v) {

        String username = name.getText().toString();

        recordCurrentUser(username);

        String password = pwd.getText().toString();
        String content = username + "#" + password + "#" + 0 + "#" + 0 + "#";

        int flag = checkexist(username, password);

        if (flag == 1) {
            createProgressDialog(username, true);//no need to store!
        } else if( flag == 0 ) {

            FileIO ioo = new FileIO(getApplicationContext());
            boolean status = ioo.write("userHistoryData", content);

            if (status) {
                createProgressDialog(username, false);
            } else {
                Toast.makeText(this, "save failed", Toast.LENGTH_SHORT).show();
            }
        }else if(flag == 2){
            Toast t = Toast.makeText(getApplicationContext(),"wrong password!",Toast.LENGTH_LONG);
            t.show();
        }


    }
    //check whether this player exists
    public int checkexist(String username, String password) {
        int flag = 0;
        FileIO io = new FileIO(getApplicationContext());
        String read = io.load("userHistoryData");
        if (read.length() == 0) return flag;
        String[] info = read.split("#");

        for (int i = 0; i < info.length - 1; i += 4)//store the information of user'name and password to check new or old user
        {
            String name = info[i];
            String psw = info[i + 1];
            if (username.equals(name) && password.equals(psw)) flag = 1;
            if (username.equals(name) && !password.equals(psw)) flag = 2;
            //existPeoleList.add(new People(name,psw));
        }
        return flag;

    }

    //create progress diaglog
    public void createProgressDialog(final String username, boolean exist) {
        final ProgressDialog pDialog = new ProgressDialog(WelcomeActivity.this);
        if (exist) {
            pDialog.setTitle("Welcome to come back! " + username);
        } else {
            pDialog.setTitle("Welcome! new user! " + username);

        }
        pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pDialog.setMessage("Loading data...");
        pDialog.setMax(100);
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);

        pDialog.show();

        new Thread(new Runnable() {
            @Override
            public void run() {

                int i = 0;
                while (i <= 100) {
                    try {
                        Thread.sleep(50);
                        //Updates the progress of a progress bar in a child thread
                        pDialog.incrementProgressBy(1);
                        i++;
                    } catch (InterruptedException e) {

                    }

                }
                pDialog.dismiss();
                Intent intent = new Intent(WelcomeActivity.this, ModeSelect.class);
                startActivity(intent);
                WelcomeActivity.this.finish();
            }
        }).start();


    }

}


