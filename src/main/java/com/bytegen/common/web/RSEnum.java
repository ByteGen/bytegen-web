package com.bytegen.common.web;

import org.springframework.http.HttpStatus;

/**
 * User: xiang
 * Date: 2018/10/10
 * Desc: Enumeration of ResultStatus
 */
public interface RSEnum {

    ResultStatus RS_SUCCESS = new ResultStatus(HttpStatus.OK.value(), "success");
    ResultStatus RS_BADE_REQUEST = new ResultStatus(HttpStatus.BAD_REQUEST.value(), "bad_request");
    ResultStatus RS_INTERNAL_ERROR = new ResultStatus(HttpStatus.INTERNAL_SERVER_ERROR.value(), "internal_error");
    ResultStatus RS_METHOD_NOT_ALLOWED = new ResultStatus(HttpStatus.METHOD_NOT_ALLOWED.value(), "method_not_allowed");
    ResultStatus RS_AUTH_FAILED = new ResultStatus(HttpStatus.UNAUTHORIZED.value(), "auth_failed");
    ResultStatus RS_REQUEST_FORBIDDEN = new ResultStatus(HttpStatus.FORBIDDEN.value(), "request_forbidden");
    ResultStatus RS_RESOURCE_NOT_FOUND = new ResultStatus(HttpStatus.NOT_FOUND.value(), "resource_not_found");
    ResultStatus RS_SIGN_FAILED = new ResultStatus(HttpStatus.BAD_REQUEST.value(), "sign_failed");

    ResultStatus RS_FAILURE = new ResultStatus(5500, "failure");
    ResultStatus RS_PERSISTENCE_ERROR = new ResultStatus(5501, "persistence_error");

    ResultStatus RS_INVALID_PARAM = new ResultStatus(5400, "invalid_param");
    ResultStatus RS_MISSING_REQUIRED_PARAM = new ResultStatus(5401, "missing_required_param");
    ResultStatus RS_TYPE_MISMATCH = new ResultStatus(5402, "type_mismatch");

}
