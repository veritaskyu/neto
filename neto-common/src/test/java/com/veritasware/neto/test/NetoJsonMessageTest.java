package com.veritasware.neto.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.veritasware.neto.test.model.TestInboundByteMessage;
import com.veritasware.neto.test.model.TestOutboundByteMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;
import com.fasterxml.jackson.core.type.TypeReference;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * Created by chacker on 2016-02-16.
 */
public class NetoJsonMessageTest {

    private final Charset charset = Charset.forName("UTF-8");
    private TestOutboundByteMessage outboundMessage;
    private ByteBuf packets;
    private TestInboundByteMessage inboundMessage;

    @Before
    public void messageInit() {
        outboundMessage = new TestOutboundByteMessage(0, Unpooled.buffer(4096));
        outboundMessage.setByteMsg((byte) 0x01);
        outboundMessage.setShortMsg((short) 0x02);
        outboundMessage.setIntMsg(0x03);
        outboundMessage.setLongMsg(0x04L);
        outboundMessage.setStringMessage("Hello!, 안녕하세요! 您好！ 初めまして。!");
        outboundMessage.setBytesMsg(new byte[]{0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08});
        packets = outboundMessage.buildMessage(charset);
        inboundMessage = new TestInboundByteMessage(packets);
        inboundMessage.buildMessage(charset);
    }

    @Test
    public void inOutMsgSameTest() throws Exception {
        assertEquals(outboundMessage.getByteMsg(), inboundMessage.getByteMsg());
        assertEquals(outboundMessage.getShortMsg(), inboundMessage.getShortMsg());
        assertEquals(outboundMessage.getIntMsg(),  inboundMessage.getIntMsg());
        assertArrayEquals(outboundMessage.getBytesMsg(), inboundMessage.getBytesMsg());
        assertEquals(outboundMessage.getStringMessage(), inboundMessage.getStringMessage());
    }

    @Test
    public void stringJsonTest() {

        class NetoJsonBaseInboundMessage {
            private String command;
            private Object data;

            public String getCommand() {
                return command;
            }

            public void setCommand(String command) {
                this.command = command;
            }

            public Object getData() {
                return data;
            }

            public void setData(Object data) {
                this.data = data;
            }
        }

        class NetoChatMessage {
            private String message;

            public String getMessage() {
                return message;
            }

            public void setMessage(String message) {
                this.message = message;
            }

            @Override
            public String toString() {
                return "NetoChatMessage{" +
                        "clientIp='" + message + '\'' +
                        '}';
            }
        }

        NetoChatMessage nc = new NetoChatMessage();
        nc.setMessage("TEST 메시지");

        NetoJsonBaseInboundMessage n = new NetoJsonBaseInboundMessage();
        n.setCommand("0001");
        n.setData(nc);

        ObjectMapper om = new ObjectMapper();

        try {
            String s = om.writeValueAsString(n);

            System.out.println(s);

            Object o = om.readValue(s, new TypeReference<Map<String, Object>>() {
            });

            System.out.println(o);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
