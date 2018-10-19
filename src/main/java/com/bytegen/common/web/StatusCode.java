package com.bytegen.common.web;

/**
 * User: xiang
 * Date: 2018/10/10
 * Desc:
 */
public class StatusCode {

    private int resultCode;  // used for result_code
    private String messageCode;  // used for response display_message with MessageI18nService

    public StatusCode(int resultCode, String messageCode) {
        this.resultCode = resultCode;
        this.messageCode = messageCode;
    }

    public int getResultCode() {
        return resultCode;
    }

    public String getMessageCode() {
        return messageCode;
    }

    @Override
    public String toString() {
        return "{\"StatusCode\":{"
                + "\"resultCode\":\"" + resultCode + "\""
                + ", \"messageCode\":\"" + messageCode + "\""
                + "}}";
    }
}
