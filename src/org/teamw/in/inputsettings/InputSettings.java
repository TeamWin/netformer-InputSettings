package org.teamw.in.inputsettings;

import java.io.DataOutputStream;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;

public class InputSettings extends Activity {
    private static final String LOG_TAG = "InputSettings: ";

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        // Set Tap to Click checkbox listener
        CheckBox cbTapToClick = (CheckBox) findViewById(R.id.cbTapToClick);
        cbTapToClick.setOnClickListener(cbTapToClick_OnClick);
        
        boolean rooted = canSU();
        if (rooted) {
        	// Do good stuff
        }
        else {
        	// Display error
        	cbTapToClick.setEnabled(rooted);
        }
    }
    
    private OnClickListener cbTapToClick_OnClick = new OnClickListener() {
		
		public void onClick(View v) {
			// Perform action on clicks, depending on whether it's now checked
	        boolean checked = ((CheckBox) v).isChecked();
	        
	        setTapToClick(checked);
		}
	};
	
	private boolean setTapToClick(boolean enabled) {
		Process process = null;
		
		// Set the command
		String cmd = String.format("echo %s > /sys/devices/platform/tegra-i2c.1/i2c-2/2-0019/tap_toggle", enabled ? "1" : "0");
		
		int exitValue = -1;
		try {
			process = Runtime.getRuntime().exec("su");
			DataOutputStream toProcess = new DataOutputStream(process.getOutputStream());
			toProcess.writeBytes(cmd);
			toProcess.flush();
		} catch (Exception e) {
			Log.e(LOG_TAG, "Exception while trying to run: '" + cmd + "' " + e.getMessage());
			process = null;
			exitValue = -1;
		}
		return exitValue == 0;			
	}
    
    // Kanged from joeykrim :P
	private boolean canSU() {
		Process process = null;
		int exitValue = -1;
		try {
			process = Runtime.getRuntime().exec("su");
			DataOutputStream toProcess = new DataOutputStream(process.getOutputStream());
			toProcess.writeBytes("exec id\n");
			toProcess.flush();
			exitValue = process.waitFor();
		} catch (Exception e) {
			exitValue = -1;
		}
		return exitValue == 0;
	}				
}