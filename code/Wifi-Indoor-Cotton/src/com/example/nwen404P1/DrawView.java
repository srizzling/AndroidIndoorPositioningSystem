package com.example.nwen404P1;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.view.View;
import math.geom2d.Point2D;
import java.util.*;

public class DrawView extends View {


    //Paint Objects for Drawing
    private Paint paint = new Paint();
    private Paint red = new Paint();
    private Paint green = new Paint();
    private Paint blue = new Paint();

    private Context context;

    private ArrayList<AccessPoint> aps = new ArrayList<AccessPoint>();
    private ArrayList<Point> pList;
    private Map<String, Double> macToLevel;
    private Map<String, Integer> macToFreq;
    private List<String> macs;
    private int floor = 0;
    private HashMap<AccessPoint, Double> filterMac;
    private double total = 0;
    private int w = getWidth();
    private int diffX = w / 90;
    private int h = getHeight();
    private int diffY = h / 90;


    public DrawView(Context context) {
        super(context);

        this.context = context;

        // Set up all Paint methods
        paint.setColor(Color.BLACK);
        paint.setTextSize(32);
        red.setColor(Color.RED);
        green.setColor(Color.GREEN);
        green.setTextSize(30);
        blue.setColor(Color.BLUE);
        blue.setTextSize(30);
        blue.setColor(Color.BLUE);
        blue.setStyle(Paint.Style.STROKE);

        // Set up all the required scaling factors required to draw on the screen
        w = getWidth();
        h = getHeight();
        diffX = w / 90;
        diffY = h / 90;

        filterMac = new HashMap<AccessPoint, Double>(); // Object for filtering a list of "known" macs, with it Mapping to distance
        macToFreq = new HashMap<String, Integer>(); // A Map for holding the BSSID -> Frequency
        macToLevel = new HashMap<String, Double>(); //A Map for holding the BSSID -> Signal Strength
        total = 0; // Total which factors the distance
        createNewWorkerThread();
    }

    /**
     * In this method it will create a new worker thread that will poll the accessible access points from the current
     * position and put the mac address in a link list and populate the other two HashMaps with its relative frequency amd
     * Signal Strength.
     */
    public void createNewWorkerThread() {
        final Handler handler = new Handler(); // Easy Mangement of worker thread for polling APS
        final WifiManager mainWifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        final double factor = 0.50;

        // A link List of macs found
        macs = new LinkedList<String>();
        Runnable r = new Runnable() {
            @Override
            public void run() {

                mainWifi.startScan();

                List<String> macsFound = new LinkedList<String>();

                //Poll
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
                invalidate(); //Important to redraw with updated information
                handler.postDelayed(this, 50);
            }
        };
        r.run();
    }

    /**
     * This is where the method happens and updates the location based off the distance from the
     * routers found.
     *
     * @param canvas The Canvas object to which you draw the elements to
     */
    @Override
    public void onDraw(Canvas canvas) {

        //Draws all the Ap in given floor.
        pList = new ArrayList<Point>();
        for (AccessPoint ap : aps) {
            pList.add(new Point(ap.getX(), ap.getY()));
            canvas.drawText(ap.getDescription(), ap.getX() * diffX, h - (ap.getY() * diffY), paint);
            canvas.drawCircle(ap.getX() * diffX, h - (ap.getY() * diffY), 8, red);
        }

        //Loop through all the MACS found and filter them based of known list of AccessPoints.
        for (String mac : macs) {

            //Distance of AP based off current location
            double distance = 0.0;

            CottonAP filter = new CottonAP();

            //Gets the floor of the Mac Address. This is important as returns APS not in our floor.
            int macByFloor = filter.getFloorByMac(mac);

            //If MacByFloor is larger than 0, that means we know the location of the MAC found.
            if (macByFloor >= 0) {

                //Calculate the ABS value to see if we need to apply a filter on it
                int diff = Math.abs(floor - macByFloor);

                //Calculate the distance from the current location to the AP given the Freq and Signal Strength received from the thread
                distance = strengthToDistance(macToLevel.get(mac), 1000000.0 * macToFreq.get(mac));

                //Based on the floor, scale it according to interference (i.e if its on another floor make the reading less significant)
                double floorScaling = 1.0 / (diff + 1.0);
                double weight = 0.0 - (Math.pow(distance, 4) * floorScaling);

                //Add the weighted distance (based on the floor) to the total so we can normailize it later.
                total += weight;

                //Add the AccessPoint to the Map, Mapped to the distance of the current location
                AccessPoint p = filter.getAPByMac(mac);
                filterMac.put(p, weight);

                //Draw the active access point
                drawActiveAP(p, distance, canvas);
            }
        }

        Point2D currentPos = calcCurrPos();
        drawCurrentPos(currentPos, canvas);
        canvas.drawText("Collected Shit: " + filterMac.size(), 32, 32, paint);
    }

    /**
     * Calculates the Current Position of the Person based off the weighted distances
     * and returns a point object of the person based of this
     *
     * @return Point object showing the current postion of the user
     */
    public Point2D calcCurrPos() {

        double xSum = 0, ySum = 0;
        for (Map.Entry<AccessPoint, Double> ap : filterMac.entrySet()) {
            AccessPoint key = ap.getKey();
            double distance = ap.getValue();

            double factor = distance / total;

            //Normilize the distance vectors and add them to get the current location of user
            xSum += key.getX() * factor;
            ySum += key.getY() * factor;
        }
        return new Point2D(xSum, ySum);
    }


    /**
     * A Method used for drawing the current position on the screen based given a Point Object
     *
     * @param p - A Point2D object, with x,y coordinates of location
     * @param c - A referenced canvas object for drawing the dot
     */
    public void drawCurrentPos(Point2D p, Canvas c) {
        double x = p.getX();
        double y = p.getY();
        c.drawText("X " + (int) x + " Y: " + (int) y, 60, (132), paint);
        c.drawCircle((float) x * diffX, h - (float) (y * diffY), 15, paint);
    }

    /**
     * Given an AccessPoint draw the location of the AccessPoint and distance on the map.
     * This methods highlights that its active given your current location
     *
     * @param ap       - The AccessPoint object with its x,y coordinates
     * @param distance - The radius of the Signal Strength -> Distance calculation
     * @param canvas   - A referenced canvas object for drawing the Position
     */
    private void drawActiveAP(AccessPoint ap, double distance, Canvas canvas) {
        Point point = ap.getPoint();

        // Draw Active AP
        int x = point.x;
        int y = point.y;
        canvas.drawCircle(ap.getX() * diffX, h - (ap.getY() * diffY), 8, green);

        //Draw Distance around circle
        canvas.drawCircle(x * diffX, h - y * diffY, (float) distance * (diffX), blue);
    }

    /**
     * Calculates the distance based of the RSSI and Frequency, Shamelessly copied from:
     * (@link https://github.com/albertyw/indoor-localization). Forumla assumes Free Space
     * with some sampled factors that consideration environment and signal distortion.
     *
     * @param level - RSSI based on your current location from the AP
     * @param freq  - Frequency is Hertz (Must be converted from MegaHertz)
     * @return distance - A double representation of the distance from current location to AP
     */
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

    /**
     * Method that allows outsiders to set the current floor
     *
     * @param floor - A int representing the current floor the user is has given
     */
    public void setFloor(int floor) {
        this.floor = floor;
    }

    /**
     * Sets the list of APS to represent on the Map
     *
     * @param aps An ArrayList<AccessPoints> of the given floor
     */
    public void setAps(ArrayList<AccessPoint> aps) {
        this.aps = aps;
    }
}