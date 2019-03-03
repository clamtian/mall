package com.mmall.common;

/**
 * Created by lucky on 2019/1/6.
 */

/**
 * 响应结果状态码
 */
public  enum ResponseCode {

    SUCCESS(0, "SUCCESS"),
    ERROR(1, "ERROR"),
    ILLEGAL_ARGUMENT(10, "非法参数"),
    NO_USER(11, "用户不存在"),
    NEED_LOGIN(12, "用户未登录"),
    PASSWORD_ERROR(13, "密码错误"),
    ANSWER_ERROR(14,"答案错误"),
    OVERTIME(15,"超时，请重试"),
    EMILE_EXIST(16,"邮箱已存在"),
    USER_EXIST(17,"用户已存在");

    private final int code;
    private final String desc;

    ResponseCode(int code,String desc){
        this.code = code;
        this.desc = desc;
    }

    public int getCode(){
        return code;
    }

    public String getDesc(){
        return desc;
    }
}
