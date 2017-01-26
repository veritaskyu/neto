package com.veritasware.neto.session.server.service;

import com.veritasware.neto.session.server.model.ChannelInfo;
import com.veritasware.neto.session.server.util.ConsistentHashing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by chacker on 2016-09-22.
 */
@Service
@Scope("prototype")
public class RedisService {

    static Logger logger = LoggerFactory.getLogger(RedisService.class);

    @Value("${neto.channel.limit}")
    private int netoChannelLimit;

    @Value("${redis.rootPrefix}")
    private String rootPrefix;

    @Value("${redis.channelPrefix}")
    private String channelPrefix;

    @Resource(name="redisTemplate")
    private HashOperations<String, String, String> hashOps;

    @Resource(name="redisTemplate")
    private SetOperations<String, String> setOps;

    @Autowired
    private ConsistentHashing<ChannelInfo> consistentHashingMap;

    private String createNodeKey(String... nodes) {
        final StringBuilder sb = new StringBuilder(rootPrefix);
        sb.append(":");
        sb.append(channelPrefix);
        for (String node : nodes) {
            sb.append(":");
            sb.append(node);
        }
        return sb.toString();
    }

    private Long getRoomNumber(String key) {
        Long connectUserCount = setOps.size(key);

        if (connectUserCount == 0 || connectUserCount < netoChannelLimit) {
            return 1L;
        }
        return (connectUserCount / netoChannelLimit) + 1;
    }

    public List<ChannelInfo> getChannels() {
        String key = createNodeKey("*");

        Set<String> roomKeys = setOps.getOperations().keys(key);

        if (roomKeys == null || roomKeys.isEmpty()) {
            return null;
        }

        List<ChannelInfo> channelInfos = new ArrayList<ChannelInfo>(roomKeys.size());

        for (String roomKey : roomKeys) {
            String ownerUser = roomKey.substring(roomKey.lastIndexOf(':') + 1);
            Long roomNumber = getRoomNumber(roomKey);

            StringBuilder sb = new StringBuilder(ownerUser);
            sb.append(roomNumber);

            ChannelInfo channelInfo = consistentHashingMap.get(sb.toString());
            channelInfo.setUserCount(setOps.size(roomKey));
            channelInfo.setUsers(null);
            channelInfo.setOwner(ownerUser);
            channelInfos.add(channelInfo);
        }
        return channelInfos;
    }

    public ChannelInfo getChannel(String ownerUser) {
        String key = createNodeKey(ownerUser);

        if (!setOps.getOperations().hasKey(key)) {
            return null;
        }

        Long roomNumber = getRoomNumber(key);

        Set<String> users = setOps.members(key);

        StringBuilder channelSb = new StringBuilder(ownerUser);
        channelSb.append(roomNumber);

        ChannelInfo channelInfo = consistentHashingMap.get(channelSb.toString());

        channelInfo.setOwner(ownerUser);
        channelInfo.setUsers(users);
        channelInfo.setUserCount(users.size());

        return channelInfo;
    }


}
