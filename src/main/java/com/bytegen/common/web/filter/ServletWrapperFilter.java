package com.bytegen.common.web.filter;

import com.bytegen.common.web.Constant;
import com.bytegen.common.web.util.GsonUtil;
import com.bytegen.common.web.util.NWebUtil;
import com.bytegen.common.web.util.TraceIdGenerator;
import com.google.common.base.Splitter;
import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.springframework.web.util.WebUtils;

import javax.annotation.PostConstruct;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.*;

@Component
@Order(1)
public class ServletWrapperFilter extends OncePerRequestFilter {

    @Value("${log.response.maxLength:10240}")
    private int maxLength;
    @Value("${log.response.maskBetwixt.uri:}")
    private String shortenResponseLogUris;
    @Value("${log.request.maskHeader.key:}")
    private String maskRequestLogHeaderKeys;

    private List<String> logResponseMaskedUris = null;
    private List<String> logRequestHeaderMaskedKeys = null;

    @PostConstruct
    private void initUriList() {
        Splitter splitter = Splitter.on(",").omitEmptyStrings().trimResults();
        if (StringUtils.isNotBlank(shortenResponseLogUris)) {
            logResponseMaskedUris = splitter.splitToList(shortenResponseLogUris.toLowerCase());
        } else {
            logResponseMaskedUris = Collections.emptyList();
        }

        if (StringUtils.isNotBlank(maskRequestLogHeaderKeys)) {
            logRequestHeaderMaskedKeys = splitter.splitToList(maskRequestLogHeaderKeys.toLowerCase());
        } else {
            logRequestHeaderMaskedKeys = Collections.emptyList();
        }
    }

    private static final Logger logger = LoggerFactory.getLogger(ServletWrapperFilter.class);
    private static final String _REQUEST_TIMESTAMP = "_request_timestamp_72ee57f3e57e43af9128a4dce2346ff1";
    private static final String LOG_TIME = "log_time";
    private static final String LOG_TYPE = "log_type";
    private static final String RESPONSE_ENTITY = "response_entity";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String traceId = getTraceId(request);

        request.setAttribute(_REQUEST_TIMESTAMP, System.currentTimeMillis());
        request.setAttribute(Constant.CONTEXT_PROPERTY_TRACE_ID, traceId);
        request.setAttribute(Constant.CONTEXT_PROPERTY_LOCALE, NWebUtil.toLocale(request.getParameter("lang"), request.getParameter("region")));
        MDC.put(Constant.CONTEXT_PROPERTY_TRACE_ID, traceId);

        // wrapper
        if (!(request instanceof BufferedRequestWrapper)) {
            request = new BufferedRequestWrapper(request);
        }
        if (!(response instanceof ContentCachingResponseWrapper)) {
            response = new ContentCachingResponseWrapper(response);
        }

