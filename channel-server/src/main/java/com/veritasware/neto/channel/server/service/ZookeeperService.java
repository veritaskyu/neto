package com.veritasware.neto.channel.server.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.veritasware.neto.model.ChannelServerInfo;
import com.veritasware.neto.channel.server.config.prop.SocketServerProperty;
import com.veritasware.neto.channel.server.config.prop.ZookeeperProperty;
import lombok.Getter;
import lombok.Setter;
import org.apache.zookeeper.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by chacker on 2016-09-20.
 */
@Component
public class ZookeeperService implements Runnable, Watcher {

    static Logger logger = LoggerFactory.getLogger(ZookeeperService.class);

    private static final String ROOT_NODE = "/neto";
    private static final String CHILD_NODE = "/channel_servers";
    private static final int DEFAULT_REPLICA = 160;

    @Getter @Setter
    private boolean stop;

    @Autowired
    private ZookeeperProperty zookeeperProperty;

    @Autowired
    private SocketServerProperty socketServerProperty;

    private ZooKeeper zk;

    @Override
    public void run() {
        logger.info("Zookeeper Service Start - {}", socketServerProperty);
        try {
            StringBuffer sb = new StringBuffer();

            sb.append(ROOT_NODE);

            zk = new ZooKeeper(zookeeperProperty.getHost(), 10000, this);

            if (zk.exists(sb.toString(), this) == null) {
                zk.create(sb.toString(), null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }

            sb.append(CHILD_NODE);

            if (zk.exists(sb.toString(), this) == null) {
                zk.create(sb.toString(), null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }

            ChannelServerInfo channelServerInfo = new ChannelServerInfo();

            channelServerInfo.setHost(socketServerProperty.getHostIp());
            channelServerInfo.setPort(socketServerProperty.getBindPort());
            //TODO.
            channelServerInfo.setReplica(DEFAULT_REPLICA);


            ObjectMapper objectMapper = new ObjectMapper();
            String channelServerInfoJsonStr = objectMapper.writeValueAsString(channelServerInfo);

            sb.append("/");
            sb.append(socketServerProperty.getHostIp());

            if (zk.exists(sb.toString(), this) != null) {
                zk.delete(sb.toString(), 0);
            }

            zk.create(sb.toString(), String.valueOf(channelServerInfoJsonStr).getBytes("UTF-8"), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);

            synchronized (this) {
                while (!stop) {
                    wait();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void process(WatchedEvent event) {

    }
}
