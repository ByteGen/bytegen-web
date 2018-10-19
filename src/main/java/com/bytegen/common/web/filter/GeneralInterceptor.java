package com.bytegen.common.web.filter;

import com.bytegen.common.web.Constant;
import com.bytegen.common.web.StatusCodes;
import com.bytegen.common.web.basic.BaseResponse;
import com.bytegen.common.web.util.NWebUtil;
import com.bytegen.common.web.util.ResponseUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * User: xiang
 * Date: 2018/10/10
 * Desc:
 */
@Component
@Order(Constant.GENERAL_INTERCEPTOR_PRIORITY)
public class GeneralInterceptor implements HandlerInterceptor {

    private Logger logger = LoggerFactory.getLogger(GeneralInterceptor.class);

    @Value("${check_signature:true}")
    private Boolean checkSignature;
    @Value("${timestamp_tolerance:600}")
    private long timestampTolerance;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String traceId = NWebUtil.getTraceIdFromRequest(request);
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        try {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            final NoSignature noSignature = handlerMethod.getMethod().getAnnotation(NoSignature.class);
            if (null == noSignature && checkSignature) {
                return signRelatedCheck(request, response, traceId);
            }

//        String nonce = request.getParameter("nonce");
//        if (StringUtils.isEmpty(nonce) || nonce.length() > 32) {
//            ResponseUtil.outputBaseResponse(response, traceId, Constant.RC_INVALID_PARAM, "invalid nonce");
//            return false;
//        }
            //check duplicate nonce
//        String key = appId + "_" + timestamp + "_" + nonce;
//        String reply;
//        try {
//            reply = sRedis.set(key, "1", "NX", "EX", 3600 * 8);
//        } catch (Exception e) {
//            logger.error("check duplicate nonce fail", e);
//            reply = "OK";
//        }
//        if (StringUtils.isBlank(reply)) {
//            ResponseUtil.outputBaseResponse(response, traceId, Constant.RC_INVALID_PARAM, "invalid nonce");
//            return false;
//        }

            return true;
        } catch (Exception e) {
            logger.error("General interceptor error, exception:{}", e.getMessage(), e);
            ResponseUtil.outputBaseResponse(response,
                    new BaseResponse(traceId, StatusCodes.RC_INTERNAL_ERROR.getResultCode(), "check sign exception"));
            return false;
        }
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse response, Object o, ModelAndView modelAndView) throws Exception {
        String contentType = response.getContentType();
        if (contentType != null) {
            if (!contentType.contains("charset")) {
                contentType = contentType + ";charset=utf-8";
                response.setHeader("Content-Type", contentType);
            }
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object o, Exception e) throws Exception {

    }


    private boolean signRelatedCheck(HttpServletRequest request, HttpServletResponse response, String traceId) throws Exception {
        String appId = request.getParameter(Constant.PARAMETER_APP_ID);
        if (StringUtils.isEmpty(appId)) {
            ResponseUtil.outputBaseResponse(response,
                    new BaseResponse(traceId, StatusCodes.RC_INVALID_PARAM.getResultCode(), "missing app_id"));
            return false;
        }

        String timestamp = request.getParameter(Constant.PARAMETER_TIMESTAMP);
        if (StringUtils.isBlank(timestamp)) {
            ResponseUtil.outputBaseResponse(response,
                    new BaseResponse(traceId, StatusCodes.RC_INVALID_PARAM.getResultCode(), "missing timestamp"));
            return false;
        }

        if (!isValidTimestamp(timestamp, timestampTolerance)) {
            ResponseUtil.outputBaseResponse(response,
                    new BaseResponse(traceId, StatusCodes.RC_INVALID_PARAM.getResultCode(), "timestamp over proof"));
            return false;
        }
        if (!Boolean.parseBoolean(request.getHeader(Constant.GATEWAY_SIGN_VERIFIED))) {
            ResponseUtil.outputBaseResponse(response,
                    new BaseResponse(traceId, StatusCodes.RC_SIGN_FAILED.getResultCode(), "invalid sign"));
            return false;
        }
        return true;
    }

    public boolean isValidTimestamp(String timeStamp, long tolerance) {
        if (StringUtils.isBlank(timeStamp)) {
            return false;
        }

        long reqTime = 0L;
        try {
            reqTime = Long.parseLong(timeStamp);
        } catch (NumberFormatException e) {
            logger.warn("timestamp format error", e);
            return false;
        }
        long now = System.currentTimeMillis() / 1000L;

        return Math.abs(reqTime - now) <= tolerance;
    }

}
