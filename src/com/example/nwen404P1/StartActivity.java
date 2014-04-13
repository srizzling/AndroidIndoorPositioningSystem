package com.example.nwen404P1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

public class StartActivity extends Activity implements AdapterView.OnItemSelectedListener, View.OnClickListener {
    private CottonAP aps = new CottonAP();
    private DrawView drawView;
    private String selectedLevel;


    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //drawView = new DrawView(this);
        //drawView.setBackgroundColor(Color.WHITE);
        //setUpAllAps();
        //drawView.setAps(aps);
        //setContentView(drawView);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        List<String> availableFloors = new ArrayList<String>();
        availableFloors.add("1");
        availableFloors.add("2");
        availableFloors.add("3");
        availableFloors.add("4");
        availableFloors.add("5");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, availableFloors);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner levels = (Spinner) findViewById(R.id.spinner);
        levels.setAdapter(adapter);

        final Button button = (Button) findViewById(R.id.start);
        button.setOnClickListener(this);
        levels.setOnItemSelectedListener(this);

        final Button button2 = (Button) findViewById(R.id.listen);
        button2.setOnClickListener(this);

    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        selectedLevel = (String) parent.getItemAtPosition(pos);

    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    @Override
    public void onClick(View v) {
        Intent myIntent;
        myIntent = new Intent(this, Display.class);
        myIntent.putExtra("level", selectedLevel);
        startActivity(myIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
}

