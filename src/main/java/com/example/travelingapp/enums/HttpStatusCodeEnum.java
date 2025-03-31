package com.example.travelingapp.enums;

import lombok.Getter;
import org.springframework.lang.Nullable;

public enum HttpStatusCodeEnum {
    INVALID_INPUT(600, Series.BAD_REQUEST, "Invalid input provided"),
    USERNAME_TAKEN(601, Series.BAD_REQUEST, "Username taken"),
    EMAIL_TAKEN(602, Series.BAD_REQUEST, "Email taken"),
    EMAIL_PATTERN_INVALID(613, Series.BAD_REQUEST, "Email form is invalid"),
    PHONE_FORMAT_INVALID(614, Series.BAD_REQUEST, "Phone format is invalid"),
    PASSWORD_NOT_QUALIFIED(603, Series.BAD_REQUEST, "Password not qualified"),
    USER_NOT_FOUND(604, Series.ERROR, "User not found"),
    CLIENT_SERVER_ERROR(605, Series.ERROR, "Client internal server error"),
    PASSWORD_NOT_CORRECT(606, Series.ERROR, "Password not correct"),
    UNDEFINED_ERROR_CODE(607, Series.ERROR, "Undefined error code"),
    UNDEFINED_HTTP_CODE(608, Series.ERROR, "Undefined http status code"),
    CONFIG_NOT_FOUND(609, Series.ERROR, "Config in database not found"),
    USERNAME_FORMAT_INVALID(610, Series.BAD_REQUEST, "Username format invalid"),
    TOKEN_GENERATE_FAIL(611, Series.ERROR, "Token generated fail"),
    TOKEN_VERIFY_FAIL(612, Series.ERROR, "Token verified fail"),
    TOKEN_EXPIRE(613, Series.ERROR, "Token expires"),
    INPUT_FORMAT_INVALID(614, Series.ERROR, "Input format invalid"),
    OTP_VERIFICATION_FAIL(615, Series.ERROR, "OTP code verification fail"),
    TOKEN_NOT_FOUND(616, Series.ERROR, "OTP code verification fail"),
    MAX_SESSIONS_REACHED(617, Series.ERROR, "Max sessions reached"),
    SESSION_TOKEN_INVALID(618, Series.ERROR, "Session token invalid"),
    SMS_SENT_FAIL(619, Series.ERROR, "SMS sent fail"),
    EMAIL_SENT_FAIL(620, Series.ERROR, "Email sent fail"),
    MAX_OTP_RETRY(621, Series.ERROR, "Max OTP retry exceeds"),
    VERIFICATION_OTP_EXPIRED(622, Series.ERROR, "Verification OTP expires"),
    OTP_BLOCKED_OR_NOT_FOUND(623, Series.ERROR, "OTP is currently blocked or not found"),

