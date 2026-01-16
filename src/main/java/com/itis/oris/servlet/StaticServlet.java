package com.itis.oris.servlet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@WebServlet("/static/*")
public class StaticServlet extends HttpServlet {

    private static final Logger log = LogManager.getLogger(StaticServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo();
        req.setAttribute("contextPath", req.getContextPath());
        if (path == null || path.equals("/")) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String resourcePath = "static" + path;
        log.info("Requested resource: " + resourcePath);

        InputStream in = getClass().getClassLoader().getResourceAsStream(resourcePath);
        if (in == null) {
            log.warn("Resource not found: " + resourcePath);
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String contentType = getServletContext().getMimeType(path);
        if (contentType == null) {
            contentType = "application/octet-stream";
        }
        resp.setContentType(contentType);

        in.transferTo(resp.getOutputStream());

//        try (in; OutputStream out = resp.getOutputStream()) {
//            byte[] buffer = new byte[8192];
//            int bytesRead;
//            while ((bytesRead = in.read(buffer)) != -1) {
//                out.write(buffer, 0, bytesRead);
//            }
//        }
    }
}

