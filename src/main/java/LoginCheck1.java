import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

public class LoginCheck1 extends HttpServlet {

    protected Connection conn = null;

    public void init() throws ServletException{

    	String jndi_url = "java:comp/env/jdbc/schedule";

    	try {
			Context ctx = new javax.naming.InitialContext();
			DataSource ds = (DataSource)ctx.lookup(jndi_url);
			conn = ds.getConnection();
    	}
//		catch (ClassNotFoundException ex){
//			log("ClassNotFoundException:" + e.getMessage());
//		}
		catch(NamingException ex) {
			// の呼び出しで、この例外が発生する可能性があります。
			log("NamingException:" + ex.getMessage());
		}
		catch(SQLException ex) {
			//ds. で、この例外が発生する可能性があります。
			log("SQLException:" + ex.getMessage());
		}
//		}catch (Exception ex){
//			log("Exception:" + ex.getMessage());
//		}

    }

    public void destory(){
        try{
            if (conn != null){
                conn.close();
            }
        }catch (SQLException e){
            log("SQLException:" + e.getMessage());
        }
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException{

        response.setContentType("text/html; charset=Shift_JIS");
        PrintWriter out = response.getWriter();

        String name = request.getParameter("user");
        String pass = request.getParameter("pass");

        HttpSession session = request.getSession(true);

        boolean check = authUser(name, pass, session);
        if (check){
            /* 認証済みにセット */
            session.setAttribute("login", "OK");

            /* 認証成功後は必ずMonthViewサーブレットを呼びだす */
            response.sendRedirect("/schedule/MonthView");
        }else{
            /* 認証に失敗したら、ログイン画面に戻す */
            session.setAttribute("status", "Not Auth");
            response.sendRedirect("/schedule/LoginPage");
        }
    }

    protected boolean authUser(String name, String pass, HttpSession session){
        if (name == null || name.length() == 0 || pass == null || pass.length() == 0){
            return false;
        }

        try {
            String sql = "SELECT * FROM usertable WHERE name = ? AND pass = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, name);
            pstmt.setString(2, pass);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()){
                int userid = rs.getInt("id");
                int roll = rs.getInt("roll");
                String username = rs.getString("name");

                session.setAttribute("userid", Integer.toString(userid));
                session.setAttribute("roll", Integer.toString(roll));
                session.setAttribute("username", username);

                return true;
            }else{
                return false;
            }
        }catch (SQLException e){
            log("SQLException:" + e.getMessage());
            return false;
        }
    }
}