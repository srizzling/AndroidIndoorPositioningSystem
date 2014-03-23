package com.example.nwen404P1;

/**
 * Created by sriram on 23/03/14.
 */
public class AccessPoint {

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMAC() {
        return MAC;
    }

    public void setMAC(String MAC) {
        this.MAC = MAC;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    private String description;
    private String MAC;
    private int floor;

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    private int x;
    private int y;


    public AccessPoint(String MAC, int floor, int x, int y, String description){
        this.description = description;
        this.MAC = MAC;
        this.floor = floor;
        this.x = x;
        this.y = y;
    }
}
