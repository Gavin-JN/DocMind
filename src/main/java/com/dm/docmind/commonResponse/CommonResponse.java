package com.dm.docmind.commonResponse;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommonResponse<T> {
    private final int status;
    private final String message;
    private String token;
    private T data;

    public CommonResponse(int status, String message) {
        this.status = status;
        this.message = message;
    }

    private CommonResponse(int status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    private CommonResponse(int status, String message, T data, String token) {
        this.status = status;
        this.message = message;
        this.data = data;
        this.token = token;
    }

    public static <T> CommonResponse<T> createForSuccess() {
        return new CommonResponse<>(ResponseCode.SUCCESS.getCode(), ResponseCode.SUCCESS.getDesc());
    }

    public static <T> CommonResponse<T> createForSuccessMessage(String message) {
        return new CommonResponse<>(ResponseCode.SUCCESS.getCode(), message);
    }

    public static <T> CommonResponse<T> createForSuccess(T data) {
        return new CommonResponse<>(ResponseCode.SUCCESS.getCode(), ResponseCode.SUCCESS.getDesc(), data);
    }

    public static <T> CommonResponse<T> createForSuccess(T data, String message) {
        return new CommonResponse<>(ResponseCode.SUCCESS.getCode(), message, data);
    }

    public static <T> CommonResponse<T> createForSuccess(T data, String message, String token) {
        return new CommonResponse<>(ResponseCode.SUCCESS.getCode(), message, data, token);
    }

    public static <T> CommonResponse<T> createForError() {
        return new CommonResponse<>(ResponseCode.ERROR.getCode(), ResponseCode.ERROR.getDesc());
    }

    public static <T> CommonResponse<T> createForError(String message) {
        return new CommonResponse<>(ResponseCode.ERROR.getCode(), message);
    }

    public static <T> CommonResponse<T> createForError(int code, String message) {
        return new CommonResponse<>(code, message);
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

    public String getToken() {
        return token;
    }

    @JsonIgnore
    public Boolean isSuccess() {
        return status == ResponseCode.SUCCESS.getCode();
    }
}
