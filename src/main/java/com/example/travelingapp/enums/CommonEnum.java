package com.example.travelingapp.enums;

import lombok.Getter;

@Getter
public enum CommonEnum {

    Register(Group.Flow),
    Login(Group.Flow),
    Common(Group.Flow),
    Token(Group.Flow),

    PASSWORD_PATTERN(Group.Config),
    EMAIL_PATTERN(Group.Config),
    PHONE_VN_PATTERN(Group.Config),
    USERNAME_PATTERN(Group.Config),

    EXPIRATION_TIME(Group.Config),

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
