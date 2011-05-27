package com.teamwin.inputsettings;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;

public class InputSettings extends Activity {
    private String sysfsTapToClickLocation = "/sys/devices/platform/tegra-i2c.1/i2c-2/2-0019/tap_toggle";
    private static final String LOG_TAG = "TTC-InputSettings: ";

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
            boolean ttcEnabled = canTapToClick();
            cbTapToClick.setChecked(ttcEnabled);
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
        String cmd = String.format("echo %s > " + sysfsTapToClickLocation + "\n", enabled ? "1" : "0");

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

    private boolean canTapToClick() {

        try {
            File file = new File(sysfsTapToClickLocation);
            BufferedReader data = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String line = data.readLine();
            line = line.trim();

            return (line.charAt(0) == '1');

        } catch (Exception e) {
            Log.e(LOG_TAG, "Exception while trying to open " + sysfsTapToClickLocation + " : " + e.getMessage());
            return false;
        }

    }

    // Kanged from joeykrim - http://www.joeykrim.com
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
