package com.bytegen.common.web.basic;

import com.bytegen.common.web.ResultStatus;
import com.bytegen.common.web.util.ParamChecker;

/**
 * User: xiang
 * Date: 2018/10/10
 * Desc:
 */
public class BasicWebException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private ResultStatus resultStatus;
    private String debugMsg;
    private Object data;

    public BasicWebException(ResultStatus resultStatus) {
        super();
        ParamChecker.assertThat(null != resultStatus, "resultStatus is null");

        this.resultStatus = resultStatus;
    }

    public BasicWebException(ResultStatus resultStatus, String debugMsg) {
        super(debugMsg);
        ParamChecker.assertThat(null != resultStatus, "resultStatus is null");

        this.resultStatus = resultStatus;
        this.debugMsg = debugMsg;
    }

    public BasicWebException(ResultStatus resultStatus, String debugMsg, Object data) {
        super(debugMsg);
        ParamChecker.assertThat(null != resultStatus, "resultStatus is null");

        this.resultStatus = resultStatus;
        this.debugMsg = debugMsg;
        this.data = data;
    }


    public BasicWebException(ResultStatus resultStatus, Throwable e) {
        this(resultStatus, null, e);
    }

    public BasicWebException(ResultStatus resultStatus, String debugMsg, Throwable e) {
        this(resultStatus, debugMsg, null, e);
    }

    public BasicWebException(ResultStatus resultStatus, String debugMsg, Object data, Throwable e) {
        super(debugMsg, e);
        ParamChecker.assertThat(null != resultStatus, "resultStatus is null");

        this.resultStatus = resultStatus;
        this.debugMsg = debugMsg;
        this.data = data;
    }


    public ResultStatus getResultStatus() {
        return resultStatus;
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
