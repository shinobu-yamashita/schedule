import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

public class CreateUserCheck1 extends HttpServlet {

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

    public void doPost(HttpServletRequest req, HttpServletResponse res)
        throws IOException, ServletException{

        res.setContentType("text/html; charset=Shift_JIS");
        PrintWriter out = res.getWriter();

        String user = req.getParameter("user");
        String pass = req.getParameter("pass");
        String rollStr = req.getParameter("roll");
        int roll;
        if (rollStr == null || rollStr.length() == 0){
            roll = -1;
        }else{
            roll = Integer.parseInt(rollStr);
        }

        HttpSession session = req.getSession(true);

        boolean check = createUser(user, pass, roll);
        if (check){
            session.setAttribute("CreateUserCheck", "Success");
            res.sendRedirect("/schedule/NewUser");
        }else{
            session.setAttribute("CreateUserCheck", "Fail");
            res.sendRedirect("/schedule/NewUser");
        }
    }

    protected boolean createUser(String user, String pass, int roll){
        if (user == null || user.length() == 0 || pass == null || pass.length() == 0 || roll == -1){
            return false;
        }

        try {

            String sql = "insert into usertable (name, pass, roll) values (?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, user);
            pstmt.setString(2, pass);
            pstmt.setInt(3, roll);
            int num = pstmt.executeUpdate();

            return true;
        }catch (SQLException e){
            log("SQLException:" + e.getMessage());
            return false;
        }
    }
}