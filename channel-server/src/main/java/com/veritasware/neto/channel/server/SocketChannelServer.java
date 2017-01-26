package com.veritasware.neto.channel.server;

import com.veritasware.neto.channel.server.config.prop.SocketServerProperty;
import com.veritasware.neto.channel.server.listener.ChannelServerStartListener;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by chacker on 2016-09-19.
 */

@Getter
@Setter
@Component
public class SocketChannelServer implements Runnable {

    static Logger logger = LoggerFactory.getLogger(SocketChannelServer.class);

    @Autowired private SocketServerProperty socketServerProperty;
    @Autowired private ChannelServerStartListener channelServerStartListener;

    @Autowired
    @Qualifier("channelInitializer")
    private ChannelInboundHandlerAdapter channelInitializer;

    @Value("${channelServer.socketBacklog}")
    private int SO_BACKLOG;

    @Override
    public void run() {

        EventLoopGroup bossGroup = new NioEventLoopGroup(socketServerProperty.getBossThreadCount());
        EventLoopGroup workerGroup = new NioEventLoopGroup(socketServerProperty.getWorkerThreadCount());

        ChannelFuture closeFuture = null;

        try {

            ServerBootstrap b = new ServerBootstrap();

            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(channelInitializer)
                    .option(ChannelOption.SO_BACKLOG, SO_BACKLOG)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture bind = b.bind(socketServerProperty.getBindPort());
            ChannelFuture sync = bind.sync();

            sync.addListener(channelServerStartListener);

            Channel ch = sync.channel();
            closeFuture = ch.closeFuture();
            closeFuture.sync();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
