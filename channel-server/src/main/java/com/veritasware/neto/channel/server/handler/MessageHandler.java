package com.veritasware.neto.channel.server.handler;

import com.veritasware.neto.exception.BadAccessException;
import com.veritasware.neto.exception.NotFoundRoomException;
import com.veritasware.neto.exception.NotFoundUserInfoException;
import com.veritasware.neto.Constants;
import com.veritasware.neto.util.NetoDateUtil;
import com.veritasware.neto.codec.json.message.*;
import com.veritasware.neto.codec.binary.message.NetoJsonMessage;
import com.veritasware.neto.model.RoomInfo;
import com.veritasware.neto.model.UserInfo;
import com.veritasware.neto.channel.server.service.RedisService;
import com.veritasware.neto.util.NetoChannelUtil;
import com.veritasware.neto.util.NetoValidator;
import com.veritasware.neto.exception.NetoException;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import io.netty.util.internal.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Collection;

/**
 * Created by chacker on 2016-10-27.
 */
public class MessageHandler extends SimpleChannelInboundHandler<NetoJsonMessage> {

    static Logger logger = LoggerFactory.getLogger(MessageHandler.class);

    private static final ChannelGroup globalChannelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);


    private RedisService redisService;

    public MessageHandler(RedisService redisService) {
        this.redisService = redisService;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        leaveRoom(ctx.channel());
        globalChannelGroup.remove(ctx.channel());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

        if (cause instanceof NetoException) {
            NetoException e = (NetoException) cause;

            int errorCode = e.getErrorCode();

            switch (errorCode) {
//                case Constants.StatusCode.PARAMETER_NULL:
//                case Constants.StatusCode.PARAMETER_NULL_OR_EMPTY:
//                    break;
                default:
                    sendMessage(ctx.channel(), new ErrorMessage(errorCode, e.getMessage()));
                    break;
            }
        } else {
            leaveRoom(ctx.channel());
            globalChannelGroup.remove(ctx.channel());
            ctx.close();
        }
    }

    private void leaveRoom(Channel channel) {
        UserInfo userInfo = NetoChannelUtil.getUserInfo(channel);
        RoomInfo roomInfo = NetoChannelUtil.getRoomInfo(channel);

        if (userInfo != null && roomInfo != null) {
            redisService.leaveRoom(roomInfo.getRoomId(), userInfo);
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, NetoJsonMessage msg) throws Exception {

        NetoValidator.validate(msg);

        Channel channel = ctx.channel();

        if (msg instanceof ChatMessage) {

            ChatMessage chat = (ChatMessage) msg;

            sendGroupMessage(channel, chat);

//            if (Constants.MessageType.NORMAL.equals(chat.getContentType())) {
//
//                sendGroupMessage(channel, chat);
//
//            } else if (Constants.MessageType.WHISPER.equals(chat.getContentType())) {
//
//                String receiverId = chat.getReceiverId();
//
//                if (StringUtil.isNullOrEmpty(receiverId)) {
//                    throw new BadAccessException(Constants.StatusCode.FAIL, "Not found receiverId");
//                }
//
//                sendPrivateMessage(receiverId, msg);
//
//            } else if (Constants.MessageType.GLOBAL_NOTICE.equals(chat.getContentType())) {
//
//            } else if (Constants.MessageType.ROOM_NOTICE.equals(chat.getContentType())) {
//
//            } else {
//                throw new BadAccessException(Constants.StatusCode.FAIL, "bad access!");
//            }


        } else if (msg instanceof HeartbeatMessage) { // 연결 유지를 위한 Heartbeat

            sendMessage(channel, msg); // 그냥 바로 전송

        } else if (msg instanceof EnterRoomMessage) { // 룸 생성, 입장

            doEnterRoom(channel, (EnterRoomMessage) msg);

        } else if (msg instanceof LeaveRoomMessage) { // 룸 퇴장

            doLeaveRoom(channel, (LeaveRoomMessage) msg);

        } else if (msg instanceof LoginMessage) { // 채널 서버 접속

            doConnect(channel, (LoginMessage) msg);

        } else if (msg instanceof RoomListAllMessage) {

            doRoomListAll(channel, (RoomListAllMessage) msg);

        } else { // Unknown Message

            throw new BadAccessException(Constants.StatusCode.FAIL, "Bad Access");

        }

    }

    private void doRoomListAll(Channel channel, RoomListAllMessage roomListAll) {

        Collection<RoomInfo> roomList = redisService.getRoomListAll();
        roomListAll.setRoomList(roomList);
        roomListAll.setStatusCode(Constants.StatusCode.OK);

        sendMessage(channel, roomListAll);
    }

    private void doConnect(Channel channel, LoginMessage msg) {
        InetSocketAddress socketAddress = (InetSocketAddress) channel.remoteAddress();
        LoginMessage connected = msg;
        connected.setClientIp(socketAddress.getAddress().getHostAddress());

        String userId = connected.getUserId();

        UserInfo userInfo = new UserInfo();
        userInfo.setConnectTime(NetoDateUtil.getCurrentTime());
        userInfo.setUserId(userId);

        NetoChannelUtil.setUserInfo(channel, userInfo);

        globalChannelGroup.add(channel);

        connected.setStatusCode(Constants.StatusCode.OK);

        channel.writeAndFlush(connected);
    }

    private void doLeaveRoom(Channel channel, LeaveRoomMessage leaveRoom) {
        UserInfo userInfo = NetoChannelUtil.getUserInfo(channel);

        if (userInfo == null || StringUtil.isNullOrEmpty(userInfo.getUserId())) {
            throw new NotFoundUserInfoException(Constants.StatusCode.NOT_FOUND_USERINFO, "Not Found User Info");
        }

        String inputId = leaveRoom.getUserId();
        String sessionId = userInfo.getUserId();

        if (userInfo.getAccessLevel() > Constants.AccessLevel.ADMIN) {
            globalChannelGroup.stream().filter(c -> c != null && leaveRoom.getUserId().equals(NetoChannelUtil.getUserInfo(c).getUserId()))
                                       .forEach(c -> {
                                                        leaveRoom(c);
                                                        leaveRoom.setStatusCode(Constants.StatusCode.OK);
                                                        channel.writeAndFlush(leaveRoom);
                                                     });
        } else if (sessionId.equals(inputId)) {
            leaveRoom(channel);
            leaveRoom.setStatusCode(Constants.StatusCode.OK);
            sendMessage(channel, leaveRoom);
        } else {
            throw new BadAccessException(Constants.StatusCode.FAIL, "bad access!");
        }
    }

    private void doEnterRoom(Channel channel, EnterRoomMessage enterRoom) {

        RoomInfo roomInfo;

        roomInfo = redisService.getRoomInfo(enterRoom.getRoomId());

        // Channel에서 UserInfo를 가져옴
        UserInfo userInfo = NetoChannelUtil.getUserInfo(channel);

        if (userInfo == null) {
            throw new NotFoundUserInfoException(Constants.StatusCode.NOT_FOUND_USERINFO, "Not found User Info");
        }

        // 입력 받은 RoomInfo가 저장소에 없으면서 생성 명령일경우 논리적 Room을 생성
        if (roomInfo == null && enterRoom.getCommand() == EnterRoomMessage.CREATE_ROOM) {
            roomInfo = new RoomInfo();
            roomInfo.setOpenTime(NetoDateUtil.getCurrentTime());
            roomInfo.setRoomTitle(enterRoom.getRoomTitle());
            roomInfo.setRoomId(enterRoom.getRoomId());
            redisService.setRoomInfo(enterRoom.getRoomId(), roomInfo);
        }

        if (roomInfo == null) {
            // 입장하려는 방이 없을 때 Client로 입장 불가 메시지 전송
            throw new NotFoundRoomException(Constants.StatusCode.NOT_FOUND_ROOM, "Not Found Room");
        }

        // 차후 유저정보 정렬을 위해 접속 정보를 셋팅한다.
        userInfo.setCurrentRoomId(roomInfo.getRoomId());
        userInfo.setRoomEnterTime(NetoDateUtil.getCurrentTime());

        Collection<UserInfo> userList = redisService.enterRoomAndGetUserList(enterRoom.getRoomId(), userInfo);

        // Channel에 UserInfo를 Bind
        NetoChannelUtil.setChatRoom(channel, roomInfo);
        enterRoom.setUsers(userList);
        enterRoom.setStatusCode(Constants.StatusCode.OK);

        // 입장 완료 메시지를 입장되어 있는 모든 Client에게 전송
        sendGroupMessage(channel, enterRoom);
    }

    public void sendMessage(Channel channel, NetoJsonMessage netoJsonMessage) {
        channel.writeAndFlush(netoJsonMessage);
    }

    public void sendGlobalBroadcastMessage(NetoJsonMessage netoJsonMessage) {
        if (netoJsonMessage == null) {
            return;
        }
        globalChannelGroup.writeAndFlush(netoJsonMessage);
    }

    public void sendGroupMessage(Channel channel, NetoJsonMessage netoJsonMessage) {
        if (channel == null || netoJsonMessage == null) {
            return;
        }
        globalChannelGroup.stream().filter(c -> c != null &&
                                           NetoChannelUtil.getRoomInfo(c).getRoomId().equals(NetoChannelUtil.getRoomInfo(channel).getRoomId()))
                                   .forEach(c -> c.writeAndFlush(netoJsonMessage));
    }

    public void sendGroupMessage(Channel channel, String roomId, NetoJsonMessage netoJsonMessage) {
        if (channel == null || roomId == null || netoJsonMessage == null) {
            return;
        }
        globalChannelGroup.stream().filter(c -> c != null &&
                                           roomId.equals(NetoChannelUtil.getRoomInfo(c).getRoomId()))
                                   .forEach(c -> c.writeAndFlush(netoJsonMessage));
    }

    public void sendPrivateMessage(String userId, NetoJsonMessage netoJsonMessage) {

        if (userId == null || netoJsonMessage == null) {
            return;
        }

        final int[] count = {0};

        globalChannelGroup.stream().filter(c -> c != null && userId.equals(NetoChannelUtil.getUserInfo(c).getUserId()))
                                   .forEach(
                                           c -> c.writeAndFlush(netoJsonMessage)
                                   );
    }
}
