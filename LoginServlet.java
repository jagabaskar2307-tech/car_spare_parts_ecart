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
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/api/v1/auth/login")
public class LoginServlet extends HttpServlet {

    private UserService userService;

    @Override
    public void init() {
        userService = new UserService(DAOFactory.userDAO());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");

        String email = req.getParameter("email");
        String password = req.getParameter("password");

        try {
            User user = userService.login(email, password);

            // Invalidate any pre-existing session, then start a fresh one (session fixation protection).
            HttpSession oldSession = req.getSession(false);
            if (oldSession != null) {
                oldSession.invalidate();
            }
            HttpSession session = req.getSession(true);
            session.setAttribute("userId", user.getId());
            session.setAttribute("role", user.getRole().name());
            session.setMaxInactiveInterval(30 * 60);

            resp.getWriter().write(JsonUtil.toJson(ApiResponse.ok(UserResponseDTO.fromEntity(user))));
        } catch (ValidationException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(JsonUtil.toJson(ApiResponse.fail("VALIDATION_ERROR", e.getMessage())));
        } catch (AuthException e) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write(JsonUtil.toJson(ApiResponse.fail("AUTH_ERROR", e.getMessage())));
        }
    }
}
