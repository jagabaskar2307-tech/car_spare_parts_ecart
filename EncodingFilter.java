package com.jagadeesh.jagadeeshcart.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;

import java.io.IOException;
import java.util.UUID;

@WebFilter("/*")
public class EncodingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        org.slf4j.MDC.put("requestId", UUID.randomUUID().toString().substring(0, 8));
        try {
            chain.doFilter(request, response);
        } finally {
            org.slf4j.MDC.remove("requestId");
        }
    }
}
