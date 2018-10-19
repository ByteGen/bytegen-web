package com.bytegen.common.web.filter;


import java.lang.annotation.*;

/**
 * User: xiang
 * Date: 2018/10/10
 * Desc:
 *
 * @see AuthenticationInterceptor
 */
@Documented
@Target(value = ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Authentication {

    boolean filterRequest() default true;
}
