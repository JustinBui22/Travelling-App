package com.example.travelingapp.enums;

import lombok.Getter;

@Getter
public enum CommonEnum {

    Test(Group.Flow),
    Register(Group.Flow),
    Login(Group.Flow),
    Common(Group.Flow),
    Token(Group.Flow),

    PASSWORD_PATTERN(Group.Config),
    EMAIL_PATTERN(Group.Config),
    PHONE_VN_PATTERN(Group.Config),
    USERNAME_PATTERN(Group.Config),
    SECRET_KEY_CONFIG(Group.Config),
    MAX_ALLOWED_SESSIONS(Group.Config),

    TOKEN_EXPIRATION_TIME(Group.Config),
    CURRENT_TOKEN_TIME_LEFT(Group.Config),
    NON_AUTHENTICATED_REQUEST(Group.Config),

    AES(Group.Algorithm),
    RSA(Group.Algorithm),
    ;


    private final Group group;

    CommonEnum(Group group) {
        this.group = group;
    }

    public enum Group {
        Flow,
        Config,
        Algorithm
    }
}
