package com.abs.telecam;

public class DeviceData {
    public DeviceData(String spinnerText, String value) {
        this.spinnerText = spinnerText;
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public String toString() {
        return spinnerText;
    }

    String spinnerText;
    String value;
}
