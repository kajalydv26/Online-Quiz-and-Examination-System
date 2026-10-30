package com.quiz;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String name = request.getParameter("name");
        String email = request.getParameter("email");

        if (name == null || name.trim().isEmpty()
                || email == null || email.trim().isEmpty()) {
            response.sendError(400, "Name and email are required.");
            return;
        }

        HttpSession session = request.getSession();
        session.setAttribute("studentName", name.trim());
        session.setAttribute("studentEmail", email.trim());
        session.setAttribute("quizSubmitted", false);

        response.sendRedirect("quiz");
    }
}