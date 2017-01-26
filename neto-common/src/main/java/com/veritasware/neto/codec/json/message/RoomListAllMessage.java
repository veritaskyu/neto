package com.veritasware.neto.codec.json.message;

import com.veritasware.neto.model.RoomInfo;
import com.veritasware.neto.codec.binary.message.NetoJsonMessage;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Collection;

/**
 * Created by chacker on 2016-11-01.
 */
@Getter
@Setter
@ToString
public class RoomListAllMessage extends NetoJsonMessage {
    private Collection<RoomInfo> roomList;
}
