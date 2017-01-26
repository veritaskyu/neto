package com.veritasware.neto.exception;

import com.veritasware.neto.exception.NetoException;

/**
 * Created by chacker on 2016-11-02.
 */
public class BadAccessException extends NetoException {

    public BadAccessException(int errorCode, String message, Object... objs) {
        super(errorCode, message, objs);
    }
}
