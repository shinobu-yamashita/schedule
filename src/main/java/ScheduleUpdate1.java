import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

public class ScheduleUpdate1 extends HttpServlet{

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
        throws ServletException, IOException{

        req.setCharacterEncoding("Shift-JIS");
        res.setContentType("text/html;charset=Shift_Jis");
        PrintWriter out = res.getWriter();

        int id;
        int year;
        int month;
        int day;
        int shour;
        int sminute;
        int ehour;
        int eminute;
        String plan;
        String memo;

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

        param = req.getParameter("DAY");
        if (param == null || param.length() == 0){
            day = -999;
        }else{
            try{
                day = Integer.parseInt(param);
            }catch (NumberFormatException e){
                day = -999;
            }
        }

        param = req.getParameter("SHOUR");
        if (param == null || param.length() == 0){
            shour = -999;
        }else{
            try{
                shour = Integer.parseInt(param);
            }catch (NumberFormatException e){
                shour = -999;
            }
        }

        param = req.getParameter("SMINUTE");
        if (param == null || param.length() == 0){
            sminute = -999;
        }else{
            try{
                sminute = Integer.parseInt(param);
            }catch (NumberFormatException e){
                sminute = -999;
            }
        }

        param = req.getParameter("EHOUR");
        if (param == null || param.length() == 0){
            ehour = -999;
        }else{
            try{
                ehour = Integer.parseInt(param);
            }catch (NumberFormatException e){
                ehour = -999;
            }
        }

        param = req.getParameter("EMINUTE");
        if (param == null || param.length() == 0){
            eminute = -999;
        }else{
            try{
                eminute = Integer.parseInt(param);
            }catch (NumberFormatException e){
                eminute = -999;
            }
        }

        param = req.getParameter("PLAN");
        if (param == null || param.length() == 0){
            plan = "";
        }else{
            try{
                plan = param;
            }catch (NumberFormatException e){
                plan = "";
            }
        }

        param = req.getParameter("MEMO");
        if (param == null || param.length() == 0){
            memo = "";
        }else{
            try{
                memo = param;
            }catch (NumberFormatException e){
                memo = "";
            }
        }

        /* IDや日付が不正な値で来た場合はパラメータ無しで「MonthView」へリダイレクトする */
        if (id == -999 || year == -999 || month == -999 || day == -999){
            res.sendRedirect("/schedule/MonthView");
        }
        String dateStr = year + "-" + month + "-" + day;

        String startTimeStr = shour + ":" + sminute + ":00";
        String endTimeStr = ehour + ":" + eminute + ":00";
        /* 日付が指定されていない場合は、開始及び終了時刻をNULLとして登録する */
        if (shour == -999 || sminute == -999 || ehour == -999 || eminute == -999){
            startTimeStr = null;
            endTimeStr = null;
        }

        try {
            String sql = "update schedule set scheduledate=?, starttime=?, endtime=?, schedule=?, schedulememo=? where id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);

			pstmt.setDate(1, Date.valueOf(dateStr));
			pstmt.setTime(2, Time.valueOf(startTimeStr));
			pstmt.setTime(3, Time.valueOf(endTimeStr));
			pstmt.setString(4, plan);
			pstmt.setString(5, memo);
			pstmt.setInt(6, id);

            int num = pstmt.executeUpdate();

            pstmt.close();

        }catch (SQLException e){
            out.println("SQLException:" + e.getMessage());
        }

        StringBuffer sb = new StringBuffer();
        sb.append("/schedule/ScheduleView");
        sb.append("?ID=");
        sb.append(id);
        res.sendRedirect(new String(sb));

    }
}
