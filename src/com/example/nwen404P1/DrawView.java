package com.example.nwen404P1;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

public class DrawView extends View {
    Paint paint = new Paint();
    ArrayList<AccessPoint> aps = new ArrayList<AccessPoint>();
    ArrayList<Point> pList;
    int w;
    int h;
    int diffX;
    int diffY;

    public DrawView(Context context) {
        super(context);
        paint.setColor(Color.BLACK);

    }

    @Override
    public void onDraw(Canvas canvas) {
        //Assumption here that everything is done in landscape
        int w = getWidth();
        int h = getHeight();
        int diffX = w/90;
        int diffY = h/60;


        // there is 87 points in total

        float x = 0;
        while(x<w){
           //canvas.drawLine(x, 0, x, h, paint);
           x=x+diffX;
        }

        float y=0;
        while(y<h){
            //canvas.drawLine(0, y, w, y, paint);
            y=y+diffY;
        }
        Paint red = new Paint();
        red.setColor(Color.RED);
        pList = new ArrayList<Point>();
        for (AccessPoint ap : aps) {
            pList.add(new Point(ap.getX(), ap.getY()));
            canvas.drawText(ap.getX()+ "," + ap.getY(), ap.getX()*diffX+5, ap.getY()*diffY, paint);
            canvas.drawCircle(ap.getX() * diffX, ap.getY() * diffY, 6, red);
        }
        canvas.drawLine(pList.get(0).x*diffX, pList.get(0).y*diffY, pList.get(1).x*diffX, pList.get(1).y*diffY, paint);
        canvas.drawText("d="+distanceBetweenPoints(), middlePoint(pList.get(0), pList.get(1)).x*diffX,middlePoint(pList.get(0), pList.get(1)).y*diffY, paint);
    }

    public void setAps(ArrayList<AccessPoint> aps){
        this.aps = aps;
    }

    public int distanceBetweenPoints(){
        float x1 = pList.get(0).x;
        float x2 = pList.get(1).x;
        float y1 = pList.get(0).y;
        float y2 = pList.get(1).y;
        int dist = (int) Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
        return  dist;
    }

    public Point middlePoint (Point p1, Point p2) {
        return new Point ((p1.x + p2.x / 2), (p1.y + p2.y) / 2);
    }



}