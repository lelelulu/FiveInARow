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
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;


/*
author:Le Fang
uid:u6590727
*/
public class H2HServerBoardview extends View {

    int count = 0;
    private Paint paint = new Paint();
    //the width of board should be the same as its height
    private int BoardWidth;
    /*the width of grid should be the same as its height
    a board consists of 9*9 grids*/
    private int start_X, start_Y, end_X, end_Y;
    private float GridWidth;
    //a board has 10 lines horizontally(as well as vertically)
    private int LINES = 9;
    //change the width of pieces to fit the board
    private float ratio = 4 * 1.0f / 5;

    private Bitmap white = BitmapFactory.decodeResource(getResources(), R.mipmap.white);
    private Bitmap black = BitmapFactory.decodeResource(getResources(), R.mipmap.black);

    public Point lastpiece = new Point();

    //check whether the current piece is white piece
    private boolean checkIsGameOver = false;
    //arraylist to storage white pieces
    public ArrayList<Point> whites = new ArrayList<>();
    public ArrayList<Point> blacks = new ArrayList<>();

    public H2HServerBoardview(Context context, AttributeSet attrs) {
        super(context, attrs);
        initBoard();
    }
    private BoardViewListener local;


    public void setBoardViewListener(BoardViewListener bvlistener){
        this.local = bvlistener;
    }
    public BoardViewListener getBoardViewListener(){
        return this.local;
    }

    public interface BoardViewListener{
        void BvTouch(ArrayList<Point> whites, ArrayList<Point> blacks, boolean winflag);

    }

    //initialize Board
    private void initBoard() {
        paint.setColor(Color.BLACK);
        //prevent some lines from being obscure
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        System.out.println("!!!!!!!!!!!!!draw board!!!!!!!");
        drawBoard(canvas);
        drawPiece(canvas);
        checkGameOver();
    }


    //draw Board which consists 9*9 grids
    private void drawBoard(Canvas canvas)//draw board
    {
        initX();
        for (int i = 0; i < LINES; i++) {
            int y = (int) ((0.5 + i) * GridWidth);
            //draw horizontal lines
            canvas.drawLine(start_X, y, end_X, y, paint);
            //draw vertical lines
            canvas.drawLine(y, start_X, y, end_X, paint);
        }
    }

    //design the coordinate of board
    void initX() {
        start_X = (int) (GridWidth / 2);
        end_X = (int) (BoardWidth - GridWidth / 2);
        System.out.println("start_X "+start_X +"end_X "+end_X+"start_Y "+start_Y+"end_Y "+end_Y);
    }

    private void drawPiece(Canvas canvas) {
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
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        BoardWidth = w;
        GridWidth = BoardWidth * 1.0f / LINES;
        //eachpiece should change their width to fit the board
        int pieceWidth = (int) (GridWidth * ratio);
        white = Bitmap.createScaledBitmap(white, pieceWidth, pieceWidth, false);
        black = Bitmap.createScaledBitmap(black, pieceWidth, pieceWidth, false);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(checkIsGameOver)//when game is over, it will show nothing even if you touch the screen
        {
            return false;
        }
        int act = event.getAction();
        if (act == MotionEvent.ACTION_UP) {
            int x = (int) event.getX();
            int y = (int) event.getY();
            Point p = getPoint(x, y);
            System.out.println("x"+p.x+"y"+p.y);

            if (p.y >= LINES) {
                return false;
            }
            if (whites.contains(p) || blacks.contains(p)) return false;

            if(count == 0)
            {
                blacks.add(p);
                lastpiece = p;
                count++;
            }

            if(getBoardViewListener()!=null){
                getBoardViewListener().BvTouch(whites,blacks,false);
            }

            invalidate();//update
        }
        return true;
    }


    private Point getPoint(int x, int y) {
        int X = (int) (x / GridWidth);
        int Y = (int) (y / GridWidth);

        return new Point(X, Y);
    }

    final static int Horizontal_Tag = 0, Vertical_Tag = 1, Left_diagonal_Tag = 2, Right_diagonal_Tag = 3;
    final static int[] Tags = {Horizontal_Tag, Vertical_Tag, Left_diagonal_Tag, Right_diagonal_Tag};
    Point point1, point2;

    public void checkGameOver() {
        boolean whiteWin = checkWinner(whites);
        boolean blackWin = checkWinner(blacks);



        if (whiteWin || blackWin) {
            checkIsGameOver = true;
            String text = whiteWin ? "you lose!" : "you win";
            //Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();

            if(getBoardViewListener()!=null){
                getBoardViewListener().BvTouch(whites,blacks,true);
            }

            createAlertDiaglog(text,blackWin);

        }

    }

    public void createAlertDiaglog(String text,boolean blackwin)
    {
        AlertDialog.Builder diaglog = new AlertDialog.Builder(getContext());
        diaglog.setTitle("Game Over: "+text);
        if(blackwin)
            diaglog.setIcon(R.drawable.congrats);

        diaglog.setCancelable(true);
        diaglog.setNegativeButton("EXIT", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                System.exit(0);
            }
        });
        diaglog.show();

    }

    //check winner by detecting whether there exists five pieces with the same color
    //in one of these four orientations (horizontal, vertical, left diagonal and right diagnal)
    public boolean checkWinner(List<Point> points) {
        for (Point point : points) {
            int x = point.x;
            int y = point.y;
            for (int i = 0; i < Tags.length; i++) {
                if (check(x, y, points, Tags[i])) {
                    return true;
                }
            }
        }
        return false;
    }
    //check the pieces in current piece's left side or right side
    /*****clarification*******
     Whatever orientation we check, for each current point, we will create point1 and point2 for four times virtually,
     then, every time we find point1 or point2 exists in pieces Arraylist actually, we will make count++
     if count = 5 ultimately, it means 5 pieces in a line.

     let me illustrate this by using graph:(note:point1 denoted by 1, point2 denoted by 2, current point denoted by C)

     a.horizontal check:
     1 1 1 1 C 2 2 2 2

     b.vertical check:
     1
     1
     1
     1
     C
     2
     2
     2
     2

     c.left_diagonal check:
     1
     *1
     **1
     ***1
     ****C
     *****2
     ******2
     *******2
     ********2

     d.right_diagonal_tag check:
     *********2
     ********2
     *******2
     ******2
     *****C
     ****1
     ***1
     **1
     *1

     ******************/

    private boolean check(int x, int y, List<Point> PList, int tag) {
        int count = 1;
        for (int i = 1; i < 5; i++) {
            if (tag == Horizontal_Tag) {
                point1 = new Point(x - i, y);
                point2 = new Point(x + i, y);
            } else if (tag == Vertical_Tag) {
                point1 = new Point(x, y - i);
                point2 = new Point(x, y + i);
            } else if (tag == Left_diagonal_Tag) {
                point1 = new Point(x - i, y + i);
                point2 = new Point(x + i, y - i);
            } else if (tag == Right_diagonal_Tag) {
                point1 = new Point(x + i, y + i);
                point2 = new Point(x - i, y - i);
            }

            if ((PList.contains(point1) && !PList.contains(point2)) || (!PList.contains(point1) && PList.contains(point2))) {
                count++;
            } else if ((PList.contains(point1) && PList.contains(point2))) {
                count += 2;
            } else break;
        }

        if (count == 5) {
            return true;
        }
        return false;
    }

    public void start() {
        whites.clear();
        blacks.clear();
        checkIsGameOver = false;

        invalidate();
    }


}







