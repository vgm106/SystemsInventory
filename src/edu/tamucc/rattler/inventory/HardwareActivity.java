package edu.tamucc.rattler.inventory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class HardwareActivity extends Activity{
	
	//private Spinner hardwareList;
	private DatabaseHandler dbHandler;
	private List<Map<String,String>> data =  new ArrayList<Map<String, String>>();
	private ListView hinfo;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hardware);
        
        //hardwareList = (Spinner) findViewById(R.id.hardwareIdList);
        hinfo = (ListView) findViewById(R.id.hardwareView);
        
        dbConnect();
    }
    
    @Override
    protected void onResume(){
    	super.onResume();
//    	AlertDialog alertDialog = new AlertDialog.Builder(this).create();
//    	alertDialog.setTitle("Reset...");
//    	alertDialog.show();
    }
    
    public void dbConnect(){
    	//CONNECT WITH DATABASE
    	dbHandler = new DatabaseHandler(this);

    	try {

    		dbHandler.createDataBase();

    	} 
    	catch (IOException ioe) {

    		throw new Error("Unable to create database");

    	}

    	try {

    		showHardware();

    	}
    	catch(SQLException sqle){

    		throw sqle;

    	}

    	dbHandler.close();
    }
    
    public void showHardware(){
        Map<String, String> datum = new HashMap<String, String>(); //values in each list item, key is the label, 
        														   //value is value from db
        
        // Restore preferences
        
        SharedPreferences settings = getSharedPreferences("hID", MODE_PRIVATE);
        String hardwareID = settings.getString("hardwareID", "notfound");
        
        
        String query ="SELECT * from Hardware where _id = '"+ hardwareID +"'";//+ hardwareList.getSelectedItem().toString() + "'" ;
        Cursor readCursor = dbHandler.getReadableDatabase().rawQuery(query, null); //get from value from db
        readCursor.moveToNext();
        
        datum.put("label", "TAMU ID:" );
        datum.put("desc", readCursor.getString(0));//hardwareList.getSelectedItem().toString());
        data.add(datum);				//add to 'data' which is used in the adapter
        
        datum = new HashMap<String, String>();
        datum.put("label", "Equipment Type");
        datum.put("desc", readCursor.getString(1));
        data.add(datum);
        
        datum = new HashMap<String, String>();
        datum.put("label", "Make_Model");
        datum.put("desc", readCursor.getString(2));
        data.add(datum);
        
        datum = new HashMap<String, String>();
        datum.put("label", "Serial");
        datum.put("desc", readCursor.getString(3));
        data.add(datum);
        
        datum = new HashMap<String, String>();
        datum.put("label", "Warranty");
        datum.put("desc", readCursor.getString(4));
        data.add(datum);
        
        datum = new HashMap<String, String>();
        datum.put("label", "PurchaseOrderNumber");
        datum.put("desc", readCursor.getString(5));
        data.add(datum);
        
        datum = new HashMap<String, String>();
        datum.put("label", "PurchaseDate");
        datum.put("desc", readCursor.getString(6));
        data.add(datum);
        
        datum = new HashMap<String, String>();
        datum.put("label", "GrantID");
        datum.put("desc", readCursor.getString(7));
        data.add(datum);
        
        datum = new HashMap<String, String>();
        datum.put("label", "Notes");
        datum.put("desc", readCursor.getString(8));
        data.add(datum);
        
        SimpleAdapter adapter = new SimpleAdapter(this, data,
                android.R.layout.simple_list_item_2,
                new String[] {"label", "desc"},
                new int[] {android.R.id.text1,
                           android.R.id.text2}); //adapter for listview
        hinfo.setAdapter(adapter);
    }

}
