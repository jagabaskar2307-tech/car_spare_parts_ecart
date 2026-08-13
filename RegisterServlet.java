package com.jagadeesh.jagadeeshcart.controller;

import com.jagadeesh.jagadeeshcart.dao.DAOFactory;
import com.jagadeesh.jagadeeshcart.dto.ApiResponse;
import com.jagadeesh.jagadeeshcart.dto.UserResponseDTO;
import com.jagadeesh.jagadeeshcart.exception.AuthException;
import com.jagadeesh.jagadeeshcart.exception.ValidationException;
import com.jagadeesh.jagadeeshcart.model.User;
import com.jagadeesh.jagadeeshcart.service.UserService;
import com.jagadeesh.jagadeeshcart.util.JsonUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/api/v1/auth/register")
public class RegisterServlet extends HttpServlet {

    private UserService userService;

    @Override
    public void init() {
        userService = new UserService(DAOFactory.userDAO());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/jsp/register.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");

        String name = req.getParameter("name");
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        String role = req.getParameter("role");

        try {
            User user = userService.register(name, email, password, role);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write(JsonUtil.toJson(ApiResponse.ok(UserResponseDTO.fromEntity(user))));
        } catch (ValidationException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(JsonUtil.toJson(ApiResponse.fail("VALIDATION_ERROR", e.getMessage())));
        } catch (AuthException e) {
            resp.setStatus(HttpServletResponse.SC_CONFLICT);
            resp.getWriter().write(JsonUtil.toJson(ApiResponse.fail("CONFLICT", e.getMessage())));
        }
    }
}
