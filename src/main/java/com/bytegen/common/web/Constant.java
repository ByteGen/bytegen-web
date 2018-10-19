package com.bytegen.common.web;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

import java.nio.charset.Charset;
import java.time.ZoneId;
import java.util.Locale;

/**
 * User: xiang
 * Date: 2018/10/10
 * Desc:
 */
public interface Constant {

    // commons
    public static final Splitter COMMA_SPLITTER = Splitter.on(',').omitEmptyStrings().trimResults();
    public static Joiner.MapJoiner MAP_QUERY_STRING_JOINER = Joiner.on('&').withKeyValueSeparator('=');
    public static final ZoneId BEIJING_TIMEZONE = ZoneId.of("Asia/Shanghai");
    public static final Locale DEFAULT_LOCALE = Locale.SIMPLIFIED_CHINESE;
    public static final Charset UTF8 = Charset.forName("utf-8");
    public static final String CONTENT_ENCODING = "Content-Encoding";


    // header related
    public static final String GATEWAY_SIGN_VERIFIED = "Gateway-Sign-Verified";
    public static final String GATEWAY_ACCOUNT_ID = "Gateway-Account-Id";


    // request attribute
    public static final String MDC_REQUEST_LOG = "_request_log";
    public static final String CONTEXT_PROPERTY_LOCALE = "_request_locale";
    public static final String CONTEXT_PROPERTY_TRACE_ID = "trace_id";
    public static final String CONTEXT_PROPERTY_ACCOUNT_ID = "account_id";


    // parameter related
    public static final String PARAMETER_APP_ID = "app_id";
    public static final String PARAMETER_TIMESTAMP = "timestamp";
    public static final String PARAMETER_SIGN = "sign";
    public static final String APP_SECRET = "app_secret";


    // filter priority
    public static final int GENERAL_INTERCEPTOR_PRIORITY = 2;
    public static final int MOCK_SERVICE_VERIFY_PRIORITY = 200;
    public static final int AUTHENTICATION_PRIORITY = 201;


}
