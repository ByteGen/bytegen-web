package com.bytegen.common.web;

/**
 * User: xiang
 * Date: 2018/10/10
 * Desc:
 */
public class ResultStatus {

    private int code;  // used for result_code
    private String message;  // used for response display_message with MessageI18nService

    public ResultStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "{\"ResultStatus\":{"
                + "\"code\":\"" + code + "\""
                + ", \"message\":\"" + message + "\""
                + "}}";
    }
}
