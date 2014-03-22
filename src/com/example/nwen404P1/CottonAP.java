package com.example.nwen404P1;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by sriram on 23/03/14.
 */
public class CottonAP {
    private ArrayList<AccessPoint> aps;
    private File apLocationFile;

    public CottonAP(File file){
        aps = new ArrayList<AccessPoint>();
        apLocationFile = file;
        setAccessPoints(apLocationFile);
    }

    public void setAccessPoints(File fileToLoad){

    }
}
