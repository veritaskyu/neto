package com.veritasware.neto.channel.server.handler;

import com.google.common.collect.BiMap;
import com.veritasware.neto.channel.server.service.RedisService;
import com.veritasware.neto.codec.json.NetoJsonToMessageDecoder;
import com.veritasware.neto.codec.websocket.NetoJsonStringToMapWebSocketDecoder;
import com.veritasware.neto.codec.json.NetoMessageToJsonEncoder;
import com.veritasware.neto.codec.websocket.NetoMessageToWebsocketFrameEncoder;
import com.veritasware.neto.codec.binary.message.NetoJsonMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.util.List;

/**
 * Created by chacker on 2016-10-20.
 */
public class ProtocolUnificationHandler extends ByteToMessageDecoder {

    static Logger logger = LoggerFactory.getLogger(ProtocolUnificationHandler.class);

    private static final String WEBSOCKET_PATH = "/websocket";

    private boolean isUnificationMode;
    private final SslContext sslCtx;
    private final boolean detectSsl;
    private final int maxFrameLength;
    private final String charset;

    private final BiMap<Integer, Class<? extends NetoJsonMessage>> opcodeMap;

    private RedisService redisService;

    public RedisService getRedisService() {
        return redisService;
    }

    public void setRedisService(RedisService redisService) {
        this.redisService = redisService;
    }

    public ProtocolUnificationHandler(boolean isUnificationMod, BiMap<Integer, Class<? extends NetoJsonMessage>> opcodeMap, SslContext sslCtx, int maxFrameLength, String charset) {
        this(isUnificationMod, opcodeMap, sslCtx, true, maxFrameLength, charset);
    }

    private ProtocolUnificationHandler(boolean isUnificationMode, BiMap<Integer, Class<? extends NetoJsonMessage>> opcodeMap, SslContext sslCtx, boolean detectSsl, int maxFrameLength, String charset) {
        this.isUnificationMode = isUnificationMode;
        this.opcodeMap = opcodeMap;
        this.sslCtx = sslCtx;
        this.detectSsl = detectSsl;
        this.maxFrameLength = maxFrameLength;
        this.charset = charset;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

        // 프로토콜 확인을 위해서 5byte를 확인한다.
        if (in.readableBytes() < 5) {
            // 다음 inbound 시 버퍼에 이전에 전송된 내용이 쌓이므로 버퍼를 비운다.
            in.clear();
            return;
        }

        if (isSsl(in)) {
            enableSsl(ctx);
        } else {

            if (!isUnificationMode) {
                switchToBinary(ctx);
                return;
            }

            final int magicPacket1 = in.getUnsignedByte(in.readerIndex());
            final int magicPacket2 = in.getUnsignedByte(in.readerIndex() + 1);

            if (isHttp(magicPacket1, magicPacket2)) {
                switchToHttp(ctx);
            } else {
                switchToBinary(ctx);
            }
        }
    }

    private static boolean isHttp(int magic1, int magic2) {
        return
                magic1 == 'G' && magic2 == 'E' || // GET
                magic1 == 'P' && magic2 == 'O' || // POST
                magic1 == 'P' && magic2 == 'U' || // PUT
                magic1 == 'H' && magic2 == 'E' || // HEAD
                magic1 == 'O' && magic2 == 'P' || // OPTIONS
                magic1 == 'P' && magic2 == 'A' || // PATCH
                magic1 == 'D' && magic2 == 'E' || // DELETE
                magic1 == 'T' && magic2 == 'R' || // TRACE
                magic1 == 'C' && magic2 == 'O';   // CONNECT
    }

    private boolean isSsl(ByteBuf buf) {
        if (detectSsl) {
            return SslHandler.isEncrypted(buf);
        }
        return false;
    }

    private void enableSsl(ChannelHandlerContext ctx) {

        logger.info("Enabled SSL Context");

        ChannelPipeline p = ctx.pipeline();
        p.addLast("ssl", sslCtx.newHandler(ctx.alloc()));
        p.addLast("unification-ssl", new ProtocolUnificationHandler(isUnificationMode, opcodeMap, sslCtx, false, maxFrameLength, charset));
        p.remove(this);
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.debug("channelActive");
        ctx.fireChannelActive();
    }

    private void switchToBinary(ChannelHandlerContext ctx) {
        ChannelPipeline p = ctx.pipeline();
        p.addLast(new DelimiterBasedFrameDecoder(maxFrameLength, Delimiters.lineDelimiter()));
        p.addLast(new StringDecoder(Charset.forName(charset)));
        p.addLast(new NetoJsonToMessageDecoder(opcodeMap));

        NetoMessageToJsonEncoder netoMessageToJsonEncoder = new NetoMessageToJsonEncoder();
        netoMessageToJsonEncoder.setOpcodeMap(opcodeMap);

        p.addLast(netoMessageToJsonEncoder);
        p.addLast(new MessageHandler(redisService));
        p.remove(this);


        // 핸들러를 다시 등록 했으므로 이벤트를 전파
        ctx.fireChannelActive();
    }

    private void switchToHttp(ChannelHandlerContext ctx) {
        ChannelPipeline p = ctx.pipeline();
        p.addLast(new HttpServerCodec());
        p.addLast(new HttpObjectAggregator(65536));
        p.addLast(new WebSocketServerCompressionHandler());
        p.addLast(new WebSocketServerProtocolHandler(WEBSOCKET_PATH, "ws", true));
        p.addLast(new NetoJsonStringToMapWebSocketDecoder());
        p.addLast(new NetoMessageToWebsocketFrameEncoder());
        p.remove(this);

        // 핸들러를 다시 등록 했으므로 이벤트를 전파
        ctx.fireChannelActive();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
//        super.exceptionCaught(ctx, cause);
    }
}
