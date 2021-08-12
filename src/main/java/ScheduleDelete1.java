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
import javax.sql.DataSource;

public class ScheduleDelete1 extends HttpServlet{

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

    public void doGet(HttpServletRequest req, HttpServletResponse res)
        throws ServletException, IOException{

        req.setCharacterEncoding("Shift-JIS");
        res.setContentType("text/html;charset=Shift_Jis");
        PrintWriter out = res.getWriter();

        int id;
        int year;
        int month;

        String param = req.getParameter("ID");
        if (param == null || param.length() == 0){
            id = -999;
        }else{
            try{
                id = Integer.parseInt(param);
            }catch (NumberFormatException e){
                id = -999;
            }
        }

        param = req.getParameter("YEAR");
        if (param == null || param.length() == 0){
            year = -999;
        }else{
            try{
                year = Integer.parseInt(param);
            }catch (NumberFormatException e){
                year = -999;
            }
        }

        param = req.getParameter("MONTH");
        if (param == null || param.length() == 0){
            month = -999;
        }else{
            try{
                month = Integer.parseInt(param);
            }catch (NumberFormatException e){
                month = -999;
            }
        }

        /* IDが不正な値で来た場合はパラメータ無しで「MonthView」へリダイレクトする */
        if (id == -999){
            res.sendRedirect("/schedule/MonthView");
        }

        try {
            String sql = "delete from schedule where id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);

            int num = pstmt.executeUpdate();

            pstmt.close();

        }catch (SQLException e){
            out.println("SQLException:" + e.getMessage());
        }

        StringBuffer sb = new StringBuffer();
        sb.append("/schedule/MonthView");
        sb.append("?YEAR=");
        sb.append(year);
        sb.append("?MONTH=");
        sb.append(month);
        res.sendRedirect(new String(sb));

    }
}
