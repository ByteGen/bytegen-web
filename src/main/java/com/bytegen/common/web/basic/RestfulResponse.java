package com.bytegen.common.web.basic;

import com.bytegen.common.web.Constant;
import org.slf4j.MDC;

/**
 * User: xiang
 * Date: 2018/10/10
 * Desc:
 */
public class RestfulResponse<T> extends BaseResponse {
    private T data;

    //////// set trace_id with MDC ////////
    public RestfulResponse(int resultCode) {
        this(resultCode, null, null, null);
    }

    public RestfulResponse(int resultCode, String debugMessage) {
        this(resultCode, null, debugMessage, null);
    }

    public RestfulResponse(int resultCode, String displayMessage, String debugMessage) {
        this(resultCode, displayMessage, debugMessage, null);
    }

    public RestfulResponse(int resultCode, T data) {
        this(resultCode, null, null, data);
    }

    public RestfulResponse(int resultCode, String debugMessage, T data) {
        this(resultCode, null, debugMessage, data);
    }

    public RestfulResponse(int resultCode, String displayMessage, String debugMessage, T data) {
        super(MDC.get(Constant.CONTEXT_PROPERTY_TRACE_ID), resultCode, displayMessage, debugMessage);
        this.data = data;
    }


    //////// set trace_id with parameter ////////
    public RestfulResponse(String traceId, int resultCode) {
        this(traceId, resultCode, null, null, null);
    }

    public RestfulResponse(String traceId, int resultCode, String debugMessage) {
        this(traceId, resultCode, null, debugMessage, null);
    }

    public RestfulResponse(String traceId, int resultCode, String displayMessage, String debugMessage) {
        this(traceId, resultCode, displayMessage, debugMessage, null);
    }

    public RestfulResponse(String traceId, int resultCode, T data) {
        this(traceId, resultCode, null, null, data);
    }

    public RestfulResponse(String traceId, int resultCode, String debugMessage, T data) {
        this(traceId, resultCode, null, debugMessage, data);
    }

    public RestfulResponse(String traceId, int resultCode, String displayMessage, String debugMessage, T data) {
        super(traceId, resultCode, displayMessage, debugMessage);
        this.data = data;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "{\"RestfulResponse\":"
                + super.toString()
                + ", \"data\":" + data
                + "}";
    }
}
