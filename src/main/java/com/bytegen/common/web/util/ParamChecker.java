package com.bytegen.common.web.util;

import com.bytegen.common.web.StatusCode;
import com.bytegen.common.web.StatusCodes;
import com.bytegen.common.web.basic.BasicWebException;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * User: xiang
 * Date: 2018/10/10
 * Desc:
 * Check request parameters
 */
public class ParamChecker {

    /**
     * @param assertion if true will go through, if false will return error with invalid_param
     * @param errMsg
     * @throws BasicWebException
     */
    public static void assertThat(boolean assertion, String errMsg) throws BasicWebException {
        assertThat(assertion, StatusCodes.RC_INVALID_PARAM, errMsg);
    }

    /**
     * @param assertion  if true will go through, if false will return error with input
     *                   statusCode
     * @param statusCode
     * @param errMsg
     * @throws BasicWebException
     */
    public static void assertThat(boolean assertion, StatusCode statusCode, String errMsg) throws BasicWebException {
        if (!assertion) {
            throw new BasicWebException(statusCode, errMsg);
        }
    }


    /**
     * @param targetParam if not null then go through (or string is not empty), else
     *                    return error with input statusCode
     * @param statusCode
     * @param errMsg
     * @throws BasicWebException
     */
    public static void notNullOrBlank(Object targetParam, StatusCode statusCode, String errMsg) throws BasicWebException {
        if (targetParam == null) {
            throw new BasicWebException(statusCode, errMsg);
        }

        if (targetParam instanceof String && StringUtils.isBlank((String) targetParam)) {
            throw new BasicWebException(statusCode, errMsg);
        }
    }

    /**
     * @param targetParam if not null then go through (or string is not empty), else
     *                    return error with invalid param
     * @param errMsg
     * @throws BasicWebException
     */
    public static void notNullOrBlank(Object targetParam, String errMsg) throws BasicWebException {
        if (targetParam == null) {
            throw new BasicWebException(StatusCodes.RC_INVALID_PARAM, errMsg);
        }

        if (targetParam instanceof String && StringUtils.isBlank((String) targetParam)) {
            throw new BasicWebException(StatusCodes.RC_INVALID_PARAM, errMsg);
        }
    }

    /**
     * @param tgtParam1 & tgtParam2 if targetParam1 & targetParam2 are null at same
     *                  time return error with invalid param, else go through
     * @param errMsg
     * @throws BasicWebException
     */
    public static void assertEitherOne(Object tgtParam1, Object tgtParam2, String errMsg) throws BasicWebException {
        if (tgtParam1 == null && tgtParam2 == null) {
            throw new BasicWebException(StatusCodes.RC_INVALID_PARAM, errMsg);
        }
    }

    /**
     * @param targetParam if targetParam is not one of range list return error with invalid
     *                    param, else go through
     * @param fieldName
     * @param range
     * @throws BasicWebException
     */
    public static void assertIn(String targetParam, String fieldName, List<String> range) throws BasicWebException {
        if (targetParam == null) {
            throw new BasicWebException(StatusCodes.RC_INVALID_PARAM,
                    String.format("[%s] should be one of %s", fieldName, range));
        }

        if (!range.contains(targetParam)) {
            throw new BasicWebException(StatusCodes.RC_INVALID_PARAM,
                    String.format("[%s] should be one of %s", fieldName, range));
        }
    }
}
