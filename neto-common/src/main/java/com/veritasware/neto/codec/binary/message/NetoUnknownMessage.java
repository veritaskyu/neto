package com.veritasware.neto.codec.binary.message;

/**
 * Created by chacker on 2016-10-27.
 */
public class NetoUnknownMessage extends NetoJsonMessage {
    private String message;

    public NetoUnknownMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
