package com.veritasware.neto.codec.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.BiMap;
import com.veritasware.neto.codec.binary.message.NetoJsonMessage;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.util.CharsetUtil;

import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.List;

/**
 * Created by chacker on 2016-10-18.
 */
public class NetoMessageToJsonEncoder extends MessageToMessageEncoder<NetoJsonMessage> {

    private BiMap<Integer, Class<? extends NetoJsonMessage>> opcodeMap;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final Charset charset;

    public BiMap<Integer, Class<? extends NetoJsonMessage>> getOpcodeMap() {
        return opcodeMap;
    }

    public void setOpcodeMap(BiMap<Integer, Class<? extends NetoJsonMessage>> opcodeMap) {
        this.opcodeMap = opcodeMap;
    }

    public NetoMessageToJsonEncoder() {
        this(CharsetUtil.UTF_8);
    }

    public NetoMessageToJsonEncoder(Charset charset) {
        this.charset = charset;
    }

    public NetoMessageToJsonEncoder(String charset) {
        this.charset = Charset.forName(charset);
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, NetoJsonMessage msg, List<Object> out) throws Exception {

        if (opcodeMap != null) {
            Integer opcode = opcodeMap.inverse().get(msg.getClass());
            msg.setOpcode(opcode);
        }

        String result = objectMapper.writeValueAsString(msg) + "\n";

        if (result.length() == 0) {
            return;
        }

        out.add(ByteBufUtil.encodeString(ctx.alloc(), CharBuffer.wrap(result), charset));
    }
}
