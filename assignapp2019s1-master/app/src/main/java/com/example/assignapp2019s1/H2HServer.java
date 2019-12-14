package com.example.assignapp2019s1;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/*
author:Le Fang
uid:u6590727
*/

public class H2HServer extends Activity {

    private TextView editText_1;
    private EditText editText_2;
    private ServerSocket serverSocket = null;
    private Socket socket =null;
    StringBuffer stringBuffer = new StringBuffer();

    private InputStream inputStream;
    BufferedWriter bw = null;
    ArrayList<Point> apw = new ArrayList<>();
    ArrayList<Point> apb = new ArrayList<>();
    H2HServerBoardview bv;



    public Handler handler = new Handler(){

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what){
                case 1:
                    editText_1.setText(msg.obj.toString());
                    break;
                case 2:
                    editText_2.setText(msg.obj.toString());
                    String tmp = msg.obj.toString();
                    System.out.println("receive from the client！！！！！!>>>>>>>>>>"+tmp);
                    int flag1= 0;
                    for(int i = 0;i < tmp.length();i++)
                    {

                        if(tmp.charAt(i) =='(')
                        {
                            //System.out.println(tmp.substring(i+1,i+2));
                            if(tmp.substring(i+1,i+2).equals("-"))
                            {
                                flag1 = i;
                            }
                        }
                    }
                    for(int i = 0;i < tmp.length();i++)
                    {
                        if(tmp.charAt(i) ==  '(')
                        {
                            System.out.println(tmp.substring(i+1,i+2));
                            if(!tmp.substring(i+1,i+2).equals("-"))
                            {

                                if(i < flag1) {
                                    //System.out.println("now add white!>>>>>>"+tmp.charAt(i + 1)+", "+tmp.charAt(i + 3));
                                    apw.add(new Point(tmp.charAt(i + 1)-'0',tmp.charAt(i + 3)-'0'));
                                }
                                else{
                                    //System.out.println("now add black!>>>>>>"+tmp.charAt(i + 1)+", "+tmp.charAt(i + 3));
                                    apb.add(new Point(tmp.charAt(i + 1)-'0',tmp.charAt(i + 3)-'0'));
                                }
                            }

                        }
                    }

                    System.out.println("");
                    bv.blacks = apb;
                    bv.whites = apw;
                    bv.count = 0;
                    bv.invalidate();
                    stringBuffer.setLength(0);
                    break;

            }

        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.h2hserver);

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().penaltyLog().penaltyDeath().build());

        editText_1 = (TextView) findViewById(R.id.et1);
        editText_2 = (EditText) findViewById(R.id.et2);
        bv = (H2HServerBoardview)findViewById(R.id.h2hboardView);
        receiveData();

        bv.setBoardViewListener(new H2HServerBoardview.BoardViewListener() {
            @Override
            public void BvTouch(ArrayList<Point> whites, ArrayList<Point> blacks,boolean winflag) {
                String str = "";
                for(int i = 0;i < whites.size();i++)
                {
                    str += "("+whites.get(i).x + ","+whites.get(i).y+"),";
                }
                str += "(-,-),";
                for(int i = 0;i < blacks.size();i++)
                {
                    str += "("+blacks.get(i).x + ","+blacks.get(i).y+"),";
                }
                //System.out.println("NOTE:server now has array:>>>>>>>>>>>>"+str);
                editText_2.setText(str);

                if(winflag)
                {
                    try {
                        bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        bw.write(editText_2.getText().toString()+"\n");
                        System.out.println("Judge success!!!! Send to the client>>>>>>>>>>>>>>>!!!!!!"+editText_2.getText().toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    try {
                        bw.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }


            }
        });

        Button bt = (Button)findViewById(R.id.button);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    bw.write(editText_2.getText().toString()+"\n");
                    System.out.println("send to the client>>>>>>>>>>>>>>>!!!!!!"+editText_2.getText().toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    bw.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void receiveData(){

        Thread thread = new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    serverSocket = new ServerSocket(8090);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                GetIpAddress.getLocalIpAddress(serverSocket);

                Message message_1 = handler.obtainMessage();
                message_1.what = 1;
                message_1.obj = "IP:" + GetIpAddress.getIP() + " PORT: " + GetIpAddress.getPort();
                handler.sendMessage(message_1);

                while (true){
                    try {
                        socket = serverSocket.accept();
                        inputStream  = socket.getInputStream();
                        StringBuffer stringBuffer = H2HServer.this.stringBuffer;
                        int len;
                        byte[] bytes = new byte[20];
                        boolean isString = false;
                        //When the input stream is closed, it will be equal to -1,
                        // which is not the case that after reading the data, it will be equal to -1 after reading the data.
                        while ((len = inputStream.read(bytes)) != -1) {
                                for (int i = 0; i < len; i++) {
                                    if (bytes[i] != '\0') {
                                        stringBuffer.append((char) bytes[i]);
                                    } else {
                                        isString = true;
                                        break;
                                    }
                                }
                                if (isString) {
                                    Message message_2 = handler.obtainMessage();
                                    message_2.what = 2;
                                    message_2.obj = stringBuffer;
                                    handler.sendMessage(message_2);
                                    isString = false;
                                }

                            }
                            //When this exception occurs, the connection on the client side has been disconnected

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
        bv.blacks.remove(bv.lastpiece);
        bv.count = 0;
        bv.invalidate();
    }


    /*When the return key is pressed, the corresponding socket resource is closed*/
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
