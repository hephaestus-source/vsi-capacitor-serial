package com.hephaestus.serial;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.util.Base64;
import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.annotation.Permission;
import com.hephaestus.serial.request.SerialOptions;
import com.hephaestus.serial.request.SerialPortFilter;
import com.hephaestus.serial.response.SerialPort;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONException;

@CapacitorPlugin(name = "Serial", permissions = {
    @Permission(alias = "usb", strings = "android.permission.USB_PERMISSION")
})
public class SerialPlugin extends Plugin {

    private static final int DEFAULT_BAUD_RATE = 115200;

    private final Serial implementation = new Serial(this::getActivity, (byte[] data) -> {
        String encodedData = Base64.encodeToString(data, Base64.NO_WRAP);

        JSObject result = new JSObject();
        result.put("data", encodedData);

        notifyListeners("read", result);
    });
    private BroadcastReceiver usbReceiver;

    @Override
    public void load() {
        super.load();
        registerUsbReceiver();
    }

    @Override
    protected void handleOnDestroy() {
        unregisterUsbReceiver();
        implementation.destroy();
    }

    @PluginMethod
    public void getDevices(PluginCall call) {
        JSObject response = new JSObject();
        JSArray list = new JSArray();

        implementation.getDevices().stream().map(SerialPort::toJSON).forEach(list::put);

        response.put("list", list);

        call.resolve(response);
    }

    @PluginMethod
    public void selectDevice(PluginCall call) {
        var deviceId = call.getInt("deviceId");
        implementation.selectDevice(deviceId);
        call.resolve();
    }

    @PluginMethod
    public void requestPort(PluginCall call) {
        var filters = call.getArray("filters");
        var request = getSerialPortFilter(filters);

        implementation.requestPort(call, request);
    }

    @PluginMethod
    public void open(PluginCall call) {
        implementation.open(call, getSerialOptions(call));
    }

    @PluginMethod
    public void close(PluginCall call) {
        implementation.close(call);
    }

    @PluginMethod
    public void write(PluginCall call) {
        String base64Data = call.getString("data");

        if (base64Data == null) {
            call.reject("Data is required");
            return;
        }

        byte[] data = Base64.decode(base64Data, Base64.NO_WRAP);

        implementation.write(call, data);
    }

    private SerialOptions getSerialOptions(PluginCall call) {
        var options = new SerialOptions();
        options.setBaudRate(call.getInt("baudRate", DEFAULT_BAUD_RATE));
        options.setDataBits(call.getInt("dataBits", UsbSerialPort.DATABITS_8));
        options.setStopBits(call.getInt("stopBits", UsbSerialPort.STOPBITS_1));
        options.setParity(call.getInt("parity", UsbSerialPort.PARITY_NONE));

        return options;
    }

    private List<SerialPortFilter> getSerialPortFilter(JSArray request) {
        if (request == null) {
            return List.of();
        }

        List<SerialPortFilter> result = new ArrayList<>();

        try {
            for (int i = 0; i < request.length(); i++) {
                var filter = new SerialPortFilter();
                var json = (JSObject) request.get(i);

                filter.setUsbVendorId(json.getInteger("usbVendorId"));
                filter.setUsbProductId(json.getInteger("usbProductId"));
                filter.setBluetoothServiceClassId(json.getInteger("bluetoothServiceClassId"));

                result.add(filter);
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    private void registerUsbReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);

        usbReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
                    UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    onDeviceConnected(device);
                } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                    UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    onDeviceDisconnected(device);
                }
            }
        };

        getActivity().registerReceiver(usbReceiver, filter);
    }

    private void unregisterUsbReceiver() {
        if (usbReceiver == null) {
            return;
        }

        getActivity().unregisterReceiver(usbReceiver);
    }

    private void onDeviceConnected(UsbDevice device) {
        JSObject response = new JSObject();
        response.put("deviceName", device.getDeviceName());
        response.put("action", "CONNECTED");

        notifyListeners("connected", response);
    }

    private void onDeviceDisconnected(UsbDevice device) {
        JSObject response = new JSObject();
        response.put("deviceName", device.getDeviceName());
        response.put("action", "DISCONNECTED");

        notifyListeners("disconnected", response);
    }
}
