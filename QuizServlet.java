package com.quiz;

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/quiz")
public class QuizServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final String[] questions = {
        "Which keyword is used to create a class in Java?",
        "Which method is the entry point of a Java application?",
        "Which keyword is used to inherit a class in Java?",

        "Which HTML tag is used to create a hyperlink?",
        "Which HTML tag is used to display an image?",
        "Which tag is used for the largest heading in HTML?",

        "Which CSS property changes the text color?",
        "Which CSS property adds space inside an element's border?",
        "Which CSS layout system is useful for arranging items in rows or columns?",

        "What is the main purpose of a Java Servlet?",
        "Which method normally handles HTTP GET requests in a servlet?",
        "Which object is commonly used to store information for a user's session?",

        "What does DBMS stand for?",
        "Which SQL command retrieves data from a table?",
        "Which key uniquely identifies each record in a table?",

        "Which keyword is used to define a function in Python?",
        "Which Python data type stores key-value pairs?",
        "Which symbol is used for single-line comments in Python?",

        "Which type of machine learning uses labelled training data?",
        "Which field of AI enables computers to understand and process human language?"
    };

    private final String[][] options = {
        {"object", "class", "method", "package"},
        {"start()", "run()", "main()", "init()"},
        {"this", "implements", "extends", "static"},

        {"<link>", "<a>", "<href>", "<p>"},
        {"<image>", "<picture>", "<src>", "<img>"},
        {"<h6>", "<head>", "<h1>", "<heading>"},

        {"font-style", "text-color", "color", "background"},
        {"margin", "padding", "spacing", "border"},
        {"Flexbox", "Frames", "Tables only", "Inline text"},

        {"To style web pages", "To handle web requests",
         "To store files", "To design databases"},
        {"doPost()", "doGet()", "doDelete()", "doConnect()"},
        {"PrintWriter", "ServletConfig", "HttpSession",
         "ServletOutputStream"},

        {"Database Management System",
         "Data Backup Management Software",
         "Digital Base Machine System",
         "Database Monitoring Service"},
        {"UPDATE", "SELECT", "INSERT", "DELETE"},
        {"Foreign key", "Duplicate key", "Primary key",
         "Secondary table"},

        {"function", "define", "def", "fun"},
        {"List", "Tuple", "Dictionary", "String"},
        {"//", "#", "<!-- -->", "/* */"},

        {"Unsupervised learning", "Supervised learning",
         "Reinforcement learning", "Random learning"},
        {"Computer graphics", "Operating systems",
         "Natural Language Processing", "Computer networking"}
    };

    // Correct option indexes: A = 0, B = 1, C = 2, D = 3
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
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null ||
            session.getAttribute("studentName") == null) {

            response.sendRedirect(
                request.getContextPath() + "/login.html"
            );
            return;
        }

        if (Boolean.TRUE.equals(session.getAttribute("quizSubmitted"))) {
            response.sendRedirect(request.getContextPath() + "/result");
            return;
        }

        response.setContentType("text/html;charset=UTF-8");

        PrintWriter out = response.getWriter();

        String studentName = escapeHtml(
            String.valueOf(session.getAttribute("studentName"))
        );

        out.println("<!DOCTYPE html>");
        out.println("<html lang='en'>");
        out.println("<head>");
        out.println("<meta charset='UTF-8'>");
        out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        out.println("<title>Online Quiz Assessment</title>");

        out.println("<style>");

        out.println("* { box-sizing: border-box; }");

        out.println("html { scroll-behavior: smooth; }");

        out.println("body {");
        out.println("margin: 0;");
        out.println("font-family: Arial, sans-serif;");
        out.println("background: #f3f6fc;");
        out.println("color: #152b55;");
        out.println("}");

        /* Top navigation */
        out.println(".topbar {");
        out.println("background: white;");
        out.println("padding: 16px 6%;");
        out.println("display: flex;");
        out.println("justify-content: space-between;");
        out.println("align-items: center;");
        out.println("gap: 12px;");
        out.println("box-shadow: 0 2px 10px rgba(0,0,0,0.05);");
        out.println("}");

        out.println(".brand {");
        out.println("font-size: 21px;");
        out.println("font-weight: bold;");
        out.println("color: #2864f0;");
        out.println("}");

        /* Sticky timer and progress area */
        out.println(".quiz-header {");
        out.println("position: sticky;");
        out.println("top: 0;");
        out.println("z-index: 1000;");
        out.println("background: #ffffff;");
        out.println("padding: 14px 22px;");
        out.println("border-radius: 0 0 14px 14px;");
        out.println("margin-bottom: 22px;");
        out.println("box-shadow: 0 5px 20px rgba(20,40,80,0.12);");
        out.println("border-bottom: 1px solid #e1e8f5;");
        out.println("}");

        out.println(".quiz-header h2 {");
        out.println("font-size: 21px;");
        out.println("margin: 0 0 7px;");
        out.println("}");

        out.println(".quiz-info {");
        out.println("font-size: 14px;");
        out.println("color: #64748b;");
        out.println("margin: 0 0 10px;");
        out.println("}");

        out.println(".timer-row {");
        out.println("display: flex;");
        out.println("justify-content: space-between;");
        out.println("align-items: center;");
        out.println("gap: 10px;");
        out.println("flex-wrap: wrap;");
        out.println("}");

        out.println(".timer {");
        out.println("font-size: 20px;");
        out.println("font-weight: bold;");
        out.println("color: #2864f0;");
        out.println("}");

        out.println(".progress-text {");
        out.println("font-size: 14px;");
        out.println("color: #475569;");
        out.println("}");

        out.println(".progress-track {");
        out.println("height: 7px;");
        out.println("background: #e4eaf7;");
        out.println("border-radius: 10px;");
        out.println("margin-top: 11px;");
        out.println("overflow: hidden;");
        out.println("}");

        out.println(".progress-fill {");
        out.println("height: 100%;");
        out.println("width: 0%;");
        out.println("background: #2864f0;");
        out.println("transition: width 0.2s;");
        out.println("}");

        /* Main content */
        out.println(".container {");
        out.println("max-width: 1000px;");
        out.println("margin: 0 auto;");
        out.println("padding: 0 18px 35px;");
        out.println("}");

        out.println(".question-card {");
        out.println("background: white;");
        out.println("padding: 25px;");
        out.println("border-radius: 16px;");
        out.println("margin-bottom: 20px;");
        out.println("box-shadow: 0 5px 20px rgba(20,40,80,0.06);");
        out.println("scroll-margin-top: 180px;");
        out.println("}");

        out.println(".question-card h3 {");
        out.println("margin: 0 0 20px;");
        out.println("font-size: 20px;");
        out.println("line-height: 1.5;");
        out.println("}");

        out.println(".option-label {");
        out.println("display: flex;");
        out.println("align-items: center;");
        out.println("gap: 12px;");
        out.println("background: #f5f7fc;");
        out.println("border: 1px solid #dce4f3;");
        out.println("padding: 14px 16px;");
        out.println("margin: 10px 0;");
        out.println("border-radius: 12px;");
        out.println("cursor: pointer;");
        out.println("line-height: 1.5;");
        out.println("overflow-wrap: anywhere;");
        out.println("}");

        out.println(".option-label:hover {");
        out.println("background: #eaf1ff;");
        out.println("border-color: #8bb3ff;");
        out.println("}");

        out.println(".option-label input {");
        out.println("width: 18px;");
        out.println("height: 18px;");
        out.println("flex-shrink: 0;");
        out.println("cursor: pointer;");
        out.println("}");

        out.println(".submit-btn {");
        out.println("display: block;");
        out.println("width: 100%;");
        out.println("padding: 16px;");
        out.println("background: #2864f0;");
        out.println("color: white;");
        out.println("border: none;");
        out.println("border-radius: 10px;");
        out.println("font-size: 18px;");
        out.println("font-weight: bold;");
        out.println("cursor: pointer;");
        out.println("}");

        out.println(".submit-btn:hover { background: #174fd0; }");

        out.println("@media (max-width: 600px) {");
        out.println(".topbar { padding: 13px 16px; }");
        out.println(".brand { font-size: 17px; }");
        out.println(".quiz-header { padding: 12px 15px; }");
        out.println(".quiz-header h2 { font-size: 18px; }");
        out.println(".timer { font-size: 17px; }");
        out.println(".container { padding: 0 10px 25px; }");
        out.println(".question-card { padding: 17px; }");
        out.println(".question-card h3 { font-size: 17px; }");
        out.println(".option-label { padding: 12px; }");
        out.println("}");

        out.println("</style>");
        out.println("</head>");
        out.println("<body>");

        out.println("<div class='topbar'>");
        out.println("<div class='brand'>Online Quiz System</div>");
        out.println("<div>Welcome, " + studentName + "</div>");
        out.println("</div>");

        out.println("<main class='container'>");

        out.println("<div class='quiz-header'>");
        out.println("<h2>Online Quiz Assessment</h2>");
        out.println("<p class='quiz-info'>20 Questions | 20 Minutes</p>");

        out.println("<div class='timer-row'>");
        out.println("<div class='timer'>Time Remaining: <span id='timer'>20:00</span></div>");
        out.println("<div class='progress-text' id='progressText'>0 of 20 questions answered</div>");
        out.println("</div>");

        out.println("<div class='progress-track'>");
        out.println("<div class='progress-fill' id='progressFill'></div>");
        out.println("</div>");
        out.println("</div>");

        out.println("<form id='quizForm' method='post' action='"
                + request.getContextPath() + "/result'>");

        for (int i = 0; i < questions.length; i++) {

            out.println("<section class='question-card'>");
            out.println("<h3>" + (i + 1) + ". "
                    + escapeHtml(questions[i]) + "</h3>");

            for (int j = 0; j < options[i].length; j++) {

                out.println("<label class='option-label'>");

                out.println("<input type='radio' name='q" + i
                        + "' value='" + j + "'>");

                // Escape HTML so tags and comments display as text.
                out.println("<span>" + escapeHtml(options[i][j]) + "</span>");

                out.println("</label>");
            }

            out.println("</section>");
        }

        out.println("<button type='submit' class='submit-btn'>Submit Quiz</button>");
        out.println("</form>");
        out.println("</main>");

        /* Timer and progress JavaScript */
        out.println("<script>");

        out.println("const quizForm = document.getElementById('quizForm');");
        out.println("const totalQuestions = " + questions.length + ";");
        out.println("let timeLeft = 20 * 60;");
        out.println("let submitted = false;");

        out.println("function updateProgress() {");
        out.println("  const answered = new Set();");
        out.println("  document.querySelectorAll('input[type=radio]:checked').forEach(function(input) {");
        out.println("    answered.add(input.name);");
        out.println("  });");
        out.println("  document.getElementById('progressText').textContent = answered.size + ' of ' + totalQuestions + ' questions answered';");
        out.println("  document.getElementById('progressFill').style.width = (answered.size / totalQuestions * 100) + '%';");
        out.println("}");

        out.println("document.querySelectorAll('input[type=radio]').forEach(function(input) {");
        out.println("  input.addEventListener('change', updateProgress);");
        out.println("});");

        out.println("function updateTimer() {");
        out.println("  if (submitted) return;");
        out.println("  const minutes = Math.floor(timeLeft / 60);");
        out.println("  const seconds = timeLeft % 60;");
        out.println("  document.getElementById('timer').textContent = String(minutes).padStart(2, '0') + ':' + String(seconds).padStart(2, '0');");
        out.println("  if (timeLeft <= 0) {");
        out.println("    submitted = true;");
        out.println("    alert('Time is up! Your quiz will be submitted.');");
        out.println("    quizForm.submit();");
        out.println("    return;");
        out.println("  }");
        out.println("  timeLeft--;");
        out.println("}");

        out.println("quizForm.addEventListener('submit', function(event) {");
        out.println("  if (!submitted && timeLeft > 0) {");
        out.println("    if (!confirm('Are you sure you want to submit your quiz?')) {");
        out.println("      event.preventDefault();");
        out.println("      return;");
        out.println("    }");
        out.println("  }");
        out.println("  submitted = true;");
        out.println("});");

        out.println("updateProgress();");
        out.println("updateTimer();");
        out.println("setInterval(updateTimer, 1000);");

        out.println("</script>");
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
