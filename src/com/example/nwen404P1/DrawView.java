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

import java.text.DecimalFormat;
import java.util.*;

public class DrawView extends View {
    Paint paint = new Paint();
    ArrayList<AccessPoint> aps = new ArrayList<AccessPoint>();
    ArrayList<Point> pList;
    int w;
    int h;
    int diffX;
    int diffY;
    private static final int showX = 300;
    Map<String, Double> macToLevel;
    Map<String, Integer> macToFreq;
    TextView text;
    List<String> macs;

    public DrawView(Context context) {
        super(context);
        paint.setColor(Color.BLACK);
        final Handler h = new Handler();
        final WifiManager mainWifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        macToFreq = new HashMap<String, Integer>();
        macToLevel = new HashMap<String, Double>();

        final double factor = 0.96;


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
            canvas.drawText(ap.getDescription(), ap.getX() * diffX, h-(ap.getY() * diffY), paint);
            canvas.drawCircle(ap.getX() * diffX, h-(ap.getY() * diffY), 8, red);
        }

        // drawShape(canvas);


        //String results = "";
        int remaining = showX;
        ArrayList<AccessPoint> filterMac = new ArrayList<AccessPoint>();
        for (String mac : macs) {
            if (remaining-- == 0) break;

            DecimalFormat f = new DecimalFormat("###.00");

            double distance = 0.0;
            //String[] name = WifiNames.getMacToName().get(mac);
            CottonAP filter = new CottonAP();

            if (filter.filter(mac)) {
                distance = strengthToDistance(macToLevel.get(mac), 1000000.0 * macToFreq.get(mac));



                AccessPoint p = filter.getAPByMac(mac);
                filterMac.add(p);
                //Log.d("accesspont", p.getPoint().toString());
                drawAP(p, distance, canvas);
            }
        }

        if(filterMac.size()==2){
            Point2D p1 = filterMac.get(0).getPoint2D();
            Point2D p2 = filterMac.get(1).getPoint2D();
            Circle2D c1 = new Circle2D(p1,strengthToDistance(
                    macToLevel.get(filterMac.get(0).getMAC()),
                    1000000.0 * macToFreq.get(filterMac.get(0).getMAC()
             )));

            Circle2D c2 = new Circle2D(p2,strengthToDistance(
                    macToLevel.get(filterMac.get(1).getMAC()),
                    1000000.0 * macToFreq.get(filterMac.get(1).getMAC()
            )));
            ArrayList<Point2D> intersection = new ArrayList<Point2D>(c1.intersections(c2));
            normalize(intersection);

        }



    }



    public void setAps(ArrayList<AccessPoint> aps) {
        this.aps = aps;
    }

    public int distanceBetweenPoints(Point p1, Point p2) {
        int dist = (int) Math.sqrt(Math.pow(p1.x - p2.x, 2) + Math.pow(p1.y - p2.y, 2));
        return dist;
    }


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
        return new Point(totalX / size, totalY / size);
    }


    private void drawAP(AccessPoint ap, double distance, Canvas canvas){
        int w = getWidth();
        int h = getHeight();
        int diffX = w / 90;
        int diffY = h / 90;
        Point point = ap.getPoint();
        Paint green = new Paint();
        green.setColor(Color.GREEN);
        int x = point.x;
        int y = point.y;
        canvas.drawCircle(ap.getX() * diffX, h - (ap.getY() * diffY), 8, green);
        Paint blue = new Paint();
        blue.setColor(Color.BLUE);
        blue.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(x * diffX, h - y * diffY, ap.getY() * (diffX), blue);

    }

    public ArrayList<Point> normalize(ArrayList<Point2D> point2D){
        ArrayList<Point> toReturn = new ArrayList<Point>();
        for(Point2D p: point2D){
            toReturn.add(new Point((int) p.getX(),(int) p.getY()));
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

        double distancePlaneM = Math.sqrt(Math.pow(directDistanceM, 2) - Math.pow(ROUTER_HEIGHT, 2));
        return distancePlaneM;

    }


}