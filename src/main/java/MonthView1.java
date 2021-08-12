import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

public class MonthView1 extends HttpServlet{

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

        res.setContentType("text/html;charset=Shift_Jis");
        PrintWriter out = res.getWriter();

        int[] calendarDay;
        int count;

        int year;
        int month;
        int day = 1;

        calendarDay = new int[42];  /* 最大で7日×6週 */
        count = 0;

        String param = req.getParameter("YEAR");
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

        /* パラメータが指定されていない場合は本日の日付を設定 */
        if (year == -999 || month == -999){
            Calendar calendar = Calendar.getInstance();
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DATE);
        }else{
            if (month == 12){
                month = 0;
                year++;
            }

            if (month == -1){
                month = 11;
                year--;
            }
        }

        /* ユーザー情報を取り出す */
        HttpSession session = req.getSession(false);
        Object tmp = session.getAttribute("username");
 
        String username;
        if (tmp == null){
            username = "";
        }else{
            username = (String)tmp;
        }

        tmp = session.getAttribute("userid");
        int userid;
        if (tmp == null){
            userid = 0;
        }else{
            userid = Integer.parseInt((String)tmp);
        }

        tmp = session.getAttribute("roll");
        String roll;
        if (tmp == null){
            roll = "";
        }else{
            roll = (String)tmp;
        }

        StringBuffer sb = new StringBuffer();

        sb.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.0.1//EN\" \"http://www.w3.org/TR/html4/strict.dtd\">");

        sb.append("<html lang=\"ja\">");
        sb.append("<head>");
        sb.append("<meta http-equiv=\"Content-Type\" Content=\"text/html;charset=Shift_JIS\">");

        sb.append("<title>スケジュール管理</title>");

        sb.append("<style>");
        sb.append("table{border:1px solid #a9a9a9;width:90%;padding:0px;margin:0px;border-collapse:collapse;}");
        sb.append("td{width:12%;border-top:1px solid #a9a9a9;border-left:1px solid #a9a9a9;vertical-align:top;margin:0px;padding:2px;}");
        sb.append("td.week{background-color:#f0f8ff;text-align:center;}");
        sb.append("td.day{background-color:#f5f5f5;text-align:right;font-size:0.75em;}");
        sb.append("td.otherday{background-color:#f5f5f5;color:#d3d3d3;text-align:right;font-size:0.75em;}");
        sb.append("td.sche{background-color:#fffffff;text-align:left;height:80px;}");
        sb.append("img{border:0px;}");
        sb.append("span.small{font-size:0.75em;}");
        sb.append("</style>");

        sb.append("</head>");
        sb.append("<body>");

        sb.append("<p>");
        sb.append(username);
        sb.append("さんのスケジュールです");

        if (roll.equals("1")){
            sb.append("&nbsp;[<a href=\"/schedule/NewUser\">ユーザーの追加</a>]");
        }

        sb.append("&nbsp;[<a href=\"/schedule/Logout\">ログアウト</a>]");

        sb.append("</p>");

        /* 日付データを配列に格納 */
        count = setDateArray(year, month, day, calendarDay, count);

        /* 年月のリンク作成 */
        sb.append(createMonthLink(year, month));

        sb.append("<table>");

        sb.append("<tr><td class=\"week\">日</td><td class=\"week\">月</td><td class=\"week\">火</td><td class=\"week\">水</td><td class=\"week\">木</td><td class=\"week\">金</td><td class=\"week\">土</td></tr>");

        int weekCount = count / 7;

        for (int i = 0 ; i < weekCount ; i++){
            /* スケジュールの日付画面を作成する */
            sb.append("<tr>");

            for (int j = i * 7 ; j < i * 7 + 7 ; j++){
                if (calendarDay[j] > 35){
                    sb.append("<td class=\"otherday\">");
                    sb.append(calendarDay[j] - 35);
                }else{
                    sb.append("<td class=\"day\">");
                    sb.append(calendarDay[j]);
                }
                sb.append("</td>");
            }

            sb.append("</tr>");

            /* カレンダーのスケジュール登録画面を作成する */
            sb.append(createScheduleStr(year, month, i * 7, calendarDay, userid));
        }

        sb.append("</table>");

        sb.append("</body>");
        sb.append("</html>");

        out.println(new String(sb));
    }

    /* スケジュール登録へのリンクを設定する */
    protected String createScheduleStr(int year, int month, int startDayNo, int[] calendarDay, int userid){
        StringBuffer sb = new StringBuffer();

        sb.append("<tr>");

        for (int i = startDayNo ; i < startDayNo + 7 ; i++){
            if (calendarDay[i] > 35){
                /* 前月及び翌月の箇所にはアイコンは表示しない */
                sb.append("<td class=\"sche\"></td>");
            }else{
                sb.append("<td class=\"sche\">");
                sb.append("<a href=\"/schedule/NewSchedule");

                /* パラメータの作成 */
                sb.append("?YEAR=");
                sb.append(year);
                sb.append("&MONTH=");
                sb.append(month);
                sb.append("&DAY=");
                sb.append(calendarDay[i]);

                sb.append("\">");
                sb.append("<img src=\"./img/memo.png\" width=\"14\" height=\"16\">");
                sb.append("</a><br>");

                /* スケジュールの表示 */

                sb.append("<span class=\"small\">");

                try {
                    String sql = "SELECT * FROM schedule WHERE userid = ? and scheduledate = ? ORDER BY starttime";
                    PreparedStatement pstmt = conn.prepareStatement(sql);

                    String startDateStr = year + "-" + (month + 1) + "-" + calendarDay[i];
                    pstmt.setInt(1, userid);
        			pstmt.setDate(2, Date.valueOf(startDateStr));

                    ResultSet rs = pstmt.executeQuery();

                    while(rs.next()){
                        int id = rs.getInt("id");
                    	SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
                    	String starttime = dateFormat.format(rs.getTime("starttime"));
                    	String endtime = dateFormat.format(rs.getTime("endtime"));
                        String schedule = rs.getString("schedule");

                        if (starttime == null || endtime == null){
                            sb.append("* ");
                        }else{
                            sb.append(starttime.substring(0, 5));
                            sb.append("-");
                            sb.append(endtime.substring(0, 5));
                            sb.append(" ");
                        }
                        sb.append("<a href=\"/schedule/ScheduleView?ID=");
                        sb.append(id);
                        sb.append("\">");
                        sb.append(schedule);
                        sb.append("</a><br>");
                    }

                    rs.close();
                    pstmt.close();

                }catch (SQLException e){
                    log("SQLException:" + e.getMessage());
                }

                sb.append("</span>");

                sb.append("</td>");
            }
            sb.append("</td>");
        }

        sb.append("</tr>");

        return (new String(sb));
    }

    protected int setDateArray(int year, int month, int day, int[] calendarDay, int count){
        Calendar calendar = Calendar.getInstance();

        /* 今月が何曜日から開始されているか確認する */
        calendar.set(year, month, 1);
        int startWeek = calendar.get(Calendar.DAY_OF_WEEK);

        /* 先月が何日までだったかを確認する */
        calendar.set(year, month, 0);
        int beforeMonthlastDay = calendar.get(Calendar.DATE);

        /* 今月が何日までかを確認する */
        calendar.set(year, month + 1, 0);
        int thisMonthlastDay = calendar.get(Calendar.DATE);

        /* 先月分の日付を格納する */
        for (int i = startWeek - 2 ; i >= 0 ; i--){
            calendarDay[count++] = beforeMonthlastDay - i + 35;
        }

        /* 今月分の日付を格納する */
        for (int i = 1 ; i <= thisMonthlastDay ; i++){
            calendarDay[count++] = i;
        }

        /* 翌月分の日付を格納する */
        int nextMonthDay = 1;
        while (count % 7 != 0){
            calendarDay[count++] = 35 + nextMonthDay++;
        }

        return count;
    }

    protected String createMonthLink(int year, int month){
        StringBuffer sb = new StringBuffer();

        sb.append("<p>");

        sb.append("<a href=\"/schedule/MonthView?YEAR=");
        sb.append(year);
        sb.append("&MONTH=");
        sb.append(month - 1);
        sb.append("\"><span class=\"small\">前月</span></a>&nbsp;&nbsp;");

        sb.append(year);
        sb.append("年");
        sb.append(month + 1);
        sb.append("月&nbsp;&nbsp;");

        sb.append("<a href=\"/schedule/MonthView?YEAR=");
        sb.append(year);
        sb.append("&MONTH=");
        sb.append(month + 1);
        sb.append("\"><span class=\"small\">翌月</span></a>");

        sb.append("</p>");

        return (new String(sb));
    }

}
