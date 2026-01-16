package com.itis.oris.servlet;

import com.itis.oris.service.DataService;
import com.itis.oris.service.TradeOfferService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/")
public class HomeServlet extends HttpServlet {

    private static final Logger log = LogManager.getLogger(HomeServlet.class);
    private TradeOfferService tradeOfferService;
    private DataService dataService;

    @Override
    public void init() {
        tradeOfferService = (TradeOfferService) getServletContext().getAttribute("tradeOfferService");
        dataService = (DataService) getServletContext().getAttribute("dataService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("pageTitle", "Главная");
        req.setAttribute("offers", tradeOfferService.getActiveOffers());
        req.setAttribute("contextPath", req.getContextPath());
        dataService.getAttributes(req);
        forward(req, resp, "home.ftl");
    }

    private void forward(HttpServletRequest req, HttpServletResponse resp, String template) throws ServletException, IOException {
        req.getRequestDispatcher("/" + template).forward(req, resp);
    }
}