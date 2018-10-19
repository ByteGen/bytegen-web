package com.bytegen.common.web.config;

import com.bytegen.common.web.StatusCodes;
import com.bytegen.common.web.basic.BasicWebException;
import com.bytegen.common.web.basic.RestfulResponse;
import com.bytegen.common.web.util.NWebUtil;
import com.bytegen.common.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * User: xiang
 * Date: 2018/10/10
 * Desc:
 */
@ControllerAdvice
public class DefaultCustomExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultCustomExceptionHandler.class);

    @Resource
    protected MessageI18nService messageI18NService;


    @ExceptionHandler(value = HttpRequestMethodNotSupportedException.class)
    @ResponseBody
    public ResponseEntity methodNotSupportException(HttpServletRequest request, Exception e) {
        LOGGER.error("Method not support with " + request.getRequestURL(), e);

        String traceId = NWebUtil.getTraceIdFromRequest(request);
        return ResponseUtil.toBaseResponse(traceId, StatusCodes.RC_METHOD_NOT_ALLOWED.getResultCode(),
                getMessage(request, StatusCodes.RC_METHOD_NOT_ALLOWED.getMessageCode()),
                e.getMessage());
    }

    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    @ResponseBody
    public ResponseEntity contentNotReadable(HttpServletRequest request, HttpMessageNotReadableException e) {
        LOGGER.warn("Content not readable in: " + request.getRequestURL(), e);

        String traceId = NWebUtil.getTraceIdFromRequest(request);
        return ResponseUtil.toBaseResponse(traceId, StatusCodes.RC_BADE_REQUEST.getResultCode(),
                getMessage(request, StatusCodes.RC_BADE_REQUEST.getMessageCode()),
                "HTTP content format not correct");
    }

    @ExceptionHandler(value = MissingServletRequestParameterException.class)
    @ResponseBody
    public ResponseEntity missingParameter(HttpServletRequest request, MissingServletRequestParameterException e) {
        LOGGER.warn("Parameter miss in: " + request.getRequestURL(), e);

        String traceId = NWebUtil.getTraceIdFromRequest(request);
        return ResponseUtil.toBaseResponse(traceId, StatusCodes.RC_MISSING_REQUIRED_PARAM.getResultCode(),
                getMessage(request, StatusCodes.RC_MISSING_REQUIRED_PARAM.getMessageCode()),
                String.format("Missing required params [%s]", e.getParameterName()));
    }

    @ExceptionHandler(value = MethodArgumentTypeMismatchException.class)
    @ResponseBody
    public ResponseEntity typeMismatch(HttpServletRequest request, MethodArgumentTypeMismatchException e) {
        LOGGER.warn("Parameter type mismatch in: " + request.getRequestURL(), e);

        String traceId = NWebUtil.getTraceIdFromRequest(request);
        return ResponseUtil.toBaseResponse(traceId, StatusCodes.RC_TYPE_MISMATCH.getResultCode(),
                getMessage(request, StatusCodes.RC_TYPE_MISMATCH.getMessageCode()),
                String.format("Type mismatching param [%s] ", e.getName()));
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseEntity argumentNotValidException(HttpServletRequest request, MethodArgumentNotValidException e) {
        LOGGER.warn("Argument invalid with: " + request.getRequestURL(), e);

        String traceId = NWebUtil.getTraceIdFromRequest(request);
        return ResponseUtil.toBaseResponse(traceId, StatusCodes.RC_INVALID_PARAM.getResultCode(),
                getMessage(request, StatusCodes.RC_INVALID_PARAM.getMessageCode()),
                e.getBindingResult().getFieldError().getDefaultMessage());
    }

    @ExceptionHandler(value = {IllegalArgumentException.class, IllegalStateException.class})
    @ResponseBody
    public ResponseEntity illegalArgumentException(HttpServletRequest request, RuntimeException e) {
        LOGGER.warn("Argument illegal with " + request.getRequestURL(), e);

        String traceId = NWebUtil.getTraceIdFromRequest(request);
        return ResponseUtil.toBaseResponse(traceId, StatusCodes.RC_INVALID_PARAM.getResultCode(),
                getMessage(request, StatusCodes.RC_INVALID_PARAM.getMessageCode()),
                e.getMessage());
    }

    @ExceptionHandler(value = BasicWebException.class)
    @ResponseBody
    public ResponseEntity tspWebAppException(HttpServletRequest request, BasicWebException e) {
        String traceId = NWebUtil.getTraceIdFromRequest(request);
        return ResponseUtil.generateResponse(
                new RestfulResponse<>(
                        traceId,
                        e.getStatusCode().getResultCode(),
                        getMessage(request, e.getStatusCode().getMessageCode()),
                        e.getDebugMsg(),
                        e.getData()
                )
        );
    }

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public ResponseEntity exception(HttpServletRequest request, Exception e) {
        LOGGER.error("Unexpected exception with " + request.getRequestURL());

        String traceId = NWebUtil.getTraceIdFromRequest(request);
        LOGGER.error(String.format("Request [%s] error!", traceId), e);
        return ResponseUtil.toBaseResponse(traceId, StatusCodes.RC_INTERNAL_ERROR.getResultCode(),
                getMessage(request, StatusCodes.RC_INTERNAL_ERROR.getMessageCode()),
                e.getMessage());
    }

    public void setMessageI18nService(MessageI18nService messageI18nService) {
        this.messageI18NService = messageI18nService;
    }

    protected String getMessage(HttpServletRequest request, String code) {
        return messageI18NService.i18nMessage(request, code, null);
    }
}
