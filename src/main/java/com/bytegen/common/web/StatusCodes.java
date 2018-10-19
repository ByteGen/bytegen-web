package com.bytegen.common.web;

import org.springframework.http.HttpStatus;

/**
 * User: xiang
 * Date: 2018/10/10
 * Desc:
 */
public interface StatusCodes {

    StatusCode RC_SUCCESS = new StatusCode(HttpStatus.OK.value(), "success");
    StatusCode RC_BADE_REQUEST = new StatusCode(HttpStatus.BAD_REQUEST.value(), "bad_request");
    StatusCode RC_INTERNAL_ERROR = new StatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value(), "internal_error");
    StatusCode RC_METHOD_NOT_ALLOWED = new StatusCode(HttpStatus.METHOD_NOT_ALLOWED.value(), "method_not_allowed");
    StatusCode RC_AUTH_FAILED = new StatusCode(HttpStatus.UNAUTHORIZED.value(), "auth_failed");
    StatusCode RC_REQUEST_FORBIDDEN = new StatusCode(HttpStatus.FORBIDDEN.value(), "request_forbidden");
    StatusCode RC_RESOURCE_NOT_FOUND = new StatusCode(HttpStatus.NOT_FOUND.value(), "resource_not_found");
    StatusCode RC_SIGN_FAILED = new StatusCode(HttpStatus.BAD_REQUEST.value(), "sign_failed");

    StatusCode RC_FAILURE = new StatusCode(5500, "failure");
    StatusCode RC_PERSISTENCE_ERROR = new StatusCode(5501, "persistence_error");

    StatusCode RC_INVALID_PARAM = new StatusCode(5400, "invalid_param");
    StatusCode RC_MISSING_REQUIRED_PARAM = new StatusCode(5401, "missing_required_param");
    StatusCode RC_TYPE_MISMATCH = new StatusCode(5402, "type_mismatch");

}
