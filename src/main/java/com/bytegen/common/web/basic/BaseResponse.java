package com.bytegen.common.web.basic;

import com.bytegen.common.web.Constant;
import com.bytegen.common.web.util.ParamChecker;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;

/**
 * User: xiang
 * Date: 2018/10/10
 * Desc:
 */
public class BaseResponse {
    private String traceId;
    protected int resultCode;
    protected String displayMessage;
    protected String debugMessage;
    protected long serverTime;

    //////// set trace_id with MDC ////////
    public BaseResponse(int resultCode) {
        this(resultCode, null, null);
    }

    public BaseResponse(int resultCode, String debugMessage) {
        this(resultCode, null, debugMessage);
    }

    public BaseResponse(int resultCode, String displayMessage, String debugMessage) {
        this(MDC.get(Constant.CONTEXT_PROPERTY_TRACE_ID), resultCode, displayMessage, debugMessage);
    }


    //////// set trace_id with parameter ////////
    public BaseResponse(String traceId, int resultCode) {
        this(traceId, resultCode, null, null);
    }

    public BaseResponse(String traceId, int resultCode, String debugMessage) {
        this(traceId, resultCode, null, debugMessage);
    }

    public BaseResponse(String traceId, int resultCode, String displayMessage, String debugMessage) {
        ParamChecker.assertThat(StringUtils.isNotBlank(traceId), "trace_id is blank");

        this.traceId = traceId;
        this.resultCode = resultCode;
        this.displayMessage = displayMessage;
        this.debugMessage = debugMessage;
        this.serverTime = System.currentTimeMillis();
    }

    public String getTraceId() {
        return this.traceId;
    }

    public int getResultCode() {
        return this.resultCode;
    }

    public long getServerTime() {
        return this.serverTime;
    }

    public void setServerTime(long serverTime) {
        this.serverTime = serverTime;
    }

    public long resetServerTime() {
        this.serverTime = System.currentTimeMillis();
        return this.serverTime;
    }

    public String getDisplayMessage() {
        return this.displayMessage;
    }

    public void setDisplayMessage(String displayMessage) {
        this.displayMessage = displayMessage;
    }

    public String getDebugMessage() {
        return this.debugMessage;
    }

    public void setDebugMessage(String debugMessage) {
        this.debugMessage = debugMessage;
    }

    @Override
    public String toString() {
        return "{\"BaseResponse\":{"
                + "\"traceId\":\"" + traceId + "\""
                + ", \"resultCode\":\"" + resultCode + "\""
                + ", \"serverTime\":\"" + serverTime + "\""
                + ", \"displayMessage\":\"" + displayMessage + "\""
                + ", \"debugMessage\":\"" + debugMessage + "\""
                + "}}";
    }
}

