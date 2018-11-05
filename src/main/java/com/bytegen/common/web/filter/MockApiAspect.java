package com.bytegen.common.web.filter;

import com.bytegen.common.web.Constant;
import com.bytegen.common.web.RSEnum;
import com.bytegen.common.web.basic.BasicWebException;
import com.bytegen.common.web.util.NWebUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

@Aspect
@Component
@Order(Constant.MOCK_SERVICE_VERIFY_PRIORITY)
public class MockApiAspect {

    private static final Logger LOGGER = LoggerFactory.getLogger(MockApiAspect.class);

    @Value("${mock.api.enable:false}")
    private boolean mockServiceEnable;

    @Before("@annotation(com.bytegen.common.web.filter.MockApi)")
    public void checkCaptcha(JoinPoint jp) throws Throwable {
        final MethodSignature signature = (MethodSignature) jp.getSignature();
        final Method method = signature.getMethod();

        if (null != method.getAnnotation(MockApi.class) && !mockServiceEnable) {
            HttpServletRequest request = NWebUtil.getCurrentHttpRequest();

            LOGGER.warn("Received mock api request unexpected: {}", request.getRequestURI());
            throw new BasicWebException(RSEnum.RS_REQUEST_FORBIDDEN, "access mock service is not allowed");
        }
    }

}
