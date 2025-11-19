package com.itis.oris.servlet;

import com.itis.oris.model.TradeOffer;
import com.itis.oris.model.User;
import com.itis.oris.service.TradeOfferService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet({"/offers", "/offer/create", "/offer/*"})
public class OfferServlet extends HttpServlet {

    private static final Logger log = LogManager.getLogger(OfferServlet.class);
    private TradeOfferService tradeOfferService;

    @Override
    public void init() {
        tradeOfferService = (TradeOfferService) getServletContext().getAttribute("tradeOfferService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath()+(req.getPathInfo()==null?"":req.getPathInfo());
        req.setAttribute("contextPath", req.getContextPath());
        User user = (User) req.getSession().getAttribute("currentUser");

        if ("/offers".equals(path)) {
            req.setAttribute("pageTitle", "Предложения");
            req.setAttribute("offers", tradeOfferService.getActiveOffers());
            forward(req, resp, "offers.ftl");
        } else if ("/offer/create".equals(path)) {
            if (user == null) {
                resp.sendRedirect(req.getContextPath() +"/login");
                return;
            }
            req.setAttribute("pageTitle", "Новое предложение");
            forward(req, resp, "offer-create.ftl");
        } else if (path.startsWith("/offer/") && path.matches( "/offer/\\d+")) {
            log.info("ЧТО ТО НЕ ТАК");
            Integer id = Integer.parseInt(path.substring("/offer/".length()));
            log.info("ID: " + id.toString());
            TradeOffer offer = tradeOfferService.findById(id);
            if (offer == null) {
                resp.sendError(404, "Предложение не найдено");
                return;
            }
            req.setAttribute("offer", offer);
            req.setAttribute("pageTitle", "Предложение #" + id);
            forward(req, resp, "offer-detail.ftl");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!"/offer/create".equals(req.getServletPath())) return;
        req.setAttribute("contextPath", req.getContextPath());

        User user = (User) req.getSession().getAttribute("currentUser");
        if (user == null) {
            resp.sendRedirect(req.getContextPath()+"/login");
            return;
        }

        try {
            TradeOffer offer = tradeOfferService.create(
                    user,
                    req.getParameter("offerSkill"),
                    req.getParameter("requestSkill"),
                    req.getParameter("description")
            );
            log.info("Создано предложение: {}", offer.getId());
            resp.sendRedirect(req.getContextPath() +"/offer/" + offer.getId());
        } catch (IllegalArgumentException e) {
            log.warn("Ошибка создания: {}", e.getMessage());
            req.setAttribute("error", e.getMessage());
            forward(req, resp, "offer-create.ftl");
        }
    }

    private void forward(HttpServletRequest req, HttpServletResponse resp, String template) throws ServletException, IOException {
        req.getRequestDispatcher("/" + template).forward(req, resp);
    }
}