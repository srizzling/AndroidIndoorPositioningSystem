package com.example.nwen404P1;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;

import java.util.ArrayList;

/**
 * Created by sriram on 12/04/14.
 */
public class Display extends Activity {

    private DrawView drawView;
    private CottonAP aps = new CottonAP();


    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        drawView = new DrawView(this);
        drawView.setBackgroundColor(Color.WHITE);
        String value = getIntent().getExtras().getString("level");
        aps.getAPByFloor(Integer.parseInt(value));
        drawView.setAps(aps.getAPByFloor(Integer.parseInt(value)));
        setContentView(drawView);
    }


}
