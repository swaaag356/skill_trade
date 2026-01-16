package com.itis.oris.filter;

import com.itis.oris.model.User;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(urlPatterns = {
        "/offer/create",
        "/profile",
        "/respond/offer/*"
})
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;
        User user = (User) request.getSession().getAttribute("currentUser");

        if (user == null) {
            response.sendRedirect(request.getPathInfo()+"/login");
            return;
        }

        chain.doFilter(req, resp);
    }
}