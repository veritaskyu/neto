package com.veritasware.neto.codec.binary.message;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by chacker on 2016-10-20.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class NetoJsonMessage {

    private int opcode;
    private int statusCode;
    private String errorMessage;

    public NetoJsonMessage() {
        this.opcode = 0;
    }

    public NetoJsonMessage(int opcode) {
        this.opcode = opcode;
    }

    public int getOpcode() {
        return opcode;
    }

    public void setOpcode(int opcode) {
        this.opcode = opcode;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

}
