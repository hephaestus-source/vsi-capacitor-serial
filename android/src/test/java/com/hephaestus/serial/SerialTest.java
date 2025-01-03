package com.hephaestus.serial;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import java.io.IOException;
import java.util.List;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.Silent.class)
public class SerialTest {

    private Serial underTest;

    @Mock
    private AppCompatActivity activity;

    @Mock
    private UsbManager usbManager;

    @Mock
    private UsbSerialProber usbSerialProber;

    @Mock
    private UsbSerialDriver firstDriver;

    @Mock
    private UsbSerialDriver secondDriver;

    @Mock
    private UsbDevice firstUsbDevice;

    @Mock
    private UsbDevice secondUsbDevice;

    @Mock
    private UsbSerialPort serialPort;

    @Mock
    private PluginCall pluginCall;

    @Mock
    private PendingIntent pendingIntent;

    @Mock
    private UsbDeviceConnection usbDeviceConnection;

    MockedStatic<UsbSerialProber> proberMockedStatic;
    MockedStatic<PendingIntent> pendingIntentMockedStatic;
    SerialPortFilter serialPortFilter;
    SerialOptions serialOptions;

    @Before
    public void beforeEach() {
        underTest = new Serial(() -> activity, (data) -> {

        });
        when(activity.getSystemService(Context.USB_SERVICE)).thenReturn(usbManager);
        when(usbManager.openDevice(firstUsbDevice)).thenReturn(usbDeviceConnection);
        when(usbManager.openDevice(secondUsbDevice)).thenReturn(usbDeviceConnection);

        proberMockedStatic = Mockito.mockStatic(UsbSerialProber.class);
        proberMockedStatic.when(UsbSerialProber::getDefaultProber).thenReturn(usbSerialProber);

        pendingIntentMockedStatic = Mockito.mockStatic(PendingIntent.class);
        pendingIntentMockedStatic
            .when(() -> PendingIntent.getBroadcast(eq(activity), eq(0), any(Intent.class), eq(PendingIntent.FLAG_MUTABLE)))
            .thenReturn(pendingIntent);

        when(firstDriver.getDevice()).thenReturn(firstUsbDevice);
        when(firstDriver.getPorts()).thenReturn(List.of(serialPort));

        when(secondDriver.getDevice()).thenReturn(secondUsbDevice);
        when(secondDriver.getPorts()).thenReturn(List.of(serialPort));

        when(firstUsbDevice.getDeviceId()).thenReturn(1);
        when(firstUsbDevice.getDeviceName()).thenReturn("Device name");
        when(firstUsbDevice.getProductId()).thenReturn(2);
        when(firstUsbDevice.getVendorId()).thenReturn(3);

        when(secondUsbDevice.getDeviceId()).thenReturn(4);
        when(secondUsbDevice.getDeviceName()).thenReturn("Device name 2");
        when(secondUsbDevice.getProductId()).thenReturn(5);
        when(secondUsbDevice.getVendorId()).thenReturn(6);

        when(serialPort.getPortNumber()).thenReturn(4);

        serialPortFilter = new SerialPortFilter();
        serialPortFilter.setUsbVendorId(1);
        serialPortFilter.setUsbProductId(2);

        serialOptions = new SerialOptions();
        serialOptions.setBaudRate(115200);
        serialOptions.setDataBits(1);
        serialOptions.setStopBits(2);
        serialOptions.setParity(3);
    }

    @After
    public void afterEach() {
        proberMockedStatic.close();
        pendingIntentMockedStatic.close();
    }

    @Test
    public void handlePortRequestWhenNoDevices() {
        underTest.requestPort(pluginCall, List.of(serialPortFilter));

        verify(pluginCall).reject("No devices found");
    }

    @Test
    public void shouldReturnDevices() {
        when(usbSerialProber.findAllDrivers(usbManager)).thenReturn(List.of(firstDriver));

        var list = underTest.getDevices();
        Assert.assertEquals(list.size(), 1);

        var serialPort = list.get(0);
        Assert.assertEquals(serialPort.getPortId(), 4);
        Assert.assertEquals(serialPort.getDeviceInstanceId(), 1);
        Assert.assertEquals(serialPort.getDeviceName(), "Device name");
        Assert.assertEquals(serialPort.getProductId(), 2);
        Assert.assertEquals(serialPort.getVendorId(), 3);
    }

    @Test
    public void shouldReturnSelectedDevices() {
        when(usbSerialProber.findAllDrivers(usbManager)).thenReturn(List.of(firstDriver, secondDriver));

        underTest.selectDevice(4);
        underTest.requestPort(pluginCall, List.of(serialPortFilter));

        verify(usbManager).requestPermission(secondUsbDevice, pendingIntent);
    }

