package com.abs.telecam;

import java.io.Serializable;

public class BlueMessage implements Serializable{
    public String command;
    public String source;
    public String flashMode;
    public byte[] payload;

    public BlueMessage(String command, String source, String flashMode, byte[] payload) {
        this.command = command;
        this.source = source;
        this.payload = payload;
        this.flashMode = flashMode;
    }

    public static BlueMessage newFrom(String command, String source, String flashMode, byte[] payload) {
        return new BlueMessage(command, source, flashMode, payload);
    }
}
