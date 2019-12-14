package com.example.assignapp2019s1;

/*
author:Le Fang
uid:u6590727
*/
public class People {

    private String name;
    private String psw;
    private int win;
    private int lose;

    public People(String name,String psw,int win,int lose)
    {
        this.name = name;
        this.win = win;
        this.lose = lose;
        this.psw = psw;
    }

    public String getName()
    {
        return name;

    }

    public String getPsw()
    {
        return psw;
    }

    public int getWin()
    {
        return win;

    }

    public int getLose()
    {
        return lose;

    }


}
