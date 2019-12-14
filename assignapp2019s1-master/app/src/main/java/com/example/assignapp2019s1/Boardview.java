package com.example.assignapp2019s1;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/*
author:Yajing Wang
uid:u6565980
*/
public class Boardview extends View {

    private Paint paint = new Paint();
    //the width of board should be the same as its height
    private int BoardWidth;
    /*the width of grid should be the same as its height
    a board consists of 9*9 grids*/
    private int start_X, end_X;
    private float GridWidth;
    //a board has 10 lines horizontally(as well as vertically)
    static public int LINES = 10;
    //change the width of pieces to fit the board
    private float ratio = 4 * 1.0f / 5;

    private Bitmap white, black;
    //check whether the current piece is white piece
    private boolean checkIsWhite = true;
    private boolean checkIsGameOver = false;
    //arraylist to storage white pieces
    private ArrayList<Point> whites = new ArrayList<>();
    private ArrayList<Point> blacks = new ArrayList<>();

    int[][] board = new int[LINES][LINES];
    int[] IWin = new int[2 * LINES * LINES];
    int[] CWin = new int[2 * LINES * LINES];
    int[][][] wins = new int[LINES][LINES][2 * LINES * LINES];

    int flag;



    int u = 0, v = 0;


    public Boardview(Context context, AttributeSet attrs) {
        super(context, attrs);
        InitWinState();
        initBoard();

    }

    private static final String INSTANCE = "instance";
    private static final String INSTANCE_GAME_OVER = "instance_game_over";
    private static final String INSTANCE_WHITE_ARRAY = "instance_white_array";
    private static final String INSTANCE_BLACK_ARRAY = "instance_black_array";

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(INSTANCE, super.onSaveInstanceState());
        bundle.putBoolean(INSTANCE_GAME_OVER, checkIsGameOver);

