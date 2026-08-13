package com.jagadeesh.jagadeeshcart.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/** Enforces that a valid session (userId set) exists before reaching protected servlets. */
@WebFilter({"/dashboard", "/api/v1/products/*"})
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        HttpSession session = request.getSession(false);
        boolean methodRequiresAuth = !"GET".equalsIgnoreCase(request.getMethod());

        if (session == null || session.getAttribute("userId") == null) {
            if (methodRequiresAuth) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write(
                        "{\"success\":false,\"data\":null,\"error\":{\"code\":\"UNAUTHENTICATED\",\"message\":\"Login required\"}}");
                return;
            }
        }
        chain.doFilter(req, res);
    }
}
