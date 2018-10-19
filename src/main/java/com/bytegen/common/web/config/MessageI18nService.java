package com.bytegen.common.web.config;

import com.bytegen.common.web.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

/**
 * User: xiang
 * Date: 2018/10/10
 * Desc:
 */
@Service
public class MessageI18nService {
    public static final Logger LOGGER = LoggerFactory.getLogger(MessageI18nService.class);
    @Resource
    private MessageSource messageSource;

    public String i18nMessage(Locale locale, String code, String defaultMessage) {
        if (locale == null) {
            locale = Constant.DEFAULT_LOCALE;
        }
        try {
            return messageSource.getMessage(code, null, locale);
        } catch (NoSuchMessageException e) {
            LOGGER.warn(String.format("no such message with locale [%s] code [%s] defaultMessage [%s]",
                    locale.toString(), code, defaultMessage));
            return defaultMessage;
        }
    }

    public String i18nMessage(HttpServletRequest request, String code, String defaultMessage) {
        return i18nMessage((Locale) request.getAttribute(Constant.CONTEXT_PROPERTY_LOCALE), code, defaultMessage);
    }

    public String i18nMessage(HttpServletRequest request, String code) {
        return i18nMessage(request, code, null);
    }

    public MessageSource getMessageSource() {
        return messageSource;
    }

    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }
}
