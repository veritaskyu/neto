package com.veritasware.neto.codec.json.message;

import com.veritasware.neto.codec.binary.message.NetoJsonMessage;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by chacker on 2016-10-31.
 */
@Getter
@Setter
@ToString
public class ErrorMessage extends NetoJsonMessage {
        private String errorMessage;

    public ErrorMessage(int statusCode, String errorMessage) {
        this.errorMessage = errorMessage;
        setStatusCode(statusCode);
    }
}
