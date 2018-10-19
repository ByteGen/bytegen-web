package com.bytegen.common.web.adapter;

import com.bytegen.common.web.util.GsonUtil;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.ValueConstants;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * User: xiang
 * Date: 2018/10/10
 * Desc:
 * <p>
 * this is used to resolve bean parameter from request parameter maps by Gson
 * </p>
 */
public class BeanParamArgumentResolver implements HandlerMethodArgumentResolver {

    private final Gson gson = GsonUtil.getGson();
    private final Map<MethodParameter, NamedValueInfo> namedValueInfoCache = new ConcurrentHashMap<>(64);

    private Gson getGson() {
        return gson;
    }

    /**
     * Create the {@link NamedValueInfo} object for the given method parameter. Implementations typically
     * retrieve the method annotation by means of {@link MethodParameter#getParameterAnnotation(Class)}.
     *
     * @param parameter the method parameter
     * @return the named value information
     */
    private NamedValueInfo createNamedValueInfo(MethodParameter parameter) {
        BeanParam ann = parameter.getParameterAnnotation(BeanParam.class);
        String name = ann.value();
        // not needed for this type, use parameter map as default
//        if (ann.value().isEmpty()) {
//            name = parameter.getParameterName();
//            if (StringUtils.isEmpty(name)) {
//                throw new IllegalArgumentException(
//                        "Name for argument type [" + parameter.getNestedParameterType().getName() +
//                                "] not available, and parameter name information not found in class file either.");
//            }
//        }
        Object defaultValue = null;
        if (!StringUtils.isBlank(ann.defaultValue()) && !ValueConstants.DEFAULT_NONE.equals(ann.defaultValue())) {
            try {
                JsonElement parsed = getGson().toJsonTree(ann.defaultValue());
                defaultValue = getGson().fromJson(parsed, parameter.getParameterType());
            } catch (Exception e) {
                throw new IllegalStateException(
                        "Default value for parameter [" + parameter.getParameterName() + "] " +
                                "with type [" + parameter.getNestedParameterType().getName() + "] does not match.");
            }
        }
        return new NamedValueInfo(name, ann.required(), defaultValue);
    }

    /**
     * Obtain the named value for the given method parameter.
     */
    private NamedValueInfo getNamedValueInfo(MethodParameter parameter) {
        return this.namedValueInfoCache.computeIfAbsent(parameter, this::createNamedValueInfo);
    }


    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        boolean hasAnno = parameter.hasParameterAnnotation(BeanParam.class);
        boolean primitive = isPrimitive(parameter);
        if (hasAnno && primitive) {
            throw new IllegalStateException(
                    "Name for argument type [" + parameter.getNestedParameterType().getName() +
                            "] annotated with BeanParam but primitive.");
        }
        return hasAnno;
    }


    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        Object val = null;

        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        NamedValueInfo info = getNamedValueInfo(parameter);

        try {
            JsonElement parsed = null;

            if (!StringUtils.isEmpty(info.name)) {
                // bind from fixed parameter name
                String var = request.getParameter(info.name);
                if (!StringUtils.isBlank(var)) {
                    parsed = getGson().toJsonTree(var);
                }
            } else {
                // bind from parameter map
                Map<String, String> var = getParameterMapNonArray(request);
                if (null != var && !var.isEmpty()) {
                    parsed = getGson().toJsonTree(var);
                }
            }

            if (null == parsed) {
                // use default
                val = info.defaultValue;
            } else {
                val = getGson().fromJson(parsed, parameter.getParameterType());
            }
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "Parse for argument '" + info.name + "' with type [" + parameter.getNestedParameterType().getName() + "] failed.", e);
        }

        if (info.required && null == val) {
            throw new IllegalStateException("Missing argument '" + info.name +
                    "' for method parameter of type " + parameter.getNestedParameterType().getSimpleName());
        }
        return val;
    }

    private Map<String, String> getParameterMapNonArray(HttpServletRequest request) {
        Enumeration<String> parameterNames = request.getParameterNames();
        if (null == parameterNames) {
            return Collections.emptyMap();
        }

        Map<String, String> map = new HashMap<>();
        while (parameterNames.hasMoreElements()) {
            String key = parameterNames.nextElement();
            map.put(key, request.getParameter(key));
        }
        return map;
    }

    private boolean isPrimitive(MethodParameter parameter) {
        return parameter.getParameterType().isPrimitive();
    }


    protected static class NamedValueInfo {

        private final String name;

        private final boolean required;

        @Nullable
        private final Object defaultValue;

        public NamedValueInfo(String name, boolean required, @Nullable Object defaultValue) {
            this.name = name;
            this.required = required;
            this.defaultValue = defaultValue;
        }
    }
}
