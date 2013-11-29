package com.abs.telecam.helpers.gui;

import android.app.Activity;
import android.view.MenuItem;
import com.abs.telecam.R;
import com.abs.telecam.TeleCam;
import com.abs.telecam.helpers.Bluetooth.BluetoothHelper;

public class BlueToothActionsMenuHelper
{
    protected final Activity activity;
    private final BluetoothHelper helper;

    public BlueToothActionsMenuHelper(Activity activity) {
        this.activity = activity;
        this.helper = TeleCam.bluetoothHelper;
    }

    public boolean getActionForItem(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.discoverable:
                helper.ensureDiscoverable();
                return true;
            case R.id.settings:
                helper.openBlueToothSettings();
                return true;
        }
        return false;
    }
}