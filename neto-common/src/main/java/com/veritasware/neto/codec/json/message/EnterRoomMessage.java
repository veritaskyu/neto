package com.veritasware.neto.codec.json.message;

import com.veritasware.neto.annotation.NetoNotNullOrNotEmpty;
import com.veritasware.neto.model.UserInfo;
import com.veritasware.neto.codec.binary.message.NetoJsonMessage;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Collection;

/**
 * Created by chacker on 2016-10-27.
 */
@Getter
@Setter
@ToString
public class EnterRoomMessage extends NetoJsonMessage {

    public static final int ENTER_ROOM = 0;
    public static final int CREATE_ROOM = 1;

    private int command;

    @NetoNotNullOrNotEmpty
    private String roomId;

    private String roomTitle;

    private Collection<UserInfo> users;
}
