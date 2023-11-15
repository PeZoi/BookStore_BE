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
            "/cart-items/**",
            "/users/*/listCartItems",

    };

    public static final String[] PUBLIC_POST = {
            "/user/register",
            "/user/authenticate",
            "/cart-item/add-item",
    };

    public static final String[] PUBLIC_PUT = {
            "/cart-item/**",
            "/cart-items/**",
    };

    public static final String[] PUBLIC_DELETE = {
            "/cart-items/**",
    };

    public static final String[] ADMIN_ENDPOINT = {
            "/users",
            "/users/**",
            "/cart-items/**",
            "/books",
            "/books/**",
            "/book/add-book/**",
            "/user/add-user/**",
            "/feedbacks/**",
            "/cart-items/**",
            "/cart-item/**",
            "/**",
    };
}