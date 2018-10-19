package com.bytegen.common.web.util;

import com.bytegen.common.web.Constant;
import com.bytegen.common.web.StatusCodes;
import com.bytegen.common.web.basic.BaseResponse;
import com.bytegen.common.web.basic.RestfulResponse;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

/**
 * User: xiang
 * Date: 2018/10/10
 * Desc:
 */
public class ResponseUtil {

    public static void outputBaseResponse(HttpServletResponse response, BaseResponse entity)
            throws Exception {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setCharacterEncoding(Constant.UTF8.name());
        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(response.getOutputStream(), Charset.forName("UTF-8")));
        writer.write(GsonUtil.getGson().toJson(entity));
        writer.flush();
        writer.close();
    }

    public static <R extends BaseResponse> ResponseEntity generateResponse(R response) {
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public static <R extends BaseResponse> ResponseEntity generateResponse(R response, HttpStatus status) {
        return new ResponseEntity<>(response, status);
    }


    public static ResponseEntity toSuccessResponse() {
        return toSuccessResponse(MDC.get(Constant.CONTEXT_PROPERTY_TRACE_ID), null);
    }

    public static ResponseEntity toSuccessBaseResponse(String traceId) {
        return toSuccessResponse(traceId, null);
    }

    public static ResponseEntity toSuccessResponse(Object data) {
        return toSuccessResponse(MDC.get(Constant.CONTEXT_PROPERTY_TRACE_ID), data);
    }

    public static <T> ResponseEntity toSuccessResponse(String traceId, T data) {
        RestfulResponse<T> response = new RestfulResponse<>(traceId, StatusCodes.RC_SUCCESS.getResultCode(), data);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    public static ResponseEntity toBaseResponse(int resultCode) {
        return toBaseResponse(resultCode, null, null);
    }

    public static ResponseEntity toBaseResponse(int resultCode, String debugMessage) {
        return toBaseResponse(resultCode, null, debugMessage);
    }

    public static ResponseEntity toBaseResponse(int resultCode, String displayMsg, String debugMsg) {
        return toBaseResponse(MDC.get(Constant.CONTEXT_PROPERTY_TRACE_ID), resultCode, displayMsg, debugMsg, HttpStatus.OK);
    }

    public static ResponseEntity toBaseResponse(String traceId, int resultCode) {
        return toBaseResponse(traceId, resultCode, null, null);
    }

    public static ResponseEntity toBaseResponse(String traceId, int resultCode, String debugMsg) {
        return toBaseResponse(traceId, resultCode, null, debugMsg);
    }

    public static ResponseEntity toBaseResponse(String traceId, int resultCode, String displayMsg, String debugMsg) {
        return toBaseResponse(traceId, resultCode, displayMsg, debugMsg, HttpStatus.OK);
    }

    public static ResponseEntity toBaseResponse(String traceId, int resultCode, String displayMsg, String debugMsg, HttpStatus status) {
        return new ResponseEntity<>(new BaseResponse(traceId, resultCode, displayMsg, debugMsg), status);
    }
}
