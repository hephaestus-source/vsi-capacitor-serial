package com.hephaestus.serial;

import static org.mockito.Mockito.when;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbManager;
import com.getcapacitor.JSObject;
import com.getcapacitor.PluginCall;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class UsbBroadcastReceiverTest {

    @InjectMocks
    private UsbBroadcastReceiver underTest;

    @Mock
    PluginCall pluginCall;

    @Mock
    Activity activity;

    @Mock
    JSObject response;

    @Mock
    Context context;

    @Mock
    private Intent intent;

    @Test
    public void shouldHandleInvalidPermissionAction() {
        when(intent.getAction()).thenReturn("SOMETHING");

        underTest.onReceive(context, intent);

        Mockito.verifyNoInteractions(pluginCall);
    }

    @Test
    public void shouldHandleNotGrantedPermission() {
        when(intent.getAction()).thenReturn(UsbBroadcastReceiver.USB_PERMISSION);
        when(intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)).thenReturn(false);

        underTest.onReceive(context, intent);

        Mockito.verify(pluginCall).reject("Permission to connect to the device was denied!");
    }

    @Test
    public void shouldHandleGrantedPermissions() {
        when(intent.getAction()).thenReturn(UsbBroadcastReceiver.USB_PERMISSION);
        when(intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)).thenReturn(true);

        underTest.onReceive(context, intent);

        Mockito.verify(pluginCall).resolve(response);
    }
}
