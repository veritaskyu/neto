package com.veritasware.neto.codec.json.message;

import com.veritasware.neto.annotation.NetoNotNullOrNotEmpty;
import com.veritasware.neto.codec.binary.message.NetoJsonMessage;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by chacker on 2016-10-20.
 */
@Getter
@Setter
public class ChatMessage extends NetoJsonMessage {

    @NetoNotNullOrNotEmpty
    private String senderId;

    private String receiverId;

    private String senderNick;

    private String messageType;

    private String contentType;
    private String content;
}
