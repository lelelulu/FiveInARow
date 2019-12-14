package com.example.assignapp2019s1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/*
author:Yajing Wang
uid:u6565980
*/
public class History extends Activity {

    private List<People> peoleList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_layout);

        TextView tv = (TextView)findViewById(R.id.textView);

        FileIO io = new FileIO(getApplicationContext());
        String read = io.load("userHistoryData");
        if(read.length() == 0)
            tv.setText("no history!");

        String[] info = read.split("#");
        setListViewAdapter(info);

    }

    public void Back(View v)
    {

        Intent intent = new Intent(History.this, ModeSelect.class);
        startActivity(intent);
        History.this.finish();
    }

    public void Clean(View v)
    {
        FileIO io = new FileIO(getApplicationContext());
        boolean status = io.privatewrite("userHistoryData","");
        if(status) {
            Toast t = Toast.makeText(getApplicationContext(),"Clean history successful!",Toast.LENGTH_LONG);
            t.show();
        }

    }



    public void setListViewAdapter(String[] info)
    {
        for(int i = 0 ;i < info.length -1;i+=4)//store the information of user'name and ...
        {
            String name = info[i];
            String psw = info[i+1];
            int win = Integer.parseInt(info[i+2]);
            int lose = Integer.parseInt(info[i+3]);
            peoleList.add(new People(name,psw,win,lose));
        }

        PeopleAdapter adapter = new PeopleAdapter(History.this,R.layout.people_items,peoleList);
        ListView lv = (ListView)findViewById(R.id.list_View);
        lv.setAdapter(adapter);
    }

}
