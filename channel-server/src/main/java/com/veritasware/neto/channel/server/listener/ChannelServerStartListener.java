package com.veritasware.neto.channel.server.listener;

import com.veritasware.neto.channel.server.config.prop.SocketServerProperty;
import com.veritasware.neto.channel.server.service.ZookeeperService;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by chacker on 2016-09-19.
 */
@Component
public class ChannelServerStartListener implements ChannelFutureListener  {

    static Logger logger = LoggerFactory.getLogger(ChannelServerStartListener.class);

    @Autowired
    private SocketServerProperty socketServerProperty;

    @Autowired
    private ZookeeperService zookeeperService;

    @Override
    public void operationComplete(ChannelFuture future) throws Exception {
        Executor executor = Executors.newCachedThreadPool();
        executor.execute(zookeeperService);
    }
}
