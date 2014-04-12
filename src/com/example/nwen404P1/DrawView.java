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
        int diffX = w / 90;
        int diffY = h / 60;


        // there is 87 points in total

        float x = 0;
        while (x < w) {
            //canvas.drawLine(x, 0, x, h, paint);
            x = x + diffX;
        }

        float y = 0;
        while (y < h) {
            //canvas.drawLine(0, y, w, y, paint);
            y = y + diffY;
        }
        Paint red = new Paint();
        red.setColor(Color.RED);
        pList = new ArrayList<Point>();
        for (AccessPoint ap : aps) {
            pList.add(new Point(ap.getX(), ap.getY()));
            canvas.drawText(ap.getX() + "," + ap.getY(), ap.getX() * diffX + 5, ap.getY() * diffY, paint);
            canvas.drawCircle(ap.getX() * diffX, ap.getY() * diffY, 6, red);
        }

        drawShape(canvas);

    }

    public void setAps(ArrayList<AccessPoint> aps) {
        this.aps = aps;
    }

    public int distanceBetweenPoints(Point p1, Point p2) {
        int dist = (int) Math.sqrt(Math.pow(p1.x - p2.x, 2) + Math.pow(p1.y - p2.y, 2));
        return dist;
    }

    p


    /**
     * Calculates the centroid based off an array of points
     *
     * @return
     */
    public Point centroid(ArrayList<Point> points) {
        int size = points.size();
        int totalX = 0;
        int totalY = 0;
        for (Point p : points) {
            totalX += p.x;
            totalY += p.y;
        }
        return new Point(totalX/size, totalY/size);
    }


}