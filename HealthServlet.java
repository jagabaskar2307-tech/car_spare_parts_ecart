package com.jagadeesh.jagadeeshcart.controller;

import com.jagadeesh.jagadeeshcart.listener.DataSourceListener;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;

@WebServlet("/api/v1/health")
public class HealthServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        String dbStatus = "DOWN";
        try (Connection conn = DataSourceListener.getDataSource().getConnection()) {
            if (conn.isValid(2)) {
                dbStatus = "UP";
            }
        } catch (Exception e) {
            // dbStatus stays DOWN
        }
        resp.getWriter().write(String.format("{\"status\":\"UP\",\"db\":\"%s\"}", dbStatus));
    }
}
