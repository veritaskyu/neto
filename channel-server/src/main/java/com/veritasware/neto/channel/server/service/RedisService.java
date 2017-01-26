package com.veritasware.neto.channel.server.service;

import com.veritasware.neto.model.RoomInfo;
import com.veritasware.neto.model.UserInfo;
import io.netty.util.internal.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * Created by chacker on 2016-09-26.
 */
@Service
@Scope("prototype")
public class RedisService {

    static Logger logger = LoggerFactory.getLogger(RedisService.class);

    private static final String NODE_KEY_USERS = "users";
    private static final String NODE_KEY_DATA = "data";
    private static final String NODE_HASH_KEY_ROOM_INFO = "info";


    @Resource(name = "redisTemplate")
    private HashOperations<String, String, String> hashOps;

    @Resource(name = "redisTemplate")
    private SetOperations<String, String> setOps;

    @Resource(name = "redisTemplate")
    private ZSetOperations<String, String> zSetOps;

    @Resource(name = "userInfoJsonTemplate")
    private SetOperations<String, UserInfo> userInfoSetOps;

    @Resource(name = "roomInfoJsonTemplate")
    private HashOperations<String, String, RoomInfo> roomInfoHashOps;

    @Value("${redis.rootPrefix}")
    private String rootPrefix;

    @Value("${redis.channelPrefix}")
    private String channelPrefix;

    private String createNodeKey(String... nodes) {

        if (nodes == null || StringUtil.isNullOrEmpty(nodes[0])) {
            return "";
        }

        final StringBuilder sb = new StringBuilder();

        for (int i = 0, len = nodes.length; i < len; i++) {
            if (i > 0) {
                sb.append(":");
            }
            sb.append(nodes[i]);
        }

        return sb.toString();
    }

    public Long enterRoom(String roomId, UserInfo user) {
        String roomKey = createNodeKey(rootPrefix, channelPrefix, roomId, NODE_KEY_USERS);
        Long userCount = userInfoSetOps.add(roomKey, user);
        return userCount;
    }

    public Collection<UserInfo> enterRoomAndGetUserList(String roomId, UserInfo user) {
        String roomKey = createNodeKey(rootPrefix, channelPrefix, roomId, NODE_KEY_USERS);
        userInfoSetOps.add(roomKey, user);
        return userInfoSetOps.members(roomKey);
    }

    public Set<UserInfo> getRoomUserList(String roomId) {
        String roomKey = createNodeKey(rootPrefix, channelPrefix, roomId, NODE_KEY_USERS);
        Set<UserInfo> members = userInfoSetOps.members(roomKey);
        return members;
    }

    public void setRoomInfo(String roomId, RoomInfo roomInfo) {
        String roomKey = createNodeKey(rootPrefix, channelPrefix, roomId, NODE_KEY_DATA);
        roomInfoHashOps.put(roomKey, NODE_HASH_KEY_ROOM_INFO, roomInfo);
    }

    public RoomInfo getRoomInfo(String roomId) {
        String roomKey = createNodeKey(rootPrefix, channelPrefix, roomId, NODE_KEY_DATA);
        return roomInfoHashOps.get(roomKey, NODE_HASH_KEY_ROOM_INFO);
    }

    public Long deleteRoomInfo(String roomId) {
        String roomKey = createNodeKey(rootPrefix, channelPrefix, roomId, NODE_KEY_DATA);
        return roomInfoHashOps.delete(roomKey, NODE_HASH_KEY_ROOM_INFO);
    }

    public Long leaveRoom(String roomId, UserInfo user) {
        String roomKey = createNodeKey(rootPrefix, channelPrefix, roomId, NODE_KEY_USERS);
        userInfoSetOps.remove(roomKey, user);
        if (userInfoSetOps.size(roomKey) <= 0) {
            setOps.getOperations().delete(roomKey);
        }
        return 0L;
    }

    public Collection<RoomInfo> getRoomListAll() {
        RedisOperations<String, ?> operations = roomInfoHashOps.getOperations();
        String key = createNodeKey(rootPrefix, channelPrefix, "*", NODE_KEY_DATA);
        Set<String> keys = operations.keys(key);

        Iterator<String> it = keys.iterator();

        Collection<RoomInfo> roomSet = new HashSet<RoomInfo>(keys.size());

        while(it.hasNext()) {
            RoomInfo roomInfo = roomInfoHashOps.get(it.next(), NODE_HASH_KEY_ROOM_INFO);
            roomSet.add(roomInfo);
        }
        return roomSet;
    }

}
