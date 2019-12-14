package com.example.assignapp2019s1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;


/*
author:Le Fang
uid:u6590727
*/
public class PeopleAdapter extends ArrayAdapter<People> {

    private int resourceId;

    public PeopleAdapter(Context context,  int textViewResourceId, List<People> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
      People people = getItem(position);
      View view =  LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
      TextView name = (TextView)view.findViewById(R.id.people_name);

      TextView win = (TextView)view.findViewById(R.id.people_win);
      TextView lose = (TextView)view.findViewById(R.id.people_lose);

       name.setText(people.getName());

       win.setText(Integer.toString(people.getWin()));
       lose.setText(Integer.toString(people.getLose()));

      return view;
    }

}
