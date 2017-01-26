package com.veritasware.neto;

/**
 * Created by chacker on 2016-10-28.
 */
public class Constants {

    public static final class MessageType {
        public static final String NORMAL = "normal";
        public static final String GLOBAL_NOTICE = "globalNotice";
        public static final String ROOM_NOTICE = "roomNotice";
        public static final String WHISPER = "whisper";
    }

    public static final class ContentType {
        public static final String LINK = "link";
        public static final String IMAGE = "image";
        public static final String MESSAGE = "message";


        public static final String EMOTICON = "emoticon";
    }

    public static final class StatusCode {

        public static final int OK = 1000;
        public static final int FAIL = -1000;
        public static final int UNKNOWN = -9999;

        public static final int NOT_FOUND_ROOM_ID  = -1001;
        public static final int NOT_FOUND_ROOM     = -1002;
        public static final int NOT_FOUND_USERINFO = -1003;
        public static final int NOT_FOUND_PARAMETER = -1004;
        public static final int PARAMETER_NULL = -1005;
        public static final int PARAMETER_NULL_OR_EMPTY = -1006;

    }

    public static class AccessLevel {
        public static final int ADMIN = 200;
    }
}
