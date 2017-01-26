package com.veritasware.neto.util;

import com.veritasware.neto.model.RoomInfo;
import com.veritasware.neto.model.UserInfo;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

/**
 * Created by chacker on 2016-10-28.
 */
public class NetoChannelUtil {
    public static final String ATTR_USERNAME = "userInfo";
    public static final String ATTR_ROOM = "room";

    public static final AttributeKey<UserInfo> ATTR_KEY_USERINFO = AttributeKey.valueOf(ATTR_USERNAME);
    public static final AttributeKey<Object> ATTR_KEY_ROOM = AttributeKey.valueOf(ATTR_ROOM);

    public static Object getAttribute(ChannelHandlerContext ctx, AttributeKey<Object> key) {
        Attribute<Object> attr = ctx.channel().attr(key);
        return attr.get();
    }

    public static void setUserInfo(Channel channel, UserInfo user) {
        channel.attr(ATTR_KEY_USERINFO).set(user);
    }

    public static UserInfo getUserInfo(Channel channel) {
        return channel.attr(ATTR_KEY_USERINFO).get();
    }

    public static void setChatRoom(Channel channel, RoomInfo roomInfo) {
        channel.attr(ATTR_KEY_ROOM).set(roomInfo);
    }

    public static RoomInfo getRoomInfo(Channel channel) {
        return (RoomInfo) channel.attr(ATTR_KEY_ROOM).get();
    }

    public static void removeRoomInfo(Channel channel) {
        channel.attr(ATTR_KEY_ROOM).set(null);
    }

}
