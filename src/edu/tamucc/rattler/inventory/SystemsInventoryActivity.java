package edu.tamucc.rattler.inventory;

import java.io.IOException;

import android.app.TabActivity;
import android.content.Intent;
import android.database.SQLException;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class SystemsInventoryActivity extends TabActivity {
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        //CREATE TABS
        TabHost tabHost = getTabHost();
        
        // Tab for Assign
        TabSpec assignspec = tabHost.newTabSpec("Assign");
        // setting Title for the Tab
        assignspec.setIndicator("Assign");
        Intent assignIntent = new Intent(this, AssignActivity.class);
        assignspec.setContent(assignIntent);
 
        // Tab for Scan
        TabSpec scanspec = tabHost.newTabSpec("Scan");
        scanspec.setIndicator("Scan");
        Intent scanIntent = new Intent(this, ScanActivity.class);
        scanIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        scanspec.setContent(scanIntent);
 
        // Tab for hardware
        TabSpec hardwarespec = tabHost.newTabSpec("Hardware");
        hardwarespec.setIndicator("Hardware");
        Intent hardwareIntent = new Intent(this, HardwareActivity.class);
        hardwareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);        
        hardwarespec.setContent(hardwareIntent);
 
        //Tab for User
        TabSpec userspec = tabHost.newTabSpec("user");
        userspec.setIndicator("User");
        Intent userIntent = new Intent(this, UserActivity.class);
        userIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);        
        userspec.setContent(userIntent);
        
        // Adding all TabSpec to TabHost
        tabHost.addTab(assignspec); // Adding assigns tab
        tabHost.addTab(scanspec); // Adding scans tab
        tabHost.addTab(hardwarespec); // Adding hardwares tab       
        tabHost.addTab(userspec); // Adding hardwares tab  
        
        //CONNECT WITH DATABASE
        DatabaseHandler dbHandler = new DatabaseHandler(this);
 
        try {
 
        	 	dbHandler.createDataBase();
 
        	} 
        catch (IOException ioe) {
 
        	throw new Error("Unable to create database");
 
        	}
 
        try {
 
        		//dbHandler.openDB();
 
        	}
        	catch(SQLException sqle){
 
        		throw sqle;
 
        	}
        dbHandler.close();
    }
    
}