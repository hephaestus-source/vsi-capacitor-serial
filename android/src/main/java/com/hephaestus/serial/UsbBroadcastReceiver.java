package com.hephaestus.serial;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbManager;
import com.getcapacitor.JSObject;
import com.getcapacitor.PluginCall;

public class UsbBroadcastReceiver extends BroadcastReceiver {

    public static final String USB_PERMISSION = "com.hephaestus.serial.USB_PERMISSION";
    private final PluginCall call;
    private final Activity activity;
    private final JSObject response;

    public UsbBroadcastReceiver(PluginCall call, Activity activity, JSObject response) {
        this.call = call;
        this.activity = activity;
        this.response = response;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (USB_PERMISSION.equals(action)) {
            if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                call.resolve(response);
            } else {
                call.reject("Permission to connect to the device was denied!");
            }

            activity.unregisterReceiver(this);
        }
    }
}
