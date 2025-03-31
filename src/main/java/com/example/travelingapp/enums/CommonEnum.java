package com.example.travelingapp.enums;

import lombok.Getter;

@Getter
public enum CommonEnum {

    TEST(Group.FLOW),
    REGISTER(Group.FLOW),
    FORGOT_PASSWORD(Group.FLOW),
    LOGIN(Group.FLOW),
    LOGOUT(Group.FLOW),
    COMMON(Group.FLOW),
    TOKEN(Group.FLOW),
    OTP(Group.FLOW),
    SMS(Group.FLOW),
    EMAIL(Group.FLOW),

    PASSWORD_PATTERN(Group.CONFIG),
    EMAIL_PATTERN(Group.CONFIG),
    PHONE_VN_PATTERN(Group.CONFIG),
    USERNAME_PATTERN(Group.CONFIG),
    SECRET_KEY_CONFIG(Group.CONFIG),
    MAX_ALLOWED_SESSIONS(Group.CONFIG),
    TOKEN_EXPIRATION_TIME(Group.CONFIG),
    OTP_EXPIRATION_TIME(Group.CONFIG),
    OTP_RESTRICTED_TIME(Group.CONFIG),
    CURRENT_TOKEN_TIME_LEFT(Group.CONFIG),
    NON_AUTHENTICATED_REQUEST(Group.CONFIG),

    EMAIL_ADDRESS_CONFIG(Group.CONFIG),
    EMAIL_HOST_CONFIG(Group.CONFIG),
    EMAIL_PORT_CONFIG(Group.CONFIG),
    EMAIL_CLIENT_ID(Group.CONFIG),
    EMAIL_CLIENT_SECRET(Group.CONFIG),
    EMAIL_TOKEN_URL(Group.CONFIG),
    EMAIL_ACCESS_TOKEN_CONFIG(Group.CONFIG),
    EMAIL_REFRESH_TOKEN(Group.CONFIG),
    EMAIL_REFRESH_ACCESS_TOKEN_RATE(Group.CONFIG),

    MAX_RETRY_OTP(Group.CONFIG),

    PHONE_NUM_OTP(Group.OTP_METHOD),
    EMAIL_OTP(Group.OTP_METHOD),

    AES(Group.ALGORITHM),
    RSA(Group.ALGORITHM),
    ;


    private final Group group;

    CommonEnum(Group group) {
        this.group = group;
    }

    public enum Group {
        FLOW,
        CONFIG,
        ALGORITHM,
        OTP_METHOD
    }
}
