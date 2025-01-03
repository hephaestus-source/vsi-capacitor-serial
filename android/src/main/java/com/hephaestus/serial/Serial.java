package com.hephaestus.serial;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import androidx.appcompat.app.AppCompatActivity;
import com.getcapacitor.PluginCall;
import com.hephaestus.serial.request.SerialOptions;
import com.hephaestus.serial.request.SerialPortFilter;
import com.hephaestus.serial.response.SerialPort;
import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.hoho.android.usbserial.util.SerialInputOutputManager;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class Serial {

    private final Supplier<AppCompatActivity> activitySupplier;
    private final Consumer<byte[]> serialConsumer;
    private UsbSerialDriver driver;
    private UsbManager manager;
    private UsbSerialPort port;
    private SerialInputOutputManager mSerialIoManager;

    private final ExecutorService mExecutor = Executors.newSingleThreadExecutor();
    private final SerialInputOutputManager.Listener mListener = new SerialInputOutputManager.Listener() {
        @Override
        public void onRunError(Exception e) {}

        @Override
        public void onNewData(final byte[] data) {
            Serial.this.updateReceivedData(data);
        }
    };
    private int selectedDeviceId;

    Serial(Supplier<AppCompatActivity> activitySupplier, Consumer<byte[]> serialConsumer) {
        this.activitySupplier = activitySupplier;
        this.serialConsumer = serialConsumer;
    }

    public List<SerialPort> getDevices() {
        return getDevicesList().stream().map(this::getDeviceResponse).collect(Collectors.toList());
    }

    public void selectDevice(Integer deviceId) {
        if (deviceId == null) {
            return;
        }

        getDevicesList()
                .stream()
                .filter(item -> item.getDevice().getDeviceId() == deviceId)
                .findFirst()
                .ifPresent(item -> {
                    selectedDeviceId = deviceId;
                });
    }

    public void requestPort(PluginCall call, List<SerialPortFilter> request) {
        List<UsbSerialDriver> availableDrivers = getDevicesList();

        if (availableDrivers.isEmpty()) {
            call.reject("No devices found");
            return;
        }

        driver = availableDrivers
                .stream()
                .filter(item -> item.getDevice().getDeviceId() == selectedDeviceId)
                .findFirst()
                .orElse(availableDrivers.get(0));

        UsbDevice device = driver.getDevice();

        var activity = activitySupplier.get();
        var intent = new Intent(UsbBroadcastReceiver.USB_PERMISSION);
        intent.setPackage(activity.getPackageName());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                activity,
                0,
                intent,
                PendingIntent.FLAG_MUTABLE
        );

        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbBroadcastReceiver.USB_PERMISSION);

        UsbBroadcastReceiver usbReceiver = new UsbBroadcastReceiver(call, activity, getDeviceResponse(driver).toJSON());
        activity.registerReceiver(usbReceiver, filter, Context.RECEIVER_NOT_EXPORTED);

        try {
            manager.requestPermission(device, pendingIntent);
        } catch (Exception e) {
            call.reject(e.getMessage());
        }
    }

    public void open(PluginCall call, SerialOptions options) {
        if (driver == null) {
            call.reject("The driver is not selected");
            return;
        }

        UsbDeviceConnection connection = manager.openDevice(driver.getDevice());

        if (connection == null) {
            call.reject("Cannot open the device");
            return;
        }

        port = driver.getPorts().get(0);
        try {
            port.open(connection);
            port.setParameters(options.getBaudRate(), options.getDataBits(), options.getStopBits(), options.getParity());
            call.resolve();
        } catch (Exception e) {
            call.reject(e.getMessage());
        }

        onDeviceStateChange();
    }

    public void close(PluginCall call) {
        try {
            if (port != null) {
                port.close();
            }

            port = null;
            call.resolve();
        } catch (IOException e) {
            call.reject(e.getMessage());
        }

        onDeviceStateChange();
    }

    public void write(PluginCall call, byte[] buffer) {
        if (port == null) {
            call.reject("The port is closed");

            return;
        }

        try {
            port.write(buffer, 1000);
            call.resolve();
        } catch (Exception e) {
            call.reject(e.getMessage());
        }
    }

    public void destroy() {
        onDeviceStateChange();

        if (port != null) {
            try {
                port.close();
            } catch (IOException ignored) {}
        }
    }

    private List<UsbSerialDriver> getDevicesList() {
        manager = (UsbManager) activitySupplier.get().getSystemService(Context.USB_SERVICE);
        UsbSerialProber prober = UsbSerialProber.getDefaultProber();

        return prober.findAllDrivers(manager);
    }

    private SerialPort getDeviceResponse(UsbSerialDriver driver) {
        var port = new SerialPort();
        var device = driver.getDevice();

        port.setPortId(driver.getPorts().get(0).getPortNumber());
        port.setPortName(driver.getPorts().get(0).getClass().getSimpleName());
        port.setDeviceInstanceId(device.getDeviceId());
        port.setDeviceName(device.getDeviceName());
        port.setProductId(device.getProductId());
        port.setUsbDriverName(driver.getClass().getSimpleName());
        port.setVendorId(device.getVendorId());

        return port;
    }

    private void onDeviceStateChange() {
        stopIoManager();
        startIoManager();
    }

    private void startIoManager() {
        if (driver == null) {
            return;
        }

        mSerialIoManager = new SerialInputOutputManager(port, mListener);
        mExecutor.submit(mSerialIoManager);
    }

    private void stopIoManager() {
        if (mSerialIoManager == null) {
            return;
        }

        mSerialIoManager.stop();
        mSerialIoManager = null;
    }

    private void updateReceivedData(byte[] data) {
        serialConsumer.accept(data);
    }
}
