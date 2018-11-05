package com.bytegen.common.web.filter;

import com.bytegen.common.web.Constant;
import com.bytegen.common.web.RSEnum;
import com.bytegen.common.web.basic.BaseResponse;
import com.bytegen.common.web.util.NWebUtil;
import com.bytegen.common.web.util.ResponseUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * User: xiang
 * Date: 2018/10/10
 * Desc:
 */
@Component
@Order(Constant.AUTHENTICATION_PRIORITY)
public class AuthenticationInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();
        Authentication anno = method.getAnnotation(Authentication.class);
        if (anno != null) {
            String traceId = NWebUtil.getTraceIdFromRequest(request);
            String accountId = request.getHeader(Constant.GATEWAY_ACCOUNT_ID);

            // if not number, it's invalid
            if (!StringUtils.isNumeric(accountId) || Integer.valueOf(accountId) <= 0) {
                accountId = null;
                request.removeAttribute(Constant.GATEWAY_ACCOUNT_ID);
            }

            if (StringUtils.isBlank(accountId) && anno.filterRequest()) {
                ResponseUtil.outputBaseResponse(response,
                        new BaseResponse(traceId, RSEnum.RS_AUTH_FAILED.getCode(), "Auth Failed"));
                return false;
            }
            request.setAttribute(Constant.CONTEXT_PROPERTY_ACCOUNT_ID, accountId);
        }
        return true;
    }

}
