package com.itis.oris.servlet;

import com.itis.oris.model.TradeOffer;
import com.itis.oris.model.TradeResponse;
import com.itis.oris.model.User;
import com.itis.oris.model.enums.Status;
import com.itis.oris.service.TradeOfferService;
import com.itis.oris.service.TradeResponseService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/my-responses")
public class MyResponsesServlet extends HttpServlet {

    private static final Logger log = LogManager.getLogger(MyResponsesServlet.class);
    private TradeOfferService offerService;
    private TradeResponseService responseService;  // ← ДОБАВИЛ

    @Override
    public void init() {
        offerService = (TradeOfferService) getServletContext().getAttribute("tradeOfferService");
        responseService = (TradeResponseService) getServletContext().getAttribute("tradeResponseService");  // ← ДОБАВИЛ
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("currentUser");
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        List<TradeOffer> inProgressOffers = offerService.getInProgressOffers();  // ← только IN_PROGRESS

        List<TradeOffer> myRespondedOffers = inProgressOffers.stream()
                .filter(offer -> responseService.findByOfferId(offer.getId()).stream()
                        .anyMatch(r -> r.getResponder().getId().equals(user.getId())))
                .toList();

        req.setAttribute("offers", myRespondedOffers);
        req.setAttribute("pageTitle", "Мои отклики");
        req.setAttribute("contextPath", req.getContextPath());
        req.getRequestDispatcher("/my-responses.ftl").forward(req, resp);
    }
}