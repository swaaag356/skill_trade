package com.itis.oris.filter;

import com.itis.oris.model.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(urlPatterns = {
        "/offer/create",
        "/profile",
        "/respond/offer/*"
})
public class AuthFilter implements Filter {

    private static final Logger log = LogManager.getLogger(AuthFilter.class);

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;
        User user = (User) request.getSession().getAttribute("currentUser");

        if (user == null) {
            log.warn("Неавторизованный доступ к {} от IP {}", request.getRequestURI(), request.getRemoteAddr());
            response.sendRedirect(request.getPathInfo()+"/login");
            return;
        }

        log.debug("Доступ разрешён: {} → {}", user.getUsername(), request.getRequestURI());
        chain.doFilter(req, resp);
    }
}