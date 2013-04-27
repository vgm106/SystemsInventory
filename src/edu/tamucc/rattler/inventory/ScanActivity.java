package edu.tamucc.rattler.inventory;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

public class ScanActivity extends Activity{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scan);
        IntentIntegrator.initiateScan(this);
    }
    public void onScan(View v){
    	IntentIntegrator.initiateScan(this);
    }
    
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	switch(requestCode) {
    	case IntentIntegrator.REQUEST_CODE: {
    		if (resultCode != RESULT_CANCELED) {
    			IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
    			if (scanResult != null) {
    				String upc = scanResult.getContents();
    				//Store the scanned value so that it can be accessed in other Activities
    				SharedPreferences settings = getSharedPreferences("hID", MODE_WORLD_READABLE);
    				SharedPreferences.Editor editor = settings.edit();
    				editor.putString("hardwareID", upc);
    			    editor.putString("isScanned", "Yes"); //to tell assign to update last scanned date
    				// Commit the edits!
    				editor.commit();

    				//Go to assignactivity
    				Intent assg = new Intent(this, SystemsInventoryActivity.class);
    				this.startActivity(assg);

    			}
    		}
    		break;
    	}
    	}
    }
}
