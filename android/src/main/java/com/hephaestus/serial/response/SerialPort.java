package com.hephaestus.serial.response;

import com.getcapacitor.JSObject;
import java.util.Objects;
import org.json.JSONException;
import org.json.JSONObject;

public class SerialPort {

    private int portId;
    private String portName;
    private int deviceInstanceId;
    private String deviceName;
    private int productId;
    private int vendorId;
    private String usbDriverName;

    public int getPortId() {
        return portId;
    }

    public void setPortId(int portId) {
        this.portId = portId;
    }

    public String getPortName() {
        return portName;
    }

    public void setPortName(String portName) {
        this.portName = portName;
    }

    public int getDeviceInstanceId() {
        return deviceInstanceId;
    }

    public void setDeviceInstanceId(int deviceInstanceId) {
        this.deviceInstanceId = deviceInstanceId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getVendorId() {
        return vendorId;
    }

    public void setVendorId(int vendorId) {
        this.vendorId = vendorId;
    }

    public String getUsbDriverName() {
        return usbDriverName;
    }

    public void setUsbDriverName(String usbDriverName) {
        this.usbDriverName = usbDriverName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SerialPort that = (SerialPort) o;
        return deviceInstanceId == that.deviceInstanceId && productId == that.productId && vendorId == that.vendorId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(deviceInstanceId, productId, vendorId);
    }

    @Override
    public String toString() {
        return (
            "SerialPort{" +
            "portId='" +
            portId +
            '\'' +
            ", portName='" +
            portName +
            '\'' +
            ", deviceInstanceId=" +
            deviceInstanceId +
            ", deviceName='" +
            deviceName +
            '\'' +
            ", productId=" +
            productId +
            ", vendorId=" +
            vendorId +
            ", usbDriverName='" +
            usbDriverName +
            '\'' +
            '}'
        );
    }

    public JSObject toJSON() {
        var obj = new JSObject();

        obj.put("portId", portId);
        obj.put("portName", portName);
        obj.put("deviceInstanceId", deviceInstanceId);
        obj.put("displayName", deviceName);
        obj.put("productId", productId);
        obj.put("serialNumber", null);
        obj.put("usbDriverName", usbDriverName);
        obj.put("vendorId", vendorId);

        return obj;
    }
}
