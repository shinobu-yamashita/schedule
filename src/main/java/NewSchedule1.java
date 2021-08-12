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

public class NewSchedule1 extends HttpServlet{

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

        int year;
        int month;
        int day;

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

        /* パラメータが指定されていない場合は本日の日付を設定 */
        if (year == -999 || month == -999 || day == -999){
            Calendar calendar = Calendar.getInstance();
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DATE);
        }

        /* ユーザー情報を取り出す */
        HttpSession session = req.getSession(false);
        String username = (String)session.getAttribute("username");
        String tmpuserid = (String)session.getAttribute("userid");
        int userid = 0;
        if (tmpuserid != null){
            userid = Integer.parseInt(tmpuserid);
        }

        StringBuffer sb = new StringBuffer();

        sb.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.0.1//EN\" \"http://www.w3.org/TR/html4/strict.dtd\">");

        sb.append("<html lang=\"ja\">");
        sb.append("<head>");
        sb.append("<meta http-equiv=\"Content-Type\" Content=\"text/html;charset=Shift_JIS\">");

        sb.append("<title>スケジュール登録</title>");

        sb.append("<style>");
        sb.append("table.sche{border:1px solid #a9a9a9;padding:0px;margin:0px;border-collapse:collapse;}");
        sb.append("td{vertical-align:top;margin:0px;padding:2px;font-size:0.75em;height:20px;}");
        sb.append("td.top{border-bottom:1px solid #a9a9a9;text-align:center;}");
        sb.append("td.time{background-color:#f0f8ff;text-align:right;border-right:1px double #a9a9a9;padding-right:5px;}");
        sb.append("td.timeb{background-color:#f0f8ff;border-bottom:1px solid #a9a9a9;border-right:1px double #a9a9a9;}");
        sb.append("td.contents{background-color:#ffffff;border-bottom:1px dotted #a9a9a9;}");
        sb.append("td.contentsb{background-color:#ffffff;border-bottom:1px solid #a9a9a9;}");
        sb.append("td.ex{background-color:#ffebcd;border:1px solid #8b0000;}");
        sb.append("img{border:0px;}");
        sb.append("p{font-size:0.75em;}");

        sb.append("#contents{margin:0;padding:0;width:710px;}");
        sb.append("#left{margin:0;padding:0;float:left;width:400px;}");
        sb.append("#right{margin:0;padding:0;float:right;width:300px;background-color:#ffffff;}");
        sb.append("#contents:after{content:\".\";display:block;height:0;clear:both;visibility:hidden;}");
        sb.append("</style>");

        sb.append("</head>");
        sb.append("<body>");

        sb.append("<p>");
        sb.append(username);
        sb.append("さんのスケジュールです");
        sb.append("</p>");

        sb.append("<p>");
        sb.append("スケジュール登録&nbsp;&nbsp;");
        sb.append("[<a href=\"/schedule/MonthView");
        sb.append("?YEAR=");
        sb.append(year);
        sb.append("&MONTH=");
        sb.append(month);
        sb.append("\">カレンダーへ戻る</a>]");
        sb.append("</p>");

        String[] scheduleArray = new String[49];
        int[] widthArray = new int[49];

        for (int i = 0 ; i < 49 ; i++){
            scheduleArray[i] = "";
            widthArray[i] = 0;
        }

        try {
            String sql = "SELECT * FROM schedule WHERE userid = ? and scheduledate = ? ORDER BY starttime";
            PreparedStatement pstmt = conn.prepareStatement(sql);

            String startDateStr = year + "-" + (month + 1) + "-" + day;
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
                    widthArray[0] = 1;
                    scheduleArray[0] = scheduleArray[0] + schedule + "<br>";
                }else{
                    String startTimeStr = starttime.substring(0, 2);
                    String startMinuteStr = starttime.substring(3, 5);

                    int startTimeNum = Integer.parseInt(startTimeStr);
                    int index = startTimeNum * 2 + 1;
                    if (startMinuteStr.equals("30")){
                        index++;
                    }

                    if (widthArray[index] == 0){
                    /* 既にデータが入っていた場合は異常な状態なので無視する */

                        String endTimeStr = endtime.substring(0, 2);
                        String endMinuteStr = endtime.substring(3, 5);

                        int endTimeNum = Integer.parseInt(endTimeStr);
                        int width = (endTimeNum - startTimeNum) * 2;

                        if (startMinuteStr.equals("30")){
                            width--;
                        }

                        if (endMinuteStr.equals("30")){
                            width++;
                        }

                        StringBuffer sbSchedule = new StringBuffer();
                        sbSchedule.append(startTimeStr);
                        sbSchedule.append(":");
                        sbSchedule.append(startMinuteStr);
                        sbSchedule.append("-");
                        sbSchedule.append(endTimeStr);
                        sbSchedule.append(":");
                        sbSchedule.append(endMinuteStr);
                        sbSchedule.append(" ");
                        sbSchedule.append("<a href=\"/schedule/ScheduleView?ID=");
                        sbSchedule.append(id);
                        sbSchedule.append("\">");
                        sbSchedule.append(schedule);
                        sbSchedule.append("</a>");

                        scheduleArray[index] = new String(sbSchedule);
                        widthArray[index] = width;

                        /* 同じスケジュールの先頭以外の箇所には「-1」を設定 */
                        for (int i = 1 ; i < width ; i++){
                            widthArray[index + i] = -1;
                        }
                    }
                }
            }

            rs.close();
            pstmt.close();

        }catch (SQLException e){
            log("SQLException:" + e.getMessage());
        }

        sb.append("<div id=\"contents\">");

        sb.append("<div id=\"left\">");

        sb.append("<table class=\"sche\">");
        sb.append("<tr><td class=\"top\" style=\"width:80px\">時刻</td><td class=\"top\" style=\"width:300px\">予定</td></tr>");

        sb.append("<tr><td class=\"timeb\">未定</td>");
        sb.append("<td class=\"contentsb\">");
        if (widthArray[0] == 1){
            sb.append(scheduleArray[0]);
        }
        sb.append("</td></tr>");

        for (int i = 1 ; i < 49 ; i++){
            if (i % 2 == 1){
                sb.append("<tr><td class=\"time\">");
                sb.append(i / 2);
                sb.append(":00</td>");
            }else{
                sb.append("<tr><td class=\"timeb\"></td>");
            }

            if (widthArray[i] != 0){
                if (widthArray[i] != -1){
                    sb.append("<td class=\"ex\" rowspan=\"");
                    sb.append(widthArray[i]);
                    sb.append("\">");

                    sb.append(scheduleArray[i]);
                }
            }else{
                if (i % 2 == 1){
                    sb.append("<td class=\"contents\">");
                }else{
                    sb.append("<td class=\"contentsb\">");
                }
            }

            sb.append("</td></tr>");
        }

        sb.append("</table>");

        sb.append("</div>");

        sb.append("<div id=\"right\">");

        sb.append("<form method=\"post\" action=\"/schedule/ScheduleInsert\">");
        sb.append("<table>");
        sb.append("<tr>");

        sb.append("<td nowrap>日付</td>");
        sb.append("<td>");
        sb.append("<select name=\"YEAR\">");
        for (int i = 2005 ; i <= 2999 ; i++){
            sb.append("<option value=\"");
            sb.append(i);
            sb.append("\"");
            if (i == year){
                sb.append(" selected");
            }
            sb.append(">");
            sb.append(i);
            sb.append("年");
        }
        sb.append("</select> ");

        sb.append("<select name=\"MONTH\">");
        for (int i = 1 ; i <= 12 ; i++){
            sb.append("<option value=\"");
            sb.append(i);
            sb.append("\"");
            if (i == (month + 1)){
                sb.append(" selected");
            }
            sb.append(">");
            sb.append(i);
            sb.append("月");
        }
        sb.append("</select> ");

        sb.append("<select name=\"DAY\">");
        int monthLastDay = getMonthLastDay(year, month, day);
        for (int i = 1 ; i <= monthLastDay ; i++){
            sb.append("<option value=\"");
            sb.append(i);
            sb.append("\"");
            if (i == day){
                sb.append(" selected");
            }
            sb.append(">");
            sb.append(i);
            sb.append("日");
        }
        sb.append("</select>");

        sb.append("</td>");
        sb.append("</tr>");

        sb.append("<tr>");
        sb.append("<td nowrap>時刻</td>");
        sb.append("<td>");
        sb.append("<select name=\"SHOUR\">");
        sb.append("<option value=\"\" selected>--時");
        for (int i = 0 ; i <= 23 ; i++){
            sb.append("<option value=\"");
            sb.append(i);
            sb.append("\">");
            sb.append(i);
            sb.append("時");
        }
        sb.append("</select> ");

        sb.append("<select name=\"SMINUTE\">");
        sb.append("<option value=\"0\">00分");
        sb.append("<option value=\"30\">30分");
        sb.append("</select>");

        sb.append(" -- ");

        sb.append("<select name=\"EHOUR\">");
        sb.append("<option value=\"\" selected>--時");
        for (int i = 0 ; i <= 23 ; i++){
            sb.append("<option value=\"");
            sb.append(i);
            sb.append("\">");
            sb.append(i);
            sb.append("時");
        }
        sb.append("</select> ");

        sb.append("<select name=\"EMINUTE\">");
        sb.append("<option value=\"0\">00分");
        sb.append("<option value=\"30\">30分");
        sb.append("</select>");

        sb.append("</td>");
        sb.append("</tr>");

        sb.append("<tr>");
        sb.append("<td nowrap>予定</td>");
        sb.append("<td><input type=\"text\" name=\"PLAN\" value=\"\" size=\"30\" maxlength=\"100\">");
        sb.append("</td>");
        sb.append("</tr>");

        sb.append("<tr>");
        sb.append("<td valign=\"top\" nowrap>メモ</td>");
        sb.append("<td><textarea name=\"MEMO\" cols=\"30\" rows=\"10\" wrap=\"virtual\"></textarea></td>");
        sb.append("</tr>");
        sb.append("</table>");

        sb.append("<p>");
        sb.append("<input type=\"submit\" name=\"Register\" value=\"登録する\"> <input type=\"reset\" value=\"入力し直す\">");
        sb.append("<p>");
        sb.append("</form>");

        sb.append("</div>");
        sb.append("</div>");

        sb.append("</body>");
        sb.append("</html>");

        out.println(new String(sb));
    }

    protected int getMonthLastDay(int year, int month, int day){

        Calendar calendar = Calendar.getInstance();

        /* 今月が何日までかを確認する */
        calendar.set(year, month + 1, 0);
        int thisMonthlastDay = calendar.get(Calendar.DATE);

        return thisMonthlastDay;
    }

}
