import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class Logout1 extends HttpServlet {

    public void doGet(HttpServletRequest req, HttpServletResponse res)
        throws IOException, ServletException{

        res.setContentType("text/html; charset=Shift_JIS");
        PrintWriter out = res.getWriter();

        HttpSession session = req.getSession(true);
        session.invalidate();

        res.sendRedirect("/schedule/LoginPage");
    }
}