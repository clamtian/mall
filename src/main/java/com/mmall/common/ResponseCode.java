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
    NO_USER(10, "用户名不存在"),
    NEED_LOGIN(11, "用户未登录"),
    PASSWORD_ERROR(12, "密码错误"),
    ILLEGAL_ARGUMENT(2, "非法参数");

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
