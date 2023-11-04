package com.example.web_bookstore_be.security;

public class Endpoints {

    public static final String font_end_host = "http://localhost:3000";
    public static final String[] PUBLIC_GET = {
            "/books",
            "/books/**",
            "/genre/**",
            "/images/**",
            "/reviews/**",
            "/users/search/existsByUsername/**",
            "/users/search/existsByEmail/**",
            "/user/active-account/**",
    };

    public static final String[] PUBLIC_POST = {
            "/user/register",
            "/user/authenticate",
    };

    public static final String[] ADMIN_ENDPOINT = {
            "/users",
            "/users/**",
            "/cart-items/**",
    };
}
