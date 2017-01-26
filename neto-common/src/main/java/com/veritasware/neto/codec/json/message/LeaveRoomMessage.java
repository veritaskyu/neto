package com.veritasware.neto.codec.json.message;

import com.veritasware.neto.annotation.NetoNotNullOrNotEmpty;
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
public class LeaveRoomMessage extends NetoJsonMessage {

    @NetoNotNullOrNotEmpty
    private String userId;
}
