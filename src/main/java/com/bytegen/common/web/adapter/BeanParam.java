package com.bytegen.common.web.adapter;

import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * User: xiang
 * Date: 2018/10/10
 * Desc:
 * <p>
 * see the {@link BeanParamArgumentResolver}
 * </p>
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface BeanParam {

    /**
     * Note:
     * <p>
     * 1. If empty, {@link HttpServletRequest#getParameterMap} will be used for parse bean
     * 2. If not empty, {@link HttpServletRequest#getParameter} will be used for parse bean
     * </p>
     */
    String value() default "";

    /**
     * Note: get parameter by {@link BeanParam#value} result
     * <p>
     * 1. If empty, null will return and requirement check work
     * 2. If not empty, new Object with null properties will return and requirement check not work
     * </p>
     */
    boolean required() default true;

    String defaultValue() default "";

}
