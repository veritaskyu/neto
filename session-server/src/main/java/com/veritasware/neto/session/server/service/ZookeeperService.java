package com.veritasware.neto.session.server.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.veritasware.neto.session.server.model.ChannelInfo;
import com.veritasware.neto.session.server.util.ConsistentHashing;
import com.veritasware.neto.session.server.config.property.ZookeeperProperty;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.ListUtils;
import org.apache.zookeeper.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by chacker on 2016-09-20.
 */

@Component
public class ZookeeperService implements Runnable, Watcher, AsyncCallback.ChildrenCallback {

    static Logger logger = LoggerFactory.getLogger(ZookeeperService.class);

    private static final String ROOT_NODE = "/neto";
    private static final String CHILD_NODE = "/channel_servers";
    private static final int DEFAULT_REPLICA = 160;

    @Getter @Setter
    private boolean stop;

    @Autowired private ZookeeperProperty zookeeperProperty;

    @Autowired
    @Qualifier("beforeChannels")
    private List<ChannelInfo> beforeChannels;
//            = new ArrayList<ChannelInfo>();

    @Autowired
    @Qualifier("consistentHashingMap")
    private ConsistentHashing<ChannelInfo> consistentHashingMap;

    private ZooKeeper zk;

    @Override
    public void run() {

        try {

            StringBuffer sb = new StringBuffer();
            sb.append(ROOT_NODE);
            sb.append(CHILD_NODE);

            zk = new ZooKeeper(zookeeperProperty.getHost(), 10000, this);

            zk.getChildren(sb.toString(), true, this, null);

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
        logger.debug("process - {}", event);

        if (event.getType() == Event.EventType.NodeChildrenChanged) {
            StringBuffer sb = new StringBuffer();
            sb.append(ROOT_NODE);
            sb.append(CHILD_NODE);
            zk.getChildren(sb.toString(), true, this, null);
        }
    }

    @Override
    public void processResult(int rc, String path, Object ctx, List<String> children) {
        List<ChannelInfo> currentChannelList = new ArrayList<ChannelInfo>();
        for (String child : children) {
            StringBuffer sb = new StringBuffer();
            sb.append(ROOT_NODE);
            sb.append(CHILD_NODE);
            sb.append("/");
            sb.append(child);
            try {
                byte[] data = zk.getData(sb.toString(), false, null);
                String channelInfoJsonStr = new String(data, Charset.forName("UTF-8"));
                ObjectMapper m = new ObjectMapper();
                ChannelInfo csi = m.readValue(channelInfoJsonStr, ChannelInfo.class);
                currentChannelList.add(csi);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (beforeChannels.size() != currentChannelList.size()) {
            List<ChannelInfo> deleteServers = ListUtils.subtract(beforeChannels, currentChannelList); // 사라진 서버
            List<ChannelInfo> addServers = ListUtils.subtract(currentChannelList, beforeChannels); // 새로 추가된 서버

            logger.debug("Deleted Server - {}", deleteServers);
            logger.debug("Added Server - {}", addServers);

            Iterator<ChannelInfo> delIt = deleteServers.iterator();
            while(delIt.hasNext()) {
                ChannelInfo next = delIt.next();
                consistentHashingMap.remove(next.getHost(), next.getReplica());
            }

            Iterator<ChannelInfo> addIt = addServers.iterator();
            while (addIt.hasNext()) {
                ChannelInfo next = addIt.next();
                consistentHashingMap.add(next.getHost(), next, next.getReplica());
            }

            beforeChannels.clear();
            beforeChannels.addAll(currentChannelList);
        }

    }
}
