package com.example.assignapp2019s1;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

/*
author:Le Fang
uid:u6590727
*/

public class H2HClient extends AppCompatActivity {

    private EditText editText_ip, editText_data;
    private OutputStream outputStream = null;
    private Socket socket = null;
    private String ip;
    private String data;
    private boolean socketStatus;//= false;
    private H2HClientBoardview bv = null;
    ArrayList<Point> apw = new ArrayList<>();
    ArrayList<Point> apb = new ArrayList<>();
    private boolean run = false;
    private final Handler handler = new Handler();

    private final Runnable task = new Runnable() {
        @Override
        public void run() {
            if (run) {
                receiveData();
                handler.postDelayed(this, 1000);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.h2hclient);

        run = true;
        handler.postDelayed(task, 1000);


        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().penaltyLog().penaltyDeath().build());

        editText_ip = (EditText) findViewById(R.id.et_ip);
        editText_data = (EditText) findViewById(R.id.et_data);

        bv = (H2HClientBoardview)findViewById(R.id.H2HClientboardView);
        Button connect = (Button) findViewById(R.id.button);


        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ip = editText_ip.getText().toString();
                if (ip.equals("")) {
                    Toast.makeText(H2HClient.this, "please input Server IP", Toast.LENGTH_SHORT).show();
                }

                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        super.run();

                        if (!socketStatus) {
                            try {
                                socket = new Socket(ip, 8090);
                            } catch (Exception e) {
                                System.out.println("cannot create socket!!!since e" + e);
                            }
                            if (socket == null)
                                System.out.println("connect fail!");
                            else {
                                socketStatus = true;

                            }

                        }

                    }
                };
                thread.start();
            }
        });

        //Judge success!!!! Send data to the server
        bv.setBoardViewListener(new H2HClientBoardview.BoardViewListener() {
            @Override
            public void BvTouch(ArrayList<Point> whites, ArrayList<Point> blacks, boolean winflag) {
                String str = "";

                for (int i = 0; i < whites.size(); i++) {
                    str += "(" + whites.get(i).x + "," + whites.get(i).y + "),";
                }
                if (whites.size() != 0)
                    str += "(-,-),";

                for (int i = 0; i < blacks.size(); i++) {
                    str += "(" + blacks.get(i).x + "," + blacks.get(i).y + "),";
                }
                //System.out.println("NOTE:>>>>>>>>>>>>" + str);
                editText_data.setText(str);

                if (winflag)//if someone wins, it will send message
                {

                    if (socketStatus) {
                        try {
                            outputStream = socket.getOutputStream();
                            data = editText_data.getText().toString() + '\0';
                            outputStream.write(data.getBytes());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }

            }
        });

    }

    //receive data from the server
    public void receiveData() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                super.run();

                if (socketStatus) {
                    BufferedReader br = null;
                    try {
                        br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        String mess = br.readLine();
                        int flag1 = 0;
                        for (int i = 0; i < mess.length(); i++) {

                            if (mess.charAt(i) == '(') {
                                if (mess.substring(i + 1, i + 2).equals("-")) {
                                    flag1 = i;

                                }

                            }
                        }
                        for (int i = 0; i < mess.length(); i++) {

                            if (mess.charAt(i) == '(') {
                                System.out.println(mess.substring(i + 1, i + 2));
                                if (!mess.substring(i + 1, i + 2).equals("-")) {

                                    if (i < flag1) {
                                        apw.add(new Point(mess.charAt(i + 1) - '0', mess.charAt(i + 3) - '0'));
                                    } else {
                                        apb.add(new Point(mess.charAt(i + 1) - '0', mess.charAt(i + 3) - '0'));
                                    }
                                }
                            }
                        }
                        bv.blacks = apb;
                        bv.whites = apw;
                        bv.count = 0;
                        bv.invalidate();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        };
        thread.start();

    }

    //sent data to the server
    public void send(View view) {

        data = editText_data.getText().toString();
        if (data.equals("")) {
            Toast.makeText(H2HClient.this, "please input Sending Data", Toast.LENGTH_SHORT).show();
        } else {
            data = data + '\0';
        }

        Thread thread = new Thread() {
            @Override
            public void run() {
                super.run();
                if (socketStatus) {
                    try {
                        outputStream = socket.getOutputStream();
                        outputStream.write(data.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        thread.start();

    }

    public void Regret(View v)
    {
        bv.whites.remove(bv.lastpiece);
        bv.count = 0;
        bv.invalidate();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        try {
            outputStream.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}