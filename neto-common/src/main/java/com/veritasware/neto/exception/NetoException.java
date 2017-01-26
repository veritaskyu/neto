package com.veritasware.neto.exception;

/**
 * Created by chacker on 2016-11-01.
 */
public class NetoException extends RuntimeException {
    private int errorCode;
    private Object[] objects;

    public NetoException(int errorCode, String message, Object... objs) {
        super(message);
        this.errorCode = errorCode;
        this.objects = objs;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public Object[] getObjects() {
        return objects;
    }

    public void setObjects(Object[] objects) {
        this.objects = objects;
    }
}
