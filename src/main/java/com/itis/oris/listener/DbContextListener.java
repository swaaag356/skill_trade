package com.itis.oris.listener;

import com.itis.oris.repository.*;
import com.itis.oris.service.*;
import com.itis.oris.util.DbConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.sql.Connection;
import java.sql.SQLException;

@WebListener
public class DbContextListener implements ServletContextListener {

    private static final Logger log = LogManager.getLogger(DbContextListener.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        log.info("Инициализация SkillTrade: подключение к БД...");

        try {
            Connection conn = DbConnection.getConnection();

            DataRepository dataRepository = new DataRepository();
            UserRepository userRepository = new UserRepository();
            SkillRepository skillRepository = new SkillRepository();
            TradeResponseRepository tradeResponseRepository = new TradeResponseRepository();
            TradeOfferRepository tradeOfferRepository = new TradeOfferRepository(tradeResponseRepository);
            ReviewRepository reviewRepository = new ReviewRepository();

            DataService dataService = new DataService(dataRepository);
            UserService userService = new UserService(userRepository, conn);
            SkillService skillService = new SkillService(skillRepository, conn);
            TradeOfferService tradeOfferService = new TradeOfferService(tradeOfferRepository, skillRepository, tradeResponseRepository, conn);
            TradeResponseService tradeResponseService = new TradeResponseService(tradeResponseRepository, tradeOfferRepository, conn);
            ReviewService reviewService = new ReviewService(reviewRepository, userRepository, conn);
            AuthService authService = new AuthService(userService);

            ServletContext context = sce.getServletContext();
            context.setAttribute("userRepository", userRepository);
            context.setAttribute("skillRepository", skillRepository);
            context.setAttribute("tradeOfferRepository", tradeOfferRepository);
            context.setAttribute("tradeResponseRepository", tradeResponseRepository);
            context.setAttribute("reviewRepository", reviewRepository);
            context.setAttribute("dataService", dataService);

            context.setAttribute("userService", userService);
            context.setAttribute("skillService", skillService);
            context.setAttribute("tradeOfferService", tradeOfferService);
            context.setAttribute("tradeResponseService", tradeResponseService);
            context.setAttribute("reviewService", reviewService);
            context.setAttribute("authService", authService);

            context.setAttribute("appConnection", conn);

            log.info("SkillTrade успешно инициализирован: репозитории и сервисы добавлены в контекст");
        } catch (SQLException e) {
            log.error("Критическая ошибка при инициализации БД", e);
            throw new RuntimeException("Не удалось подключиться к базе данных", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        log.info("Остановка SkillTrade: закрытие ресурсов...");

        Connection conn = (Connection) sce.getServletContext().getAttribute("appConnection");
        if (conn != null) {
            try {
                if (!conn.isClosed()) {
                    conn.close();
                    log.info("Соединение с БД закрыто");
                }
            } catch (SQLException e) {
                log.error("Ошибка при закрытии соединения", e);
            }
        }

        DbConnection.destroy();
        log.info("HikariCP пул закрыт");

        log.info("SkillTrade остановлен");
    }
}