    CONTINUE(100, HttpStatusCodeEnum.Series.INFORMATIONAL, "Continue"),
    SWITCHING_PROTOCOLS(101, HttpStatusCodeEnum.Series.INFORMATIONAL, "Switching Protocols"),
    PROCESSING(102, HttpStatusCodeEnum.Series.INFORMATIONAL, "Processing"),
    EARLY_HINTS(103, HttpStatusCodeEnum.Series.INFORMATIONAL, "Early Hints"),
    /**
     * @deprecated
     */
    @Deprecated(
            since = "6.0.5"
    )
    CHECKPOINT(103, HttpStatusCodeEnum.Series.INFORMATIONAL, "Checkpoint"),
    OK(200, HttpStatusCodeEnum.Series.SUCCESSFUL, "OK"),
    CREATED(201, HttpStatusCodeEnum.Series.SUCCESSFUL, "Created"),
    ACCEPTED(202, HttpStatusCodeEnum.Series.SUCCESSFUL, "Accepted"),
    NON_AUTHORITATIVE_INFORMATION(203, HttpStatusCodeEnum.Series.SUCCESSFUL, "Non-Authoritative Information"),
    NO_CONTENT(204, HttpStatusCodeEnum.Series.SUCCESSFUL, "No Content"),
    RESET_CONTENT(205, HttpStatusCodeEnum.Series.SUCCESSFUL, "Reset Content"),
    PARTIAL_CONTENT(206, HttpStatusCodeEnum.Series.SUCCESSFUL, "Partial Content"),
    MULTI_STATUS(207, HttpStatusCodeEnum.Series.SUCCESSFUL, "Multi-Status"),
    ALREADY_REPORTED(208, HttpStatusCodeEnum.Series.SUCCESSFUL, "Already Reported"),
    IM_USED(226, HttpStatusCodeEnum.Series.SUCCESSFUL, "IM Used"),
    MULTIPLE_CHOICES(300, HttpStatusCodeEnum.Series.REDIRECTION, "Multiple Choices"),
    MOVED_PERMANENTLY(301, HttpStatusCodeEnum.Series.REDIRECTION, "Moved Permanently"),
    FOUND(302, HttpStatusCodeEnum.Series.REDIRECTION, "Found"),
    /**
     * @deprecated
     */
    @Deprecated
    MOVED_TEMPORARILY(302, HttpStatusCodeEnum.Series.REDIRECTION, "Moved Temporarily"),
    SEE_OTHER(303, HttpStatusCodeEnum.Series.REDIRECTION, "See Other"),
    NOT_MODIFIED(304, HttpStatusCodeEnum.Series.REDIRECTION, "Not Modified"),
    /**
     * @deprecated
     */
    @Deprecated
    USE_PROXY(305, HttpStatusCodeEnum.Series.REDIRECTION, "Use Proxy"),
    TEMPORARY_REDIRECT(307, HttpStatusCodeEnum.Series.REDIRECTION, "Temporary Redirect"),
    PERMANENT_REDIRECT(308, HttpStatusCodeEnum.Series.REDIRECTION, "Permanent Redirect"),
    BAD_REQUEST(400, HttpStatusCodeEnum.Series.CLIENT_ERROR, "Bad Request"),
    UNAUTHORIZED(401, HttpStatusCodeEnum.Series.CLIENT_ERROR, "Unauthorized"),
    PAYMENT_REQUIRED(402, HttpStatusCodeEnum.Series.CLIENT_ERROR, "Payment Required"),
    FORBIDDEN(403, HttpStatusCodeEnum.Series.CLIENT_ERROR, "Forbidden"),
    NOT_FOUND(404, HttpStatusCodeEnum.Series.CLIENT_ERROR, "Not Found"),
    METHOD_NOT_ALLOWED(405, HttpStatusCodeEnum.Series.CLIENT_ERROR, "Method Not Allowed"),
    NOT_ACCEPTABLE(406, HttpStatusCodeEnum.Series.CLIENT_ERROR, "Not Acceptable"),
    PROXY_AUTHENTICATION_REQUIRED(407, HttpStatusCodeEnum.Series.CLIENT_ERROR, "Proxy Authentication Required"),
    REQUEST_TIMEOUT(408, HttpStatusCodeEnum.Series.CLIENT_ERROR, "Request Timeout"),
    CONFLICT(409, HttpStatusCodeEnum.Series.CLIENT_ERROR, "Conflict"),
    GONE(410, HttpStatusCodeEnum.Series.CLIENT_ERROR, "Gone"),
    LENGTH_REQUIRED(411, HttpStatusCodeEnum.Series.CLIENT_ERROR, "Length Required"),
    PRECONDITION_FAILED(412, HttpStatusCodeEnum.Series.CLIENT_ERROR, "Precondition Failed"),
    PAYLOAD_TOO_LARGE(413, HttpStatusCodeEnum.Series.CLIENT_ERROR, "Payload Too Large"),
    /**
     * @deprecated
     */
    @Deprecated
    REQUEST_ENTITY_TOO_LARGE(413, HttpStatusCodeEnum.Series.CLIENT_ERROR, "Request Entity Too Large"),
    URI_TOO_LONG(414, HttpStatusCodeEnum.Series.CLIENT_ERROR, "URI Too Long"),
    /**
     * @deprecated
     */
    @Deprecated
    REQUEST_URI_TOO_LONG(414, HttpStatusCodeEnum.Series.CLIENT_ERROR, "Request-URI Too Long"),
    UNSUPPORTED_MEDIA_TYPE(415, HttpStatusCodeEnum.Series.CLIENT_ERROR, "Unsupported Media Type"),
    REQUESTED_RANGE_NOT_SATISFIABLE(416, HttpStatusCodeEnum.Series.CLIENT_ERROR, "Requested range not satisfiable"),
    EXPECTATION_FAILED(417, HttpStatusCodeEnum.Series.CLIENT_ERROR, "Expectation Failed"),
    I_AM_A_TEAPOT(418, HttpStatusCodeEnum.Series.CLIENT_ERROR, "I'm a teapot"),
    /**
     * @deprecated
     */
    @Deprecated
    INSUFFICIENT_SPACE_ON_RESOURCE(419, HttpStatusCodeEnum.Series.CLIENT_ERROR, "Insufficient Space On Resource"),
    /**
     * @deprecated
     */
    @Deprecated
    METHOD_FAILURE(420, HttpStatusCodeEnum.Series.CLIENT_ERROR, "Method Failure"),
    /**
     * @deprecated
     */
    @Deprecated
    DESTINATION_LOCKED(421, HttpStatusCodeEnum.Series.CLIENT_ERROR, "Destination Locked"),
    UNPROCESSABLE_ENTITY(422, HttpStatusCodeEnum.Series.CLIENT_ERROR, "Unprocessable Entity"),
    LOCKED(423, HttpStatusCodeEnum.Series.CLIENT_ERROR, "Locked"),
    FAILED_DEPENDENCY(424, HttpStatusCodeEnum.Series.CLIENT_ERROR, "Failed Dependency"),
    TOO_EARLY(425, HttpStatusCodeEnum.Series.CLIENT_ERROR, "Too Early"),
    UPGRADE_REQUIRED(426, HttpStatusCodeEnum.Series.CLIENT_ERROR, "Upgrade Required"),
    PRECONDITION_REQUIRED(428, HttpStatusCodeEnum.Series.CLIENT_ERROR, "Precondition Required"),
    TOO_MANY_REQUESTS(429, HttpStatusCodeEnum.Series.CLIENT_ERROR, "Too Many Requests"),
    REQUEST_HEADER_FIELDS_TOO_LARGE(431, HttpStatusCodeEnum.Series.CLIENT_ERROR, "Request Header Fields Too Large"),
    UNAVAILABLE_FOR_LEGAL_REASONS(451, HttpStatusCodeEnum.Series.CLIENT_ERROR, "Unavailable For Legal Reasons"),
    INTERNAL_SERVER_ERROR(500, HttpStatusCodeEnum.Series.SERVER_ERROR, "Internal Server Error"),
    NOT_IMPLEMENTED(501, HttpStatusCodeEnum.Series.SERVER_ERROR, "Not Implemented"),
    BAD_GATEWAY(502, HttpStatusCodeEnum.Series.SERVER_ERROR, "Bad Gateway"),
    SERVICE_UNAVAILABLE(503, HttpStatusCodeEnum.Series.SERVER_ERROR, "Service Unavailable"),
    GATEWAY_TIMEOUT(504, HttpStatusCodeEnum.Series.SERVER_ERROR, "Gateway Timeout"),
    HTTP_VERSION_NOT_SUPPORTED(505, HttpStatusCodeEnum.Series.SERVER_ERROR, "HTTP Version not supported"),
    VARIANT_ALSO_NEGOTIATES(506, HttpStatusCodeEnum.Series.SERVER_ERROR, "Variant Also Negotiates"),
    INSUFFICIENT_STORAGE(507, HttpStatusCodeEnum.Series.SERVER_ERROR, "Insufficient Storage"),
    LOOP_DETECTED(508, HttpStatusCodeEnum.Series.SERVER_ERROR, "Loop Detected"),
    BANDWIDTH_LIMIT_EXCEEDED(509, HttpStatusCodeEnum.Series.SERVER_ERROR, "Bandwidth Limit Exceeded"),
    NOT_EXTENDED(510, HttpStatusCodeEnum.Series.SERVER_ERROR, "Not Extended"),
    NETWORK_AUTHENTICATION_REQUIRED(511, HttpStatusCodeEnum.Series.SERVER_ERROR, "Network Authentication Required");