    @Test
    public void shouldPermissionRequestRejection() {
        when(usbSerialProber.findAllDrivers(usbManager)).thenReturn(List.of(firstDriver));
        doThrow(RuntimeException.class).when(usbManager).requestPermission(firstUsbDevice, pendingIntent);

        underTest.requestPort(pluginCall, List.of(serialPortFilter));

        verify(pluginCall).reject(null);
    }

    @Test
    public void shouldReturnDefaultDevice() {
        when(usbSerialProber.findAllDrivers(usbManager)).thenReturn(List.of(firstDriver, secondDriver));

        underTest.selectDevice(-1);
        underTest.requestPort(pluginCall, List.of(serialPortFilter));

        verify(usbManager).requestPermission(firstUsbDevice, pendingIntent);
    }

    @Test
    public void shouldHandleInvalidSelectedDevice() {
        when(usbSerialProber.findAllDrivers(usbManager)).thenReturn(List.of(firstDriver, secondDriver));

        underTest.selectDevice(null);
        underTest.requestPort(pluginCall, List.of(serialPortFilter));

        verify(usbManager).requestPermission(firstUsbDevice, pendingIntent);
    }

    @Test
    public void handlePermissionRequest() {
        when(usbSerialProber.findAllDrivers(usbManager)).thenReturn(List.of(firstDriver));

        underTest.requestPort(pluginCall, List.of(serialPortFilter));

        verify(activity).registerReceiver(any(UsbBroadcastReceiver.class), any(IntentFilter.class));
        verify(usbManager).requestPermission(firstUsbDevice, pendingIntent);
    }

    @Test
    public void shouldAttemptToOpenPortWhenDeviceNotSelected() {
        underTest.open(pluginCall, serialOptions);

        verify(pluginCall).reject("The driver is not selected");
    }

    @Test
    public void shouldAttemptToOpenPortWhenConnectionNotEstablished() {
        when(usbSerialProber.findAllDrivers(usbManager)).thenReturn(List.of(firstDriver));
        when(usbManager.openDevice(firstUsbDevice)).thenReturn(null);
        underTest.requestPort(pluginCall, List.of(serialPortFilter));

        underTest.open(pluginCall, serialOptions);

        verify(pluginCall).reject("Cannot open the device");
    }

    @Test
    public void shouldHandleOpenPortException() throws IOException {
        when(usbSerialProber.findAllDrivers(usbManager)).thenReturn(List.of(firstDriver));
        doThrow(RuntimeException.class).when(serialPort).open(usbDeviceConnection);

        underTest.requestPort(pluginCall, List.of(serialPortFilter));
        underTest.open(pluginCall, serialOptions);

        verify(pluginCall).reject(null);
    }

    @Test
    public void shouldOpenPort() throws IOException {
        when(usbSerialProber.findAllDrivers(usbManager)).thenReturn(List.of(firstDriver));
        underTest.requestPort(pluginCall, List.of(serialPortFilter));
        underTest.open(pluginCall, serialOptions);

        verify(pluginCall).resolve();
        verify(serialPort).open(usbDeviceConnection);
        verify(serialPort).setParameters(115200, 1, 2, 3);
    }

    @Test
    public void shouldClosePort() {
        when(usbSerialProber.findAllDrivers(usbManager)).thenReturn(List.of(firstDriver));
        underTest.requestPort(pluginCall, List.of(serialPortFilter));
        underTest.open(pluginCall, serialOptions);

        underTest.close(pluginCall);
        verify(pluginCall, times(2)).resolve();
    }

    @Test
    public void shouldAttemptToWriteToClosedPort() {
        underTest.write(pluginCall, new byte[100]);

        verify(pluginCall).reject("The port is closed");
    }

    @Test
    public void shouldWritePort() throws IOException {
        when(usbSerialProber.findAllDrivers(usbManager)).thenReturn(List.of(firstDriver));
        underTest.requestPort(pluginCall, List.of(serialPortFilter));
        underTest.open(pluginCall, serialOptions);

        underTest.write(pluginCall, new byte[100]);

        verify(serialPort).write(new byte[100], 1000);
        verify(pluginCall, times(2)).resolve();
    }

    @Test
    public void shouldHandleWriteException() throws IOException {
        when(usbSerialProber.findAllDrivers(usbManager)).thenReturn(List.of(firstDriver));
        when(serialPort.write(new byte[100], 1000)).thenThrow(RuntimeException.class);

        underTest.requestPort(pluginCall, List.of(serialPortFilter));
        underTest.open(pluginCall, serialOptions);

        underTest.write(pluginCall, new byte[100]);

        verify(pluginCall).reject(null);
    }
}
