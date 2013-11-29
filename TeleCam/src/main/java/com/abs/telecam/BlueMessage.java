package com.abs.telecam;

import java.io.Serializable;

public class BlueMessage implements Serializable{
    public String command;
    public String source;
    public byte[] payload;

    public BlueMessage(String command, String source,byte[] payload) {
        this.command = command;
        this.source = source;
        this.payload = payload;
    }

    public static BlueMessage newFrom(String command, String source, byte[] payload) {
        return new BlueMessage(command, source, payload);
    }
}
