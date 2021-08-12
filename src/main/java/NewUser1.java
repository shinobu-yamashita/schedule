import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class NewUser1 extends HttpServlet{

    public void doGet(HttpServletRequest req, HttpServletResponse res)
        throws IOException, ServletException{

        res.setContentType("text/html; charset=Shift_JIS");
        PrintWriter out = res.getWriter();

        /* ユーザー情報を取り出す */
        HttpSession session = req.getSession(false);
        String roll = (String)session.getAttribute("roll");
        if (roll == null || !roll.equals("1")){
            res.sendRedirect("/schedule/MonthView");
        }

        StringBuffer sb = new StringBuffer();

        sb.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.0.1//EN\" \"http://www.w3.org/TR/html4/strict.dtd\">");

        sb.append("<html lang=\"ja\">");
        sb.append("<head>");
        sb.append("<meta http-equiv=\"Content-Type\" Content=\"text/html;charset=Shift_JIS\">");
        sb.append("<title>ユーザーの作成</title>");
        sb.append("</head>");
        sb.append("<body>");

        sb.append("<h1>ユーザーの作成</h1>");
        sb.append("<p>新しいユーザーを作成します</p>");

        Object status = session.getAttribute("CreateUserCheck");

        if (status != null){
            String statusStr = (String)status;

            if (statusStr.equals("Fail")){
                sb.append("<p>ユーザーの作成に失敗しました</p>");
                sb.append("<p>再度ユーザー名とパスワードを入力して下さい</p>");
            }else if (statusStr.equals("Success")){
                sb.append("<p>ユーザーの作成に成功しました</p>");
                sb.append("<p>続けて作成する場合はユーザー名とパスワードを入力して下さい</p>");
            }

            session.setAttribute("CreateUserCheck", null);
        }

        sb.append("<form method=\"POST\" action=\"/schedule/CreateUserCheck\" name=\"loginform\">");
        sb.append("<table>");
        sb.append("<tr>");
        sb.append("<td>ユーザー名</td>");
        sb.append("<td><input type=\"text\" name=\"user\" size=\"32\"></td>");
        sb.append("</tr>");
        sb.append("<tr>");
        sb.append("<td>パスワード</td>");
        sb.append("<td><input type=\"password\" name=\"pass\" size=\"32\"></td>");
        sb.append("</tr>");
        sb.append("<tr>");
        sb.append("<td>権限</td>");
        sb.append("<td>");
        sb.append("<select name=\"roll\">");
        sb.append("<option value=\"1\">管理者");
        sb.append("<option value=\"0\" selected>一般");
        sb.append("</select>");
        sb.append("</td>");
        sb.append("</tr>");
        sb.append("<tr>");
        sb.append("<td><input type=\"submit\" value=\"create\"></td>");
        sb.append("<td><input type=\"reset\" value=\"reset\"></td>");
        sb.append("</tr>");
        sb.append("</table>");
        sb.append("</form>");

        sb.append("<p><a href=\"/schedule/MonthView\">スケジュール一覧へ</a></p>");

        sb.append("</body>");
        sb.append("</html>");

        out.println(new String(sb));
    }
}