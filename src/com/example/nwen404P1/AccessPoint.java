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

    public AccessPoint(String description, String MAC, int floor){
        this.description = description;
        this.MAC = MAC;
        this.floor = floor;
    }
}
