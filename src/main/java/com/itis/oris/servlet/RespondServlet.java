package com.itis.oris.servlet;

import com.itis.oris.model.User;
import com.itis.oris.service.TradeOfferService;
import com.itis.oris.service.TradeResponseService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/respond/offer/*")
public class RespondServlet extends HttpServlet {

    private static final Logger log = LogManager.getLogger(RespondServlet.class);
    private TradeResponseService responseService;
    private TradeOfferService offerService;

    @Override
    public void init() {
        responseService = (TradeResponseService) getServletContext().getAttribute("tradeResponseService");
        offerService = (TradeOfferService) getServletContext().getAttribute("tradeOfferService");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("currentUser");
        req.setAttribute("contextPath", req.getContextPath());
        if (user == null) {
            resp.sendRedirect(req.getContextPath()+"/login");
            return;
        }

        String path = req.getPathInfo();
        if (path == null || path.length() < 2) {
            resp.sendError(400, "Invalid offer ID");
            return;
        }

        Integer offerId = Integer.parseInt(path.substring(1));

        try {
            responseService.respond(offerId, user, req.getParameter("message"));
            log.info("Отклик на {} от {}", offerId, user.getUsername());
            resp.sendRedirect(req.getContextPath() +"/offer/" + offerId);
        } catch (IllegalArgumentException e) {
            req.setAttribute("error", e.getMessage());
            req.setAttribute("offer", offerService.findById(offerId));
            forward(req, resp, "offer-detail.ftl");
        }
    }

    private void forward(HttpServletRequest req, HttpServletResponse resp, String template) throws ServletException, IOException {
        req.getRequestDispatcher("/" + template).forward(req, resp);
    }
}