    private static final HttpStatusCodeEnum[] VALUES = values();
    public final int value;
    private final Series series;
    @Getter
    private final String reasonPhrase;

    HttpStatusCodeEnum(int value, Series series, String reasonPhrase) {
        this.value = value;
        this.series = series;
        this.reasonPhrase = reasonPhrase;
    }

    public int value() {
        return this.value;
    }

    public Series series() {
        return this.series;
    }

    public boolean is1xxInformational() {
        return this.series() == HttpStatusCodeEnum.Series.INFORMATIONAL;
    }

    public boolean is2xxSuccessful() {
        return this.series() == HttpStatusCodeEnum.Series.SUCCESSFUL;
    }

    public boolean is3xxRedirection() {
        return this.series() == HttpStatusCodeEnum.Series.REDIRECTION;
    }

    public boolean is4xxClientError() {
        return this.series() == HttpStatusCodeEnum.Series.CLIENT_ERROR;
    }

    public boolean is5xxServerError() {
        return this.series() == HttpStatusCodeEnum.Series.SERVER_ERROR;
    }

    public boolean isError() {
        return this.is4xxClientError() || this.is5xxServerError();
    }

    public String toString() {
        return this.value + " " + this.name();
    }

    @Nullable
    public static HttpStatusCodeEnum resolve(int statusCode) {
        HttpStatusCodeEnum[] var1 = VALUES;
        int var2 = var1.length;

        for (HttpStatusCodeEnum status : var1) {
            if (status.value == statusCode) {
                return status;
            }
        }

        return null;
    }

    public enum Series {
        INFORMATIONAL(1),
        SUCCESSFUL(2),
        REDIRECTION(3),
        CLIENT_ERROR(4),
        SERVER_ERROR(5),
        ERROR(6),
        BAD_REQUEST(7);

        private final int value;

        Series(int value) {
            this.value = value;
        }

        public int value() {
            return this.value;
        }

        /**
         * @deprecated
         */
        @Deprecated
        public static Series valueOf(HttpStatusCodeEnum status) {
            return status.series;
        }

        public static Series valueOf(int statusCode) {
            Series series = resolve(statusCode);
            if (series == null) {
                throw new IllegalArgumentException("No matching constant for [" + statusCode + "]");
            } else {
                return series;
            }
        }

        @Nullable
        public static Series resolve(int statusCode) {
            int seriesCode = statusCode / 100;
            Series[] var2 = values();
            int var3 = var2.length;

            for (Series series : var2) {
                if (series.value == seriesCode) {
                    return series;
                }
            }

            return null;
        }
    }

    public static HttpStatusCodeEnum valueOf(int statusCode) {
        HttpStatusCodeEnum status = resolve(statusCode);
        if (status == null) {
            throw new IllegalArgumentException("No matching constant for [" + statusCode + "]");
        } else {
            return status;
        }
    }
}
