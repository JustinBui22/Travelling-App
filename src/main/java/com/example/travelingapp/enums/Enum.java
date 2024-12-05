package com.example.travelingapp.enums;

import lombok.Getter;

@Getter
public enum Enum {

    Register(Group.Flow),
    Login(Group.Flow),

    PASSWORD_PATTERN(Group.Config),
    EMAIL_PATTERN(Group.Config),
    PHONE_VN_PATTERN(Group.Config);

    private final Group group;

    Enum(Group group) {
        this.group = group;
    }

    public enum Group {
        Flow,
        Config,
        C
    }
}
