package com.inkwy.database.enums;

public enum CodeEnum {

    FAILED(-1,"失败"),
    PHOTO_UPLOAD_FAIL(444,"身份证照片认证失败"),

    SENDSMS_CODE_FAIL(12,"发送手机短信验证码失败"),

    SUCCESS(200,"成功"),

    BIND_CARD_SUCCESS(208,"绑卡成功"),

    BIND_CARD_OPEN_SUCCESS(209,"绑卡成功"),

    CHANNEL_TAG_ERROR(210, "通道标识错误"),

    NOT_LOGIN(403,"请先登录"),

    SESSION_EXPIRED(403,"会话已结束"),

    NOT_FREQUENT(999,"操作频繁,请稍后再试"),

    CACHE_OUT_TIME(406, "缓存过期"),

    SYSTEM_ERROR(998, "服务开小差了,请稍后再试~");

    ;

    private final int code;

    private final String message;

    CodeEnum(int code, String message){
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
