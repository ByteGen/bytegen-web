package com.bytegen.common.web.util;

import com.bytegen.common.web.Constant;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

/**
 * User: xiang
 * Date: 2018/10/10
 * Desc:
 */
public class NWebUtil {

    public static Locale toLocale(String language, String countryCode) {
        Locale locale;
        try {
            if (StringUtils.isBlank(language)) {
                locale = Constant.DEFAULT_LOCALE;
            } else {
                if (language.contains("-")) {
                    language = language.substring(0, language.indexOf("-"));
                }
                if (language.contains("_")) {
                    language = language.substring(0, language.indexOf("_"));
                }
                if (StringUtils.isBlank(countryCode)) {
                    locale = new Locale(language);
                } else {
                    countryCode = StringUtils.upperCase(countryCode);
                    locale = new Locale(language, countryCode);
                }
            }
        } catch (Exception e) {
            locale = Constant.DEFAULT_LOCALE;
        }
        return locale;
    }

    public static HttpServletRequest getCurrentHttpRequest() {
        final RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        return ((ServletRequestAttributes) requestAttributes).getRequest();
    }

    public static String getTraceIdFromRequest() {
        return getTraceIdFromRequest(getCurrentHttpRequest());
    }

    public static String getTraceIdFromRequest(HttpServletRequest request) {
        ParamChecker.assertThat(null != request, "Request is null");

        return (String) request.getAttribute(Constant.CONTEXT_PROPERTY_TRACE_ID);
    }

    /**
     * @return account id
     * @see com.bytegen.common.web.filter.AuthenticationInterceptor
     */
    public static String getCurrentAccountId() {
        return getCurrentAccountId(getCurrentHttpRequest());
    }

    public static String getCurrentAccountId(HttpServletRequest request) {
        ParamChecker.assertThat(null != request, "Request is null");

        return (String) request.getAttribute(Constant.CONTEXT_PROPERTY_ACCOUNT_ID);
    }

    public static String getRemoteIpAddr(HttpServletRequest request) {
        final String xRealIp = request.getHeader("X-Real-IP");
        if (StringUtils.isNotBlank(xRealIp))
            return xRealIp.trim();
        final String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (StringUtils.isBlank(xForwardedFor)) return StringUtils.EMPTY;
        if (!StringUtils.contains(xForwardedFor, ',')) return xForwardedFor;
        return Constant.COMMA_SPLITTER.splitToList(xForwardedFor).get(0);
    }
}
