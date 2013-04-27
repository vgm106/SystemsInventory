package edu.tamucc.rattler.inventory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class UserActivity extends Activity{
	//private Spinner hardwareList;
	private DatabaseHandler dbHandler;
	private List<Map<String,String>> data =  new ArrayList<Map<String, String>>();
	private ListView uinfo;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user);
        
        uinfo = (ListView) findViewById(R.id.userView);
        
        //CONNECT WITH DATABASE
        dbHandler = new DatabaseHandler(this);
 
        try { 
        	 	dbHandler.createDataBase();
        	} 
        catch (IOException ioe) {
        	throw new Error("Unable to create database");
        	}
        try {
        		showUser();
        	}
        	catch(SQLException sqle){
        		throw sqle;
        	}
        
        dbHandler.close();
    }
    
    protected void onResume(){
    	super.onResume();
    }
    
    public void showUser(){
    	 Map<String, String> datum = new HashMap<String, String>(); //values in each list item, key is the label, //value is value from db
    	 
    	 
     	// Restore preferences
         
         SharedPreferences settings = getSharedPreferences("hID", MODE_WORLD_READABLE);
         String hardwareID = settings.getString("hardwareID", "null");
        
         if(hardwareID != null){//get user id
        	 String query = "select EndUserID from HardwareInventory where _id ='" + hardwareID + "'";
             Cursor readCursor = dbHandler.getReadableDatabase().rawQuery(query, null); //get from value from db
             if(readCursor.getCount() != 0){       
            	 readCursor.moveToNext();
            	 String userId = readCursor.getString(0);
            	 if(userId != null){
            		 query = "select * from EndUser,EndUserData,Title,Department,Location where EndUser._id=" + userId + " and EndUser.UserID=EndUserData._id and EndUser.TitleID=Title._id and EndUser.DepartmentID=Department._id and EndUser.LocationID=Location._id";
            		 readCursor = dbHandler.getReadableDatabase().rawQuery(query, null); //get from values from db
            		 if(readCursor.getCount() != 0){
            			 readCursor.moveToNext();

            			 String title = readCursor.getString(readCursor.getColumnIndex("TitleName"));
            			 datum.put("label", "Title: " );
            			 datum.put("desc", title);
            			 data.add(datum);				//add to 'data' which is used in the adapter

            			 datum = new HashMap<String, String>();
            			 String first = readCursor.getString(readCursor.getColumnIndex("FirstName"));
            			 datum.put("label", "First Name: " );
            			 datum.put("desc", first);
            			 data.add(datum);

            			 datum = new HashMap<String, String>();
            			 String last = readCursor.getString(readCursor.getColumnIndex("LastName"));
            			 datum.put("label", "Last Name: " );
            			 datum.put("desc", last);
            			 data.add(datum);            		 

            			 datum = new HashMap<String, String>();
            			 String phone = readCursor.getString(readCursor.getColumnIndex("Phone"));
            			 datum.put("label", "Phone: " );
            			 datum.put("desc", phone);
            			 data.add(datum);

            			 datum = new HashMap<String, String>();
            			 String email = readCursor.getString(readCursor.getColumnIndex("Email"));
            			 datum.put("label", "Email: " );
            			 datum.put("desc", email);
            			 data.add(datum);

            			 datum = new HashMap<String, String>();
            			 String dept = readCursor.getString(readCursor.getColumnIndex("Departmentname"));
            			 datum.put("label", "Department: " );
            			 datum.put("desc", dept);
            			 data.add(datum);

            			 datum = new HashMap<String, String>();
            			 String location = readCursor.getString(readCursor.getColumnIndex("LocationName"));
            			 datum.put("label", "Location: " );
            			 datum.put("desc", location);
            			 data.add(datum);

            			 SimpleAdapter adapter = new SimpleAdapter(this, data,
            					 android.R.layout.simple_list_item_2,
            					 new String[] {"label", "desc"},
            					 new int[] {android.R.id.text1,
            					 android.R.id.text2}); //adapter for listview
            			 uinfo.setAdapter(adapter);
            		 }//end if
            	 }//endif
             }//end if the hardware has any users associated
             else //if no users found
             {
            	 Toast.makeText(this, "No user associated", Toast.LENGTH_SHORT).show();
             }
         }//end if
    }

}
