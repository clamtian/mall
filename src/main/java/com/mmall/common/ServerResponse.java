package com.mmall.common;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.Serializable;

/**
 * Created by lucky on 2019/1/6.
 */
//在序列化Json时，忽略值为null的属性
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)

/**
 * 返回给前台的响应
 */
public class ServerResponse<T> implements Serializable {

    private int status;//状态
    private String msg;//状态信息
    private T Data;//返回数据

    private ServerResponse(int status) {
        this.status = status;
    }

    private ServerResponse(int status, String msg) {
        this.msg = msg;
        this.status = status;
    }

    private ServerResponse(int status, T Data) {
        this.status = status;
        this.Data = Data;
    }

    private ServerResponse(int status, String msg, T Data) {
        this.status = status;
        this.msg = msg;
        this.Data = Data;
    }

    @JsonIgnore
    //从Json序列化结果中将其剔除
    public boolean isSuccess() {
        return this.status == ResponseCode.SUCCESS.getCode();
    }

    public int getStatus() {
        return status;
    }

    public String getMsg() {
        return msg;
    }

    public T getData() {
        return Data;
    }

    public static <T> ServerResponse<T> createBySuccess() {
        return new ServerResponse(ResponseCode.SUCCESS.getCode());
    }

    public static <T> ServerResponse<T> createBySuccessMessage(String msg) {
        return new ServerResponse(ResponseCode.SUCCESS.getCode(), msg);
    }

    public static <T> ServerResponse<T> createBySuccess(T data) {
        return new ServerResponse(ResponseCode.SUCCESS.getCode(), data);
    }

    public static <T> ServerResponse<T> createBySuccess(String msg, T data) {
        return new ServerResponse(ResponseCode.SUCCESS.getCode(), msg, data);
    }

    public static <T> ServerResponse<T> createByError() {
        return new ServerResponse(ResponseCode.ERROR.getCode(), ResponseCode.ERROR.getDesc());
    }

    public static <T> ServerResponse<T> createByErrorMessage(String errorMessage) {
        return new ServerResponse(ResponseCode.ERROR.getCode(), errorMessage);
    }

    public static <T> ServerResponse<T> createByErrorMessageCode(String errorMessage, int errorCode) {
        return new ServerResponse(errorCode, errorMessage);
    }
}