        bundle.putParcelableArrayList(INSTANCE_BLACK_ARRAY, blacks);
        bundle.putParcelableArrayList(INSTANCE_WHITE_ARRAY, whites);
        return bundle;
    }


    int total;

    public void InitWinState() {
        int num = 0;
        for (int i = 0; i < LINES; i++) {
            for (int j = 0; j <= 5; j++) {
                for (int k = 0; k < 5; k++) {
                    wins[i][j + k][num] = 1;

                }
                num++;
            }
        }
        for (int i = 0; i < LINES; i++) {
            for (int j = 0; j <= 5; j++) {
                for (int k = 0; k < 5; k++) {
                    wins[j + k][i][num] = 1;

                }
                num++;
            }
        }

        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 6; j++) {
                for (int k = 0; k < 5; k++) {
                    wins[i + k][j + k][num] = 1;

                }
                num++;
            }
        }

        for (int i = 0; i < 6; i++) {
            for (int j = 9; j > 3; j--) {
                for (int k = 0; k < 5; k++) {
                    wins[i + k][j - k][num] = 1;


                }
                num++;
            }
        }
        total = num;
        for (int i = 0; i < num; i++) {
            IWin[i] = 0;
            CWin[i] = 0;
        }

    }

    //initialize Board
    private void initBoard() {
        paint.setColor(Color.BLACK);
        //prevent some lines from being obscure
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        white = BitmapFactory.decodeResource(getResources(), R.mipmap.white);
        black = BitmapFactory.decodeResource(getResources(), R.mipmap.black);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawBoard(canvas);
        drawPieces(canvas);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        BoardWidth = w;
        GridWidth = BoardWidth * 1.0f / LINES;
        //eachpiece should change their width to fit the board
        int pieceWidth = (int) (GridWidth * ratio);
        white = Bitmap.createScaledBitmap(white, pieceWidth, pieceWidth, false);
        black = Bitmap.createScaledBitmap(black, pieceWidth, pieceWidth, false);

    }

    //draw Board which consists 9*9 grids
    private void drawBoard(Canvas canvas)//draw board
    {
        start_X = (int) (GridWidth / 2);
        end_X = (int) (BoardWidth - GridWidth / 2);
        for (int i = 0; i < LINES; i++) {
            int y = (int) ((0.5 + i) * GridWidth);
            //draw horizontal lines
            canvas.drawLine(start_X, y, end_X, y, paint);
            //draw vertical lines
            canvas.drawLine(y, start_X, y, end_X, paint);
        }
    }

    private void drawPieces(Canvas canvas) {
        for (int i = 0, n = whites.size(); i < n; i++) {//put white pieces to board
            Point wPoint = whites.get(i);
            float left = (wPoint.x + (1 - ratio) / 2) * GridWidth;
            float top = (wPoint.y + (1 - ratio) / 2) * GridWidth;

            canvas.drawBitmap(white, left, top, null);
        }
        for (int i = 0, n = blacks.size(); i < n; i++) {//put black pieces to the board
            Point bPoint = blacks.get(i);
            float left = (bPoint.x + (1 - ratio) / 2) * GridWidth;
            float top = (bPoint.y + (1 - ratio) / 2) * GridWidth;

            canvas.drawBitmap(black, left, top, null);
        }


    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (checkIsGameOver || !checkIsWhite)//when game is over, it will show nothing even if you touch the screen
        {
            System.out.println("return false!");
            return false;
        }
        int act = event.getAction();
        if (act == MotionEvent.ACTION_UP) {
            int x = (int) event.getX();
            int y = (int) event.getY();
            Point p = getPoint(x, y);
            //System.out.println("x"+p.x+"y"+p.y);

            if (p.y >= LINES) {
                return false;
            }
            if (whites.contains(p) || blacks.contains(p)) return false;

            if (checkIsWhite) {
                whites.add(p);
                invalidate();

                board[p.x][p.y] = 1;
                for (int f = 0; f < total; f++) {
                    if (wins[p.x][p.y][f] == 1) {
                        IWin[f]++;
                        CWin[f] = 6;
                        if (IWin[f] == 5) {
                            createAlertDiaglog("You win!");
                            writeToHistory(false);
                            checkIsGameOver = true;
                        }
                    }
                }

                if (!checkIsGameOver) {
                    checkIsWhite = !checkIsWhite;
                    computerAI();
                }

            }
            invalidate();

            return true;
        }
        return true;
    }

    private Point getPoint(int x, int y) {
        int X = (int) (x / GridWidth);
        int Y = (int) (y / GridWidth);

        return new Point(X, Y);
    }
    //create AlertDiaglog
    public void createAlertDiaglog(String text) {
        AlertDialog.Builder diaglog = new AlertDialog.Builder(getContext());
        diaglog.setTitle("Game Over: " + text);
        diaglog.setMessage("Restart Again?");
        diaglog.setCancelable(false);
        diaglog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                start();
            }
        });
        diaglog.setNegativeButton("EXIT", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                System.exit(0);
            }
        });
        diaglog.show();
    }

    private void computerAI() {
        // TODO Auto-generated method stub
        int max = 0;

        int[][] myScore = new int[10][10];
        int[][] computerScore = new int[10][10];

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                myScore[i][j] = 0;
                computerScore[i][j] = 0;
            }
        }

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if (board[i][j] == 0) {
                    for (int k = 0; k < total; k++) {
                        if (wins[i][j][k] == 1) {
                            if (flag == 1) {//easy
                                switch (IWin[k]) {
                                    case 1:
                                        myScore[i][j] += 220;
                                        break;
                                    case 2:
                                        myScore[i][j] += 420;
                                        break;
                                    case 3:
                                        myScore[i][j] += 2000;
                                        break;
                                    case 4:
                                        myScore[i][j] += 22000;
                                        break;

                                }

                                switch (CWin[k]) {
                                    case 1:
                                        myScore[i][j] += 200;
                                        break;
                                    case 2:
                                        myScore[i][j] += 400;
                                        break;
                                    case 3:
                                        myScore[i][j] += 2000;
                                        break;
                                    case 4:
                                        myScore[i][j] += 10000;
                                        break;

                                }

                            } else {  //difficult
                                switch (IWin[k]) {
                                    case 1:
                                        myScore[i][j] += 200;
                                        break;
                                    case 2:
                                        myScore[i][j] += 400;
                                        break;
                                    case 3:
                                        myScore[i][j] += 2000;
                                        break;
                                    case 4:
                                        myScore[i][j] += 10000;
                                        break;

                                }

                                switch (CWin[k]) {
                                    case 1:
                                        myScore[i][j] += 220;
                                        break;
                                    case 2:
                                        myScore[i][j] += 420;
                                        break;
                                    case 3:
                                        myScore[i][j] += 2000;
                                        break;
                                    case 4:
                                        myScore[i][j] += 22000;
                                        break;

                                }
                            }
                        }
                    }

                    if (myScore[i][j] > max) {
                        max = myScore[i][j];
                        u = i;
                        v = j;
                    } else if (myScore[i][j] == max) {
                        if (computerScore[i][j] > computerScore[u][v]) {
                            u = i;
                            v = j;
                        }
                    }

                    if (computerScore[i][j] > max) {
                        max = computerScore[i][j];
                        u = i;
                        v = j;
                    } else if (computerScore[i][j] == max) {
                        if (myScore[i][j] > myScore[u][v]) {
                            u = i;
                            v = j;
                        }
                    }

                }
            }
        }
        board[u][v] = 2;
        blacks.add(new Point(u, v));
        invalidate();

        for (int f = 0; f < total; f++) {
            if (wins[u][v][f] == 1) {
                CWin[f]++;
                IWin[f] = 6;
                if (CWin[f] == 5) {
                    createAlertDiaglog("Computer Wins!");
                    writeToHistory(true);
                    checkIsGameOver = true;
                }
            }
        }

        if (!checkIsGameOver) {
            checkIsWhite = !checkIsWhite;
        }
    }

    //write win_lose pattern to history
    public void writeToHistory(boolean computer_win) {

        FileIO io = new FileIO(getContext());
        String read = io.load("userHistoryData");
        String[] info = read.split("#");


        String readusername = io.load("currentUser");
        for (int i = 0; i < info.length - 1; i += 4) {
            if (info[i].equals(readusername)) {
                if (computer_win)
                    info[i + 3] = Integer.toString(Integer.parseInt(info[i + 3]) + 1);
                else info[i + 2] = Integer.toString(Integer.parseInt(info[i + 2]) + 1);

            }

        }
        String content = "";
        for (int i = 0; i < info.length - 1; i += 4) {
            String tmp = info[i] + "#" + info[i + 1] + "#" + info[i + 2] + "#" + info[i + 3] + "#";
            content += tmp;
        }
        boolean status = io.privatewrite("userHistoryData", content);
        if (status) {
            System.out.println("save win state successfully!" + content);
        }
    }

    public void start() {
        u = 0;
        v = 0;
        total = 0;

        whites.clear();
        blacks.clear();

        checkIsWhite = true;
        checkIsGameOver = false;

        wins = new int[10][10][200];
        board = new int[10][10];
        IWin = new int[200];
        CWin = new int[200];

        InitWinState();
        invalidate();
    }



}




