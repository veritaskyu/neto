package com.veritasware.neto.codec.json.message;

import com.veritasware.neto.annotation.NetoNotNullOrNotEmpty;
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
public class LoginMessage extends NetoJsonMessage {

    @NetoNotNullOrNotEmpty
    private String userId;
    private String userPassword;
    private String clientIp;
    private String connectTime;

}
