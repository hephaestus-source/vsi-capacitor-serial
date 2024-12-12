package com.hephaestus.serial.request;

public class SerialPortFilter {

    private Integer usbVendorId;
    private Integer usbProductId;
    private Integer bluetoothServiceClassId;

    public Integer getUsbVendorId() {
        return usbVendorId;
    }

    public void setUsbVendorId(Integer usbVendorId) {
        this.usbVendorId = usbVendorId;
    }

    public Integer getUsbProductId() {
        return usbProductId;
    }

    public void setUsbProductId(Integer usbProductId) {
        this.usbProductId = usbProductId;
    }

    public Integer getBluetoothServiceClassId() {
        return bluetoothServiceClassId;
    }

    public void setBluetoothServiceClassId(Integer bluetoothServiceClassId) {
        this.bluetoothServiceClassId = bluetoothServiceClassId;
    }
}
