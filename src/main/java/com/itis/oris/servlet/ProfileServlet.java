package com.itis.oris.servlet;

import com.itis.oris.model.User;
import com.itis.oris.service.TradeOfferService;
import com.itis.oris.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/profile")
public class ProfileServlet extends HttpServlet {

    private static final Logger log = LogManager.getLogger(ProfileServlet.class);
    private UserService userService;
    private TradeOfferService offerService;

    @Override
    public void init() {
        userService = (UserService) getServletContext().getAttribute("userService");
        offerService = (TradeOfferService) getServletContext().getAttribute("tradeOfferService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User current = (User) req.getSession().getAttribute("currentUser");
        req.setAttribute("contextPath", req.getContextPath());
        if (current == null) {
            resp.sendRedirect(req.getContextPath() +"/login");
            return;
        }

        String idParam = req.getParameter("id");
        User user = idParam != null ? offerService.findById(Integer.parseInt(idParam)).getUser() : current;

        req.setAttribute("user", user);
        req.setAttribute("userOffers", offerService.getOffersByUserId(user.getId()));
        req.setAttribute("pageTitle", "Профиль: " + user.getUsername());
        forward(req, resp, "profile.ftl");
    }

    private void forward(HttpServletRequest req, HttpServletResponse resp, String template) throws ServletException, IOException {
        req.getRequestDispatcher("/" + template).forward(req, resp);
    }
}