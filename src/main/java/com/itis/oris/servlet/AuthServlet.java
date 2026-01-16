package com.itis.oris.servlet;

import com.itis.oris.model.User;
import com.itis.oris.service.AuthService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet({"/login", "/register", "/logout"})
public class AuthServlet extends HttpServlet {

    private static final Logger log = LogManager.getLogger(AuthServlet.class);
    private AuthService authService;

    @Override
    public void init() {
        authService = (AuthService) getServletContext().getAttribute("authService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        req.setAttribute("contextPath", req.getContextPath());
        if ("/logout".equals(path)) {
            HttpSession session = req.getSession(false);
            if (session != null) {
                session.invalidate();
                log.info("Пользователь вышел");
            }
            resp.sendRedirect(req.getContextPath() +"/");
            return;
        }

        req.setAttribute("pageTitle", "/login".equals(path) ? "Вход" : "Регистрация");
        forward(req, resp, "/login".equals(path) ? "login.ftl" : "register.ftl");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        req.setAttribute("contextPath", req.getContextPath());
        try {
            if ("/login".equals(path)) {
                User user = authService.login(req.getParameter("username"), req.getParameter("password"));
                req.getSession().setAttribute("currentUser", user);
                log.info("Успешный вход: {}", user.getUsername());
                resp.sendRedirect(req.getContextPath() +"/offers");
            } else if ("/register".equals(path)) {
                User user = authService.register(
                        req.getParameter("username"),
                        req.getParameter("password"),
                        req.getParameter("email"),
                        req.getParameter("about")
                );
                req.getSession().setAttribute("currentUser", user);
                log.info("Успешная регистрация: {}", user.getUsername());
                resp.sendRedirect(req.getContextPath() +"/offers");
            }
        } catch (IllegalArgumentException e) {
            log.warn("Ошибка авторизации: {}", e.getMessage());
            req.setAttribute("error", e.getMessage());
            forward(req, resp, "/login".equals(path) ? "login.ftl" : "register.ftl");
        }
    }

    private void forward(HttpServletRequest req, HttpServletResponse resp, String template) throws ServletException, IOException {
        req.getRequestDispatcher("/" + template).forward(req, resp);
    }
}