        try {
            makeRequestLog((BufferedRequestWrapper) request);
            filterChain.doFilter(request, response);
        } finally {
            makeResponseLog(request, response);
            updateResponse(response);

            MDC.clear();
        }
    }

    private static String getTraceId(final HttpServletRequest request) {
        String outerTraceId = request.getParameter(Constant.CONTEXT_PROPERTY_TRACE_ID);
        if (!StringUtils.isBlank(outerTraceId)) {
            return outerTraceId;
        }
        outerTraceId = request.getHeader("X-Request-Id");
        return StringUtils.isBlank(outerTraceId) ? TraceIdGenerator.generateTraceId() : outerTraceId;
    }

    private void updateResponse(HttpServletResponse response) throws IOException {
        ContentCachingResponseWrapper responseWrapper =
                WebUtils.getNativeResponse(response, ContentCachingResponseWrapper.class);
        responseWrapper.copyBodyToResponse();
    }

    private void makeRequestLog(BufferedRequestWrapper request) {
        try {
            Map<String, Object> requestLog = Maps.newHashMap();
            requestLog.put(LOG_TIME, System.currentTimeMillis());
            requestLog.put(LOG_TYPE, "request_log");
            requestLog.put("method", request.getMethod());
            requestLog.put("uri", request.getRequestURI());
            requestLog.put(Constant.CONTEXT_PROPERTY_TRACE_ID, request.getAttribute(Constant.CONTEXT_PROPERTY_TRACE_ID));
            requestLog.put("qs", request.getQueryString());
            final Enumeration<String> headerNames = request.getHeaderNames();
            if (headerNames != null) {
                final Map<String, String> headers = Maps.newHashMap();
                while (headerNames.hasMoreElements()) {
                    final String headerName = headerNames.nextElement();
                    if (logRequestHeaderMaskedKeys.contains(toLowerCase(headerName))) {
                        headers.put(headerName, starFilter(request.getHeader(headerName), 6, 6, 6));
                    } else {
                        headers.put(headerName, request.getHeader(headerName));
                    }
                }
                requestLog.put("headers", headers);
            }

            if (permitsRequestBody(toUpperCase(request.getMethod()))) {
                if (isJson(request.getContentType())) {
                    if (isGZip(request.getHeader(Constant.CONTENT_ENCODING))) {
                        requestLog.put("body", "[gzip json content]");
                    } else {
                        // deserialize to make the json readable
                        ByteBuffer buffer = request.getBuffer();
                        if (null != buffer) {
                            requestLog.put("body", GsonUtil.getGson().fromJson(Constant.UTF8.decode(buffer).toString(), JsonElement.class));
                        }
                    }
                } else if (isMultipartFormData(request.getContentType())) {
                    requestLog.put("body", "[multi part form]");
                } else {
                    final Enumeration<String> paramNames = request.getParameterNames();
                    if (paramNames != null) {
                        final Map<String, String> params = Maps.newHashMap();
                        while (paramNames.hasMoreElements()) {
                            final String paramName = paramNames.nextElement();
                            params.put(paramName, request.getParameter(paramName));
                        }
                        requestLog.put("body", Constant.MAP_QUERY_STRING_JOINER.join(params));
                    }
                }
            }
            String message = GsonUtil.getGson().toJson(requestLog);
            logger.info(message);
            MDC.put(Constant.MDC_REQUEST_LOG, message);
        } catch (Exception e) {
            logger.error("do request interceptor error.", e);
        }
    }

    private static boolean isGZip(final String encoding) {
        return (encoding != null) && encoding.toLowerCase().equals("gzip");
    }

    private static boolean isJson(final String contentType) {
        return (contentType != null) && contentType.toLowerCase().startsWith("application/json");
    }

    private static boolean isMultipartFormData(String contentType) {
        return (contentType != null) && (contentType.toLowerCase().startsWith("multipart/"));
    }

    private void makeResponseLog(HttpServletRequest request, HttpServletResponse response) {
        try {
            Map<String, Object> map = new HashMap<>();
            map.put(LOG_TIME, System.currentTimeMillis());
            map.put(LOG_TYPE, "response_log");

            map.put(Constant.CONTEXT_PROPERTY_TRACE_ID, request.getAttribute(Constant.CONTEXT_PROPERTY_TRACE_ID));
            map.put(Constant.CONTEXT_PROPERTY_ACCOUNT_ID, request.getAttribute(Constant.CONTEXT_PROPERTY_ACCOUNT_ID));
            map.put("qs", request.getQueryString());
            map.put("uri", request.getRequestURI());

            Long requestTimestamp = (Long) request.getAttribute(_REQUEST_TIMESTAMP);
            map.put("latency", null == requestTimestamp ? -1 : System.currentTimeMillis() - requestTimestamp);

            map.put("response_status", response.getStatus());

            String rawBody = getResponsePayload(response);
            // deserialize to make the json readable
            JsonObject entity = GsonUtil.getGson().fromJson(rawBody, JsonObject.class);
            if (logResponseMaskedUris.contains(toLowerCase(request.getRequestURI()))
                    || rawBody.length() > maxLength) {
                if (null != entity && entity.has("data")) {
                    JsonElement data = entity.get("data");
                    if (null != data) {
                        String masked = starFilter(data.toString(), 20, 20, 6);

                        JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty("raw", masked);
                        entity.add("data", jsonObject);
                    }
                } else {
                    entity = new JsonObject();
                    entity.addProperty("raw", "unknown");
                }
            }
            map.put(RESPONSE_ENTITY, entity);

            logger.info(GsonUtil.getGson().toJson(map));
        } catch (Exception e) {
            logger.error("do response log fail", e);
        }
    }

    private String getResponsePayload(HttpServletResponse response) {
        ContentCachingResponseWrapper wrapper =
                WebUtils.getNativeResponse(response, ContentCachingResponseWrapper.class);
        if (null != wrapper) {
            byte[] buf = wrapper.getContentAsByteArray();
            if (buf.length > 0) {
                // int length = Math.min(buf.length, maxLength);
                try {
                    return new String(buf, /*0, length, */wrapper.getCharacterEncoding());
                } catch (UnsupportedEncodingException e) {
                    logger.error("get response payload fail", e);
                }
            }
        }
        return "{}";
    }

    private static String starFilter(String content, int frontNum, int endNum, Integer starNum) {
        if (StringUtils.isEmpty(content)) {
            return content;
        } else {
            int len = content.length();
            if (frontNum < len && frontNum >= 0) {
                if (endNum < len && endNum >= 0) {
                    if (frontNum + endNum >= len) {
                        return content;
                    } else {
                        StringBuilder sb = new StringBuilder("");
                        if (starNum == null) {
                            starNum = len - frontNum - endNum;
                        }

                        for (int i = 0; i < starNum; ++i) {
                            sb.append('*');
                        }

                        return content.substring(0, frontNum) + sb.toString() + content.substring(len - endNum, len);
                    }
                } else {
                    return content;
                }
            } else {
                return content;
            }
        }
    }

    private static String toLowerCase(String content) {
        if (null == content) {
            return null;
        }
        return content.toLowerCase();
    }

    private static String toUpperCase(String content) {
        if (null == content) {
            return null;
        }
        return content.toUpperCase();
    }

    // the following two methods are copied from okhttp3.internal.http.HttpMethod
    private static boolean requiresRequestBody(String method) {
        return method.equals("POST")
                || method.equals("PUT")
                || method.equals("PATCH")
                || method.equals("PROPPATCH") // WebDAV
                || method.equals("REPORT");   // CalDAV/CardDAV (defined in WebDAV Versioning)
    }

    private static boolean permitsRequestBody(String method) {
        return requiresRequestBody(method)
                || method.equals("OPTIONS")
                || method.equals("DELETE")    // Permitted as spec is ambiguous.
                || method.equals("PROPFIND")  // (WebDAV) without body: request <allprop/>
                || method.equals("MKCOL")     // (WebDAV) may contain a body, but behaviour is unspecified
                || method.equals("LOCK");     // (WebDAV) body: create lock, without body: refresh lock
    }
}
