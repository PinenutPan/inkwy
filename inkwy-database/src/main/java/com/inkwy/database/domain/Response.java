package com.inkwy.database.domain;

import com.inkwy.database.enums.CodeEnum;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class Response<T> implements Serializable {

    private int code;

    private String message;

    private T data;

    public Response() {
        super();
    }

    public Response(int code, String message) {
        super();
        this.code = code;
        this.message = message;
    }

    public Response(int code, String message, T data) {
        super();
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> Response<T> bindCardSuccess(T data) {
        Response<T> response = new Response<>();
        response.code = CodeEnum.BIND_CARD_SUCCESS.getCode();
        response.message = CodeEnum.SUCCESS.getMessage();
        response.data = data;
        return response;
    }

    public static <T> Response<T> bindCardOpenSuccess(T data) {
        Response<T> response = new Response<>();
        response.code = CodeEnum.BIND_CARD_OPEN_SUCCESS.getCode();
        response.message = CodeEnum.SUCCESS.getMessage();
        response.data = data;
        return response;
    }

    public static <T> Response<T> success(T data) {
        Response<T> response = new Response<>();
        response.code = CodeEnum.SUCCESS.getCode();
        response.message = CodeEnum.SUCCESS.getMessage();
        response.data = data;
        return response;
    }

    public static Response failed() {
        Response response = new Response<>();
        response.code = CodeEnum.FAILED.getCode();
        response.message = CodeEnum.FAILED.getMessage();
        return response;
    }

    public static <T> Response<T> respSuccess() {
        return respSuccess(null);
    }

    public static <T> Response<T> respSuccess(T data) {
        Response<T> response = new Response<>();
        response.code = CodeEnum.SUCCESS.getCode();
        response.message = CodeEnum.SUCCESS.getMessage();
        response.data = data;
        return response;
    }

    public static <T>  Response<T> respFail(String message) {
        Response<T> response = new Response<>();
        response.code = CodeEnum.FAILED.getCode();
        response.message = message;
        response.data = null;
        return response;
    }

    /**
     * 身份证照片 审核失败的错误
     */
    public static <T>  Response<T> photoUploadFail(Object data) {
        Response<T> response = new Response<>();
        response.code = CodeEnum.PHOTO_UPLOAD_FAIL.getCode();
        response.message = (data == null ? CodeEnum.PHOTO_UPLOAD_FAIL.getMessage() : data.toString());
        response.data = null;
        return response;
    }


    public static <T>  Response<T> respFail(Object data) {
        Response<T> response = new Response<>();
        response.code = CodeEnum.FAILED.getCode();
        response.message = (data == null ? CodeEnum.FAILED.getMessage() : data.toString());
        response.data = null;
        return response;
    }

    public static <T> Response<T> resp(int code, String message, T data) {
        Response<T> response = new Response<>();
        response.code = code;
        response.message = message;
        response.data = data;
        return response;
    }

    public static <T> Response<T> resp(int code, String message) {
        return resp(code, message, null);
    }

    public static <T> Response<T> resp(CodeEnum codeEnum, T data) {
        Response<T> response = new Response<>();
        response.code = codeEnum.getCode();
        response.message = codeEnum.getMessage();
        response.data = data;
        return response;
    }

    public static <T> Response<T> resp(CodeEnum codeEnum) {
        return resp(codeEnum, null);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

}
