package com.veritasware.neto.codec.json.message;

import com.veritasware.neto.codec.binary.message.NetoJsonMessage;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by chacker on 2016-10-26.
 */
@Getter
@Setter
@ToString
public class DisconnectMessage extends NetoJsonMessage {
    private String userId;
    private String clientIp;
    private String disconnectTime;
}
