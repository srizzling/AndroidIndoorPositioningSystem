package com.example.nwen404P1;


import java.util.ArrayList;

/**
 * Created by sriram on 23/03/14.
 */
public class CottonAP {
    private ArrayList<AccessPoint> aps;

    public void setAccessPoints(ArrayList<AccessPoint> aps){
        this.aps = aps;
    }

    public ArrayList<AccessPoint> getAPByFloor(int floor){
        ArrayList<AccessPoint> temp = new ArrayList<AccessPoint>();
        for (AccessPoint ap : aps){
            if(ap.getFloor() == floor){
                temp.add(ap);
            }
        }
        return temp;
    }

    public ArrayList<AccessPoint> getAll(){
        return aps;
    }
}
