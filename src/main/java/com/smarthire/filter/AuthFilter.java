package com.smarthire.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Set;

/**
 * Protects URLs that require authentication.
 * Public paths (login, register, home, static assets) are allowed through.
 * Student and Recruiter paths are checked for the correct role.
 */
@WebFilter("/*")
public class AuthFilter implements Filter {

    private static final Set<String> PUBLIC_PATHS = Set.of(
        "", "/", "/home", "/login", "/register", "/logout", "/index.jsp"
    );

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;
        String path = request.getServletPath();

        // Allow public paths
        if (PUBLIC_PATHS.contains(path) || path.startsWith("/css") || path.startsWith("/js") || path.startsWith("/images")) {
            chain.doFilter(req, resp);
            return;
        }

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String role = (String) session.getAttribute("role");

        // Protect student paths
        if (path.startsWith("/student") && !"STUDENT".equals(role)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // Protect recruiter paths
        if (path.startsWith("/recruiter") && !"RECRUITER".equals(role)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        chain.doFilter(req, resp);
    }
}
