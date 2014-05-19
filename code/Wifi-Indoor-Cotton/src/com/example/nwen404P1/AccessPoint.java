package com.example.nwen404P1;

import android.graphics.Point;


/**
 * Created by sriram on 23/03/14.
 *
 * AccessPoint object used to store the valulable information for each AccessPoint in Cotton.
 */
public class AccessPoint {

    private String description;
    private String MAC;
    private int floor;
    private int x;
    private int y;

    public AccessPoint(String MAC, int floor, int x, int y, String description) {
        this.description = description;
        this.MAC = MAC;
        this.floor = floor;
        this.x = x;
        this.y = y;
    }

    public String getDescription() {
        return description;
    }

    public String getMAC() {
        return MAC;
    }

    public int getFloor() {
        return floor;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Point getPoint() {
        return new Point(this.x, this.y);
    }
}
