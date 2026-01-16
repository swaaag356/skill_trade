package com.itis.oris.servlet;

import com.itis.oris.model.User;
import com.itis.oris.service.TradeOfferService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/complete/offer/*")
public class CompleteOfferServlet extends HttpServlet {

    private static final Logger log = LogManager.getLogger(CompleteOfferServlet.class);
    private TradeOfferService offerService;

    @Override
    public void init() {
        offerService = (TradeOfferService) getServletContext().getAttribute("tradeOfferService");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("currentUser");
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String path = req.getPathInfo();
        if (path == null || path.length() < 2) {
            resp.sendError(400);
            return;
        }

        Integer offerId = Integer.parseInt(path.substring(1));

        try {
            offerService.completeOffer(offerId, user);
            resp.sendRedirect(req.getContextPath() + "/offer/" + offerId);
        } catch (IllegalArgumentException e) {
            req.setAttribute("error", e.getMessage());
            req.setAttribute("offer", offerService.findById(offerId));
            req.getRequestDispatcher("/offer-detail.ftl").forward(req, resp);
        }
    }
}