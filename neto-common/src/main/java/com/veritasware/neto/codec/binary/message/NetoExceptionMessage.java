package com.veritasware.neto.codec.binary.message;

/**
 * Created by chacker on 2016-10-05.
 */
public class NetoExceptionMessage {
    private String message;


    public NetoExceptionMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
