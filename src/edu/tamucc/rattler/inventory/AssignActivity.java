package edu.tamucc.rattler.inventory;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class AssignActivity extends Activity{ 
    
    private DatabaseHandler dbHandler;
    private Spinner hardwareList;
    private Spinner userList;
    private TextView lastScan;
    private String hID;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.assign);
        
        hardwareList = (Spinner) findViewById(R.id.hardwareIdList);
        userList = (Spinner) findViewById(R.id.userIdList);
        lastScan =(TextView) findViewById(R.id.lastScan);
        
		hardwareList.setOnItemSelectedListener(new  OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> parent,
		            View view, int pos, long id){
				// TODO Auto-generated method stub
				//Store the selected value so that it can be accessed in other Activities
				SharedPreferences settings = getSharedPreferences("hID", MODE_PRIVATE);
				SharedPreferences.Editor editor = settings.edit();
				editor.putString("hardwareID", parent.getSelectedItem().toString());

				// Commit the edits!
				editor.commit();				
				
				//Show the correct user in spinner automatically
				hID = parent.getSelectedItem().toString(); //get selected hardware ID
	        	ArrayAdapter<String> ul = (ArrayAdapter<String>) userList.getAdapter(); 	        	
	        	//Now select from spinner
	        	userList.setSelection(ul.getPosition(getUserName(hID)), true);
		        
		        //Update Last Scanned
		        onClickVerify(lastScan);
			    
			    
			}
	        
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
	    });
        
      //CONNECT WITH DATABASE
        dbHandler = new DatabaseHandler(this);
 
        try {
 
        	 	dbHandler.createDataBase();
 
        	} 
        catch (IOException ioe) {
 
        	throw new Error("Unable to create database");
 
        	}
 
        try {
 
        		//dbHandler.openDB();
        		listHardware();
        	    listUsers();
        	    
        	}
        	catch(SQLException sqle){
 
        		throw sqle;
 
        	}
        
        dbHandler.close();
        
        hID = hardwareList.getSelectedItem().toString();
        
        
        // Restore preferences from scan activity.      
        SharedPreferences settings = getSharedPreferences("hID", MODE_PRIVATE);
        String hardwareID = settings.getString("hardwareID", "notfound");
        String isScanned = settings.getString("isScanned", "No");
        if(isScanned.contains("Yes")){
        	Calendar c = Calendar.getInstance();
        	SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy h:mm:ss");;
        	String lastScanned = df.format(c.getTime());
        	setLastScanned(hardwareID, lastScanned);
        }

		//Store the selected value so that it can be accessed in other Activities
	    SharedPreferences.Editor editor = settings.edit();
	    editor.putString("hardwareID", hID);
	    editor.putString("isScanned", "No");

	    // Commit the edits!
	    editor.commit();
	    
	    ArrayAdapter<String> spinnerAdapter = (ArrayAdapter<String>) hardwareList.getAdapter();
	    if(spinnerAdapter.getPosition(hardwareID) == -1){ //if no hid found{
	    	Toast.makeText(this, "No such ID found in Inventory", Toast.LENGTH_SHORT).show();
	    }
	    hardwareList.setSelection(spinnerAdapter.getPosition(hardwareID));
    }
    
    public void listHardware(){
		Cursor readCursor = dbHandler.getReadableDatabase().rawQuery("SELECT _id from Hardware", null);
		ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, android.R.id.text1);
		spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		hardwareList.setAdapter(spinnerAdapter);
		hardwareList.setOverScrollMode(android.view.View.OVER_SCROLL_ALWAYS);
		while(readCursor.moveToNext()){
			spinnerAdapter.add(readCursor.getString(0));
		}
		spinnerAdapter.notifyDataSetChanged();
		hardwareList.setAdapter(spinnerAdapter);
    }
    
    public void listUsers(){
		Cursor readCursor = dbHandler.getReadableDatabase().rawQuery("SELECT LastName,FirstName FROM EndUserData ORDER BY LastName", null);
		ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, android.R.id.text1);
		spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		userList.setAdapter(spinnerAdapter);
		userList.setOverScrollMode(android.view.View.OVER_SCROLL_ALWAYS);
		while(readCursor.moveToNext()){
			spinnerAdapter.add(readCursor.getString(0)+","+readCursor.getString(1));
		}
		spinnerAdapter.notifyDataSetChanged();
		userList.setAdapter(spinnerAdapter);
    }
    
    public void onClickAssign(View v){
    	hID = hardwareList.getSelectedItem().toString(); //get selected hardware ID
    	String[] name = userList.getSelectedItem().toString().split(","); //get selected user name
    	String query = "SELECT _id FROM EndUserData WHERE FirstName = '" + name[1] + "' AND LastName ='" + name[0] + "'";
        Cursor readCursor = dbHandler.getReadableDatabase().rawQuery(query, null); //get from value from db
    	if(readCursor.getCount() != 0){
    		readCursor.moveToNext();
    		String uID = readCursor.getString(0); //got the user ID
    		
    		query = "SELECT _id FROM EndUser WHERE UserID = '" + uID + "'";
    		readCursor = dbHandler.getReadableDatabase().rawQuery(query, null); //get from value from db
        	if(readCursor.getCount() != 0){
        		readCursor.moveToNext();
        		String euID = readCursor.getString(0); //got the EndUser ID
        		
        		query = "SELECT * FROM HardwareInventory WHERE _id = " + hID; 
        		readCursor  =  dbHandler.getReadableDatabase().rawQuery(query,null);
        		if(readCursor.getCount() == 0){ //check if the hardware is already in the hardwareinventory 
        			query = "INSERT INTO HardwareInventory (_id,EndUserID)  values ('" + hID + "','" + euID + "')"; //set new user for the hardware
        			readCursor  =  dbHandler.getWritableDatabase().rawQuery(query,null);
        			readCursor.moveToNext();
        			readCursor.close();
        		}
        		else{
        			readCursor.moveToNext();
        			if(getUserName(readCursor.getString(1)) != userList.getSelectedItem().toString()){//to check if its a re-assign
        				query = "UPDATE HardwareInventory SET EndUserID = '" + euID + "' WHERE _id = '" + hID + "'";
            			readCursor  =  dbHandler.getWritableDatabase().rawQuery(query,null);
            			readCursor.moveToNext();
            			readCursor.close();
        			}
        			
        		}
        	    
        		Toast.makeText(this, hID + " " + euID, Toast.LENGTH_SHORT).show();
        	}
    		
    	}
    	
    }
    
    public void onClickVerify(View v){
    	hID = hardwareList.getSelectedItem().toString(); //get selected hardware ID
    	String query = "SELECT LastScanned FROM Hardware WHERE _id = '" + hID + "'";
    	Cursor readCursor = dbHandler.getReadableDatabase().rawQuery(query, null);
    	readCursor.moveToNext();
    	if(readCursor.getCount() != 0 && readCursor.getString(0) != null){
    		
    		lastScan.setText("Last Scanned: " + readCursor.getString(0));
    	}
    	else{
    		lastScan.setText("Last Scanned: Not scanned");
    	}
    	//readCursor.close();
    	// 
    }
    
    public String getUserName(String hID){
    	String name = "";	   
		String query = "SELECT EndUserID FROM HardwareInventory WHERE HardwareInventory._id = '" + hID + "'" ; //query for enduser id
        Cursor readCursor = dbHandler.getReadableDatabase().rawQuery(query, null); //get from value from db
        if(readCursor.getCount() != 0){ 
        	readCursor.moveToNext();
        	String euID = readCursor.getString(0);
        	query = "SELECT UserID FROM EndUser WHERE EndUser._id = '" + euID +"'";//query for User ID 
	        readCursor = dbHandler.getReadableDatabase().rawQuery(query, null); //get from value from db
	        if(readCursor.getCount() != 0){ 
	        	readCursor.moveToNext();
	        	String uID = readCursor.getString(0);
	        	query = "SELECT LastName,FirstName FROM EndUserData WHERE EndUserData._id = '" + uID + "'"; // query names to select automatically in spinner
	        	readCursor = dbHandler.getReadableDatabase().rawQuery(query, null); //get from value from db
		        if(readCursor.getCount() != 0){ 
		        	readCursor.moveToNext();
		        	name = readCursor.getString(0) + "," + readCursor.getString(1);
		            return name;
		        }
	        }
        	
        }
        return name;
    }
    
    public String getEndUserID(String userName){
    	String endUserID = "";
    	String query;
    	String[] names = userName.split(","); 
    	query = "SELECT _id FROM EndUserData WHERE FirstName = '" + names[1] + "' AND LastName = '" + names[0] + "'"; //query user id
    	Cursor readCursor = dbHandler.getReadableDatabase().rawQuery(query, null);
    	if(readCursor.getCount() != 0){
    		readCursor.moveToNext();
    		String uID = readCursor.getString(0); // got user id from enduserdata
    		
    		query = "SELECT _id FROM EndUser WHERE UserID = '" + uID + "'"; //query for EndUserID
    		readCursor = dbHandler.getReadableDatabase().rawQuery(query, null);
        	if(readCursor.getCount() != 0){
        		readCursor.moveToNext();
        		endUserID = readCursor.getString(0);
        		return endUserID;
        	}
    	}
    	return endUserID;
    }
    
    public void setLastScanned(String hardwareID, String lastScanned) {
    	String query = "UPDATE Hardware SET LastScanned = '" + lastScanned + "' WHERE _id = '" + hardwareID + "'";
    	Cursor readCursor = dbHandler.getWritableDatabase().rawQuery(query, null);
    	readCursor.moveToNext();
    	readCursor.close();
	}
   

}
