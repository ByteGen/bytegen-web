package com.bytegen.common.web.basic;

import com.bytegen.common.web.StatusCode;
import com.bytegen.common.web.util.ParamChecker;

/**
 * User: xiang
 * Date: 2018/10/10
 * Desc:
 */
public class BasicWebException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private StatusCode statusCode;
    private String debugMsg;
    private Object data;

    public BasicWebException(StatusCode statusCode) {
        super();
        ParamChecker.assertThat(null != statusCode, "statusCode is null");

        this.statusCode = statusCode;
    }

    public BasicWebException(StatusCode statusCode, String debugMsg) {
        super(debugMsg);
        ParamChecker.assertThat(null != statusCode, "statusCode is null");

        this.statusCode = statusCode;
        this.debugMsg = debugMsg;
    }

    public BasicWebException(StatusCode statusCode, String debugMsg, Object data) {
        super(debugMsg);
        ParamChecker.assertThat(null != statusCode, "statusCode is null");

        this.statusCode = statusCode;
        this.debugMsg = debugMsg;
        this.data = data;
    }


    public BasicWebException(StatusCode statusCode, Throwable e) {
        this(statusCode, null, e);
    }

    public BasicWebException(StatusCode statusCode, String debugMsg, Throwable e) {
        this(statusCode, debugMsg, null, e);
    }

    public BasicWebException(StatusCode statusCode, String debugMsg, Object data, Throwable e) {
        super(debugMsg, e);
        ParamChecker.assertThat(null != statusCode, "statusCode is null");

        this.statusCode = statusCode;
        this.debugMsg = debugMsg;
        this.data = data;
    }


    public StatusCode getStatusCode() {
        return statusCode;
    }

    public String getDebugMsg() {
        return debugMsg;
    }

    public void setDebugMsg(String debugMsg) {
        this.debugMsg = debugMsg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
