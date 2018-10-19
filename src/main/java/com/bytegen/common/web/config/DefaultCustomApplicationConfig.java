package com.bytegen.common.web.config;

import com.bytegen.common.metrics.spring.MetricsInterceptor;
import com.bytegen.common.web.adapter.BeanParamArgumentResolver;
import com.bytegen.common.web.filter.AuthenticationInterceptor;
import com.bytegen.common.web.filter.GeneralInterceptor;
import com.bytegen.common.web.util.GsonUtil;
import com.google.gson.Gson;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * User: xiang
 * Date: 2018/10/10
 * Desc:
 */
@Configuration
public class DefaultCustomApplicationConfig implements WebMvcConfigurer {

    @Resource
    private GeneralInterceptor generalInterceptor;
    @Resource
    private AuthenticationInterceptor authenticationInterceptor;

    //////// web interceptors ////////
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new MetricsInterceptor()).addPathPatterns("/**");
        registry.addInterceptor(generalInterceptor).addPathPatterns("/**");
        registry.addInterceptor(authenticationInterceptor).addPathPatterns("/**");
    }

    //////// argument resolvers ////////
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        // add bean argument resolver
        argumentResolvers.add(new BeanParamArgumentResolver());
    }

    //////// use Gson as serialization/deserialization tool instead of Jackson ////////
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        // add GsonHttpMessageConverter
        GsonHttpMessageConverter gsonHttpMessageConverter = new GsonHttpMessageConverter(gson());
        gsonHttpMessageConverter.setSupportedMediaTypes(Arrays.asList(MediaType.APPLICATION_JSON, new MediaType("application", "*+json")));

        converters.add(gsonHttpMessageConverter);
    }

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        // delete MappingJackson2HttpMessageConverter
        converters.removeIf(httpMessageConverter -> httpMessageConverter instanceof MappingJackson2HttpMessageConverter);
    }

    @Bean
    public Gson gson() {
        return GsonUtil.getGson();
    }

    //////// international messages config ////////
    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("i18n/messages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }
}