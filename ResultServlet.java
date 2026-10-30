package com.quiz;

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/result")
public class ResultServlet extends HttpServlet {

    // Correct answers: A = 0, B = 1, C = 2, D = 3
    private final int[] correctAnswers = {
        1, 2, 2,
        1, 3, 2,
        2, 1, 0,
        1, 1, 2,
        0, 1, 2,
        2, 2, 1,
        1, 2
    };

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        // Check whether the student is logged in.
        if (session == null ||
            session.getAttribute("studentName") == null) {
            response.sendRedirect("login.html");
            return;
        }

        // Prevent submitting the same quiz more than once.
        if (Boolean.TRUE.equals(session.getAttribute("quizSubmitted"))) {
            response.sendRedirect("result");
            return;
        }

        int score = 0;

        // Calculate the score out of 20.
        for (int i = 0; i < correctAnswers.length; i++) {

            String answer = request.getParameter("q" + i);

            // Unanswered questions count as incorrect.
            if (answer == null || answer.trim().isEmpty()) {
                continue;
            }

            try {
                int selectedAnswer = Integer.parseInt(answer);

                // Only option indexes 0, 1, 2 and 3 are valid.
                if (selectedAnswer < 0 || selectedAnswer > 3) {
                    response.sendError(
                        HttpServletResponse.SC_BAD_REQUEST,
                        "Invalid answer submitted."
                    );
                    return;
                }

                if (selectedAnswer == correctAnswers[i]) {
                    score++;
                }

            } catch (NumberFormatException e) {
                response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Invalid answer submitted."
                );
                return;
            }
        }

        // Store the result in the student's session.
        session.setAttribute("score", score);
        session.setAttribute("quizSubmitted", true);

        response.sendRedirect("result");
    }

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null ||
            session.getAttribute("studentName") == null) {
            response.sendRedirect("login.html");
            return;
        }

        if (!Boolean.TRUE.equals(session.getAttribute("quizSubmitted"))) {
            response.sendRedirect("quiz");
            return;
        }

        response.setContentType("text/html;charset=UTF-8");

        PrintWriter out = response.getWriter();

        String name = escapeHtml(
            (String) session.getAttribute("studentName")
        );

        int score = (Integer) session.getAttribute("score");
        int total = correctAnswers.length;
        int incorrect = total - score;
        int percentage = score * 100 / total;

        String heading;
        String message;

        if (percentage >= 80) {
            heading = "Excellent Work!";
            message = "Outstanding performance! Keep up the great work.";
        } else if (percentage >= 60) {
            heading = "Good Job!";
            message = "You have done well. Keep practising to improve further.";
        } else if (percentage >= 40) {
            heading = "Keep Improving!";
            message = "Good effort. Review the topics and try again.";
        } else {
            heading = "Keep Learning!";
            message = "Every attempt is a learning opportunity. Revise the topics and try again.";
        }

        out.println("<!DOCTYPE html>");
        out.println("<html lang='en'>");
        out.println("<head>");
        out.println("<meta charset='UTF-8'>");
        out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        out.println("<title>Quiz Result</title>");

        out.println("<style>");
        out.println("* { box-sizing: border-box; }");

        out.println("body {");
        out.println("margin: 0;");
        out.println("min-height: 100vh;");
        out.println("font-family: Arial, Helvetica, sans-serif;");
        out.println("background: #f3f6fc;");
        out.println("color: #25324b;");
        out.println("display: flex;");
        out.println("justify-content: center;");
        out.println("align-items: center;");
        out.println("padding: 30px 16px;");
        out.println("}");

        out.println(".result-card {");
        out.println("width: 100%;");
        out.println("max-width: 540px;");
        out.println("background: #ffffff;");
        out.println("border-radius: 18px;");
        out.println("padding: 35px 30px;");
        out.println("text-align: center;");
        out.println("box-shadow: 0 10px 35px rgba(32,55,100,0.10);");
        out.println("}");

        out.println(".badge {");
        out.println("display: inline-block;");
        out.println("background: #eaf2ff;");
        out.println("color: #2459d3;");
        out.println("padding: 8px 15px;");
        out.println("border-radius: 20px;");
        out.println("font-size: 13px;");
        out.println("font-weight: bold;");
        out.println("margin-bottom: 14px;");
        out.println("}");

        out.println("h1 {");
        out.println("font-size: 28px;");
        out.println("margin: 0 0 10px;");
        out.println("color: #1e2b45;");
        out.println("}");

        out.println(".welcome {");
        out.println("color: #6b778c;");
        out.println("font-size: 15px;");
        out.println("margin: 0 0 22px;");
        out.println("line-height: 1.6;");
        out.println("}");

        out.println(".score-circle {");
        out.println("width: 150px;");
        out.println("height: 150px;");
        out.println("border-radius: 50%;");
        out.println("margin: 0 auto 22px;");
        out.println("background: #edf3ff;");
        out.println("border: 8px solid #3268e8;");
        out.println("display: flex;");
        out.println("flex-direction: column;");
        out.println("align-items: center;");
        out.println("justify-content: center;");
        out.println("}");

        out.println(".score-number {");
        out.println("font-size: 34px;");
        out.println("font-weight: bold;");
        out.println("color: #2459d3;");
        out.println("}");

        out.println(".score-label {");
        out.println("font-size: 13px;");
        out.println("color: #6b778c;");
        out.println("margin-top: 4px;");
        out.println("}");

        out.println(".stats {");
        out.println("display: grid;");
        out.println("grid-template-columns: repeat(3, 1fr);");
        out.println("gap: 12px;");
        out.println("margin: 24px 0;");
        out.println("}");

        out.println(".stat {");
        out.println("padding: 16px 8px;");
        out.println("background: #f7f9fd;");
        out.println("border: 1px solid #edf0f6;");
        out.println("border-radius: 12px;");
        out.println("}");

        out.println(".stat-number {");
        out.println("font-size: 24px;");
        out.println("font-weight: bold;");
        out.println("margin-bottom: 6px;");
        out.println("}");

        out.println(".stat-label {");
        out.println("font-size: 12px;");
        out.println("color: #6b778c;");
        out.println("}");

        out.println(".correct { color: #168354; }");
        out.println(".incorrect { color: #d64545; }");
        out.println(".total { color: #2459d3; }");

        out.println(".feedback {");
        out.println("background: #f7f9fd;");
        out.println("border-radius: 12px;");
        out.println("padding: 18px;");
        out.println("margin: 20px 0 24px;");
        out.println("overflow-wrap: anywhere;");
        out.println("}");

        out.println(".feedback h2 {");
        out.println("font-size: 18px;");
        out.println("margin: 0 0 8px;");
        out.println("color: #25324b;");
        out.println("}");

        out.println(".feedback p {");
        out.println("font-size: 14px;");
        out.println("line-height: 1.7;");
        out.println("color: #667085;");
        out.println("margin: 0;");
        out.println("}");

        out.println(".buttons {");
        out.println("display: flex;");
        out.println("justify-content: center;");
        out.println("gap: 12px;");
        out.println("flex-wrap: wrap;");
        out.println("}");

        out.println(".btn {");
        out.println("display: inline-block;");
        out.println("padding: 12px 22px;");
        out.println("min-width: 125px;");
        out.println("border-radius: 9px;");
        out.println("text-decoration: none;");
        out.println("font-size: 14px;");
        out.println("font-weight: bold;");
        out.println("text-align: center;");
        out.println("transition: background 0.2s ease;");
        out.println("}");

        out.println(".btn-primary {");
        out.println("background: #2f63e9;");
        out.println("color: white;");
        out.println("}");

        out.println(".btn-primary:hover { background: #204fce; }");

        out.println(".btn-secondary {");
        out.println("background: #edf2fb;");
        out.println("color: #2459d3;");
        out.println("}");

        out.println(".btn-secondary:hover { background: #dfe8fa; }");

        out.println("@media (max-width: 480px) {");
        out.println(".result-card { padding: 28px 18px; }");
        out.println("h1 { font-size: 24px; }");
        out.println(".stats { gap: 7px; }");
        out.println(".stat { padding: 13px 4px; }");
        out.println(".stat-number { font-size: 21px; }");
        out.println(".stat-label { font-size: 11px; }");
        out.println(".btn { min-width: 115px; padding: 12px 15px; }");
        out.println("}");

        out.println("</style>");
        out.println("</head>");

        out.println("<body>");
        out.println("<main class='result-card'>");

        out.println("<div class='badge'>QUIZ COMPLETED</div>");
        out.println("<h1>" + heading + "</h1>");

        out.println("<p class='welcome'>Well done, " + name
            + "! Here is your quiz performance.</p>");

        out.println("<div class='score-circle'>");
        out.println("<div class='score-number'>" + percentage + "%</div>");
        out.println("<div class='score-label'>" + score + " out of " + total + "</div>");
        out.println("</div>");

        out.println("<div class='stats'>");

        out.println("<div class='stat'>");
        out.println("<div class='stat-number total'>" + total + "</div>");
        out.println("<div class='stat-label'>Total Questions</div>");
        out.println("</div>");

        out.println("<div class='stat'>");
        out.println("<div class='stat-number correct'>" + score + "</div>");
        out.println("<div class='stat-label'>Correct Answers</div>");
        out.println("</div>");

        out.println("<div class='stat'>");
        out.println("<div class='stat-number incorrect'>" + incorrect + "</div>");
        out.println("<div class='stat-label'>Incorrect Answers</div>");
        out.println("</div>");

        out.println("</div>");

        out.println("<div class='feedback'>");
        out.println("<h2>" + heading + "</h2>");
        out.println("<p>" + message + "</p>");
        out.println("</div>");

        out.println("<div class='buttons'>");
        out.println("<a class='btn btn-primary' href='logout'>Logout</a>");
        out.println("<a class='btn btn-secondary' href='index.html'>Home Page</a>");
        out.println("</div>");

        out.println("</main>");
        out.println("</body>");
        out.println("</html>");
    }

    private String escapeHtml(String text) {
        if (text == null) {
            return "";
        }

        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#39;");
    }
}

