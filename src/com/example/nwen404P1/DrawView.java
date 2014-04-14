package com.example.nwen404P1;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import math.geom2d.Point2D;
import math.geom2d.conic.Circle2D;

import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.util.*;

public class DrawView extends View {
    private static final int showX = 300;
    Paint paint = new Paint();
    ArrayList<AccessPoint> aps = new ArrayList<AccessPoint>();
    ArrayList<Point> pList;
    int w;
    int h;
    int diffX;
    int diffY;
    Map<String, Double> macToLevel;
    Map<String, Integer> macToFreq;
    TextView text;
    List<String> macs;
    int floor = 0;
    HashMap<AccessPoint, Double> filterMac;
    double total = 0;

    public DrawView(Context context) {
        super(context);
        paint.setColor(Color.BLACK);
        paint.setTextSize(32);
        final Handler h = new Handler();
        final WifiManager mainWifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        filterMac = new HashMap<AccessPoint, Double>();
        macToFreq = new HashMap<String, Integer>();
        macToLevel = new HashMap<String, Double>();
        total = 0;

        final double factor = 0.50;


        macs = new LinkedList<String>();

        Runnable r = new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                mainWifi.startScan();

                List<String> macsFound = new LinkedList<String>();

                for (ScanResult scanResult : mainWifi.getScanResults()) {
                    if (!macToLevel.containsKey(scanResult.BSSID)) {
                        macToLevel.put(scanResult.BSSID, (double) scanResult.level);
                    } else {
                        double prev = macToLevel.get(scanResult.BSSID);
                        macToLevel.put(scanResult.BSSID, factor * prev + (1.0 - factor) * (double) scanResult.level);
                    }

                    if (!macs.contains(scanResult.BSSID)) {
                        macs.add(scanResult.BSSID);
                    }

                    macToFreq.put(scanResult.BSSID, scanResult.frequency);
                    macsFound.add(scanResult.BSSID);
                }

                invalidate();


                h.postDelayed(this, 50);
            }
        };

        r.run();

    }

    @Override
    public void onDraw(Canvas canvas) {


        //Assumption here that everything is done in landscape
        int w = getWidth();
        int h = getHeight();
        int diffX = w / 90;
        int diffY = h / 90;


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
            canvas.drawText(ap.getDescription(), ap.getX() * diffX, h - (ap.getY() * diffY), paint);
            canvas.drawCircle(ap.getX() * diffX, h - (ap.getY() * diffY), 8, red);
        }

        // drawShape(canvas);


        //String results = "";
        int remaining = showX;


        for (String mac : macs) {
            if (remaining-- == 0) break;
            double distance = 0.0;

            CottonAP filter = new CottonAP();
            int macByFloor = filter.getFloorByMac(mac);


            if (macByFloor>=0) {

                int diff = Math.abs(floor - macByFloor);
                distance = strengthToDistance(macToLevel.get(mac), 1000000.0 * macToFreq.get(mac));
                double floorScaling = 1.0 / (diff + 1.0);
                double weight = 0.0 - (Math.pow(distance, 4) * floorScaling);

                total += weight;
                AccessPoint p = filter.getAPByMac(mac);
                //Log.d("distance" + p.getDescription(),""+distance);
                filterMac.put(p, weight);
                drawAP(p, distance, canvas);
            }

        }
        double xSum = 0, ySum = 0;
        for (Map.Entry<AccessPoint, Double> ap : filterMac.entrySet()) {
            AccessPoint key = ap.getKey();
            double distance = ap.getValue();

            double factor = distance/total;
            xSum += key.getX() * factor;
            ySum += key.getY() * factor;
        }

        Point2D point = new Point2D(xSum, ySum);
        drawPoint(point, canvas, false);
        canvas.drawText("Collected Shit: "+filterMac.size(), 32, 32, paint);


    }


    public void drawLineBetweenPoints(ArrayList<Point2D> points, Canvas canvas) {
        //Size of array must be 2.
        if (points.size() == 2)
            canvas.drawLine((float) points.get(0).getX(), (float) points.get(0).getY(), (float) points.get(1).getX(), (float) points.get(1).getY(), paint);
    }


    public void setAps(ArrayList<AccessPoint> aps) {
        this.aps = aps;
    }

    public int distanceBetweenPoints(Point p1, Point p2) {
        int dist = (int) Math.sqrt(Math.pow(p1.x - p2.x, 2) + Math.pow(p1.y - p2.y, 2));
        return dist;
    }

    public void drawPoint(Point2D p, Canvas c, boolean draw) {
        Paint blue = new Paint();
        blue.setColor(Color.BLUE);
        blue.setTextSize(30);
        int w = getWidth();
        int h = getHeight();
        int diffX = w / 90;
        int diffY = h / 90;
        double x = p.getX();
        double y = p.getY();

        c.drawText("X " + (int) x + " Y: " + (int) y, 60, (132), blue);

        c.drawCircle((float) x*diffX, h- (float) (y*diffY), 15, blue);

    }

    public void drawPoints(ArrayList<Point2D> intersections, Canvas c) {
        Paint blue = new Paint();
        blue.setColor(Color.GREEN);
        blue.setTextSize(30);
        int w = getWidth();
        int h = getHeight();
        int diffX = w / 90;
        int diffY = h / 90;
        for (Point2D p : intersections) {
            c.drawCircle((float) p.getX(), (float) p.getY(), 15, blue);
        }
    }

    /**
     * Calculates the centroid based off an array of points
     *
     * @return
     */
    public Point2D centroid(ArrayList<Point2D> points) {
        int size = points.size();
        int totalX = 0;
        int totalY = 0;
        for (Point2D p : points) {
            totalX += p.getX();
            totalY += p.getY();
        }
        if (size != 0) return new Point2D(totalX / size, totalY / size);
        return null;
    }


    private void drawAP(AccessPoint ap, double distance, Canvas canvas) {
        int w = getWidth();
        int h = getHeight();
        int diffX = w / 90;
        int diffY = h / 90;
        Point point = ap.getPoint();
        Paint green = new Paint();
        green.setColor(Color.GREEN);
        green.setTextSize(30);
        int x = point.x;
        int y = point.y;
        canvas.drawCircle(ap.getX() * diffX, h - (ap.getY() * diffY), 8, green);
        //canvas.drawText(ap.getX() + " " + ap.getY(), ap.getX() * diffX, h - (ap.getY() * diffY), green);
        Paint blue = new Paint();
        blue.setColor(Color.BLUE);
        blue.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(x * diffX, h - y * diffY, (float) distance * (diffX), blue);
        canvas.drawText("d:" + distance + " " + ap.getDescription(), 0, 0, green);


    }

    public ArrayList<Point> normalize(ArrayList<Point2D> point2D) {
        ArrayList<Point> toReturn = new ArrayList<Point>();
        for (Point2D p : point2D) {
            toReturn.add(new Point((int) p.getX(), (int) p.getY()));
        }
        return toReturn;
    }


    private double strengthToDistance(double level, double freq) {
        final double a = -0.07363796;
        final double b = -2.52218124;


        final double C = 299792458.0;
        final double ROUTER_HEIGHT = 2.5;

        double n = Math.max(2, a * level + b);
        level = -level;
        double wavelength = C / freq;
        double FSPL = 20.0 * Math.log10(4.0 * Math.PI / wavelength);
        double directDistanceM = Math.pow(10, (level - FSPL) / (10.0 * n));

        directDistanceM = Math.max(directDistanceM, ROUTER_HEIGHT);


        return directDistanceM;

    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public ArrayList<Point2D> convertAPList(ArrayList<AccessPoint> aps) {
        int w = getWidth();
        int h = getHeight();
        int diffX = w / 90;
        int diffY = h / 90;
        ArrayList<Point2D> toReturn = new ArrayList<Point2D>();
        for (AccessPoint ap : aps) {
            toReturn.add(ap.getPoint2D(diffX, diffY, h));
        }
        return toReturn;
    }

}