package com.homework.AuthService.settings;

import org.springframework.stereotype.Component;

public class Constants {
    public static final int accessTokenSeconds = 15 * 60;
    public static final int refreshTokenSeconds = 60 * 60;

    public static final String ADMIN_ROLE_TITLE = "ROLE_ADMIN";
    public static final String USER_ROLE_TITLE = "ROLE_USER";
    public static final String PREMIUM_USER_ROLE_TITLE = "ROLE_PREMIUM_USER";
}
