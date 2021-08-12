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

public class EditSchedule1 extends HttpServlet{

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

        int year = -1;
        int month = -1;
        int day = -1;
        int currentscheduleid;
        String currentStartTime = "";
        String currentEndTime = "";
        String currentSchedule = "";
        String currentMemo = "";

        String param = req.getParameter("ID");
        if (param == null || param.length() == 0){
            currentscheduleid = -1;
        }else{
            try{
                currentscheduleid = Integer.parseInt(param);
            }catch (NumberFormatException e){
                currentscheduleid = -1;
            }
        }

        /* パラメータが不正な場合はトップページへリダイレクト */
        if (currentscheduleid == -1){
            res.sendRedirect("/schedule/top.html");
        }

        /* ユーザー情報を取り出す */
        HttpSession session = req.getSession(false);
        String username = (String)session.getAttribute("username");
        String tmpuserid = (String)session.getAttribute("userid");
        int userid = 0;
        if (tmpuserid != null){
            userid = Integer.parseInt(tmpuserid);
        }

        try {
            String sql = "SELECT * FROM schedule WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setInt(1, currentscheduleid);
            ResultSet rs = pstmt.executeQuery();

            rs.next();
           	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String scheduledate = dateFormat.format(rs.getDate("scheduledate"));
            String yearStr = scheduledate.substring(0, 4);
            String monthStr = scheduledate.substring(5, 7);
            String dayStr = scheduledate.substring(8, 10);

            year = Integer.parseInt(yearStr);
            month = Integer.parseInt(monthStr) - 1;
            day = Integer.parseInt(dayStr);

            dateFormat = new SimpleDateFormat("HH:mm:ss");
            currentStartTime = dateFormat.format(rs.getTime("starttime"));
            currentEndTime = dateFormat.format(rs.getTime("endtime"));
            currentSchedule = rs.getString("schedule");
            currentMemo = rs.getString("schedulememo");

            rs.close();
            pstmt.close();

        }catch (SQLException e){
            log("SQLException:" + e.getMessage());
        }

        StringBuffer sb = new StringBuffer();

        sb.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.0.1//EN\" \"http://www.w3.org/TR/html4/strict.dtd\">");

        sb.append("<html lang=\"ja\">");
        sb.append("<head>");
        sb.append("<meta http-equiv=\"Content-Type\" Content=\"text/html;charset=Shift_JIS\">");

        sb.append("<title>スケジュール変更</title>");

        sb.append("<style>");
        sb.append("table.sche{border:1px solid #a9a9a9;padding:0px;margin:0px;border-collapse:collapse;}");
        sb.append("td{vertical-align:top;margin:0px;padding:2px;font-size:0.75em;height:20px;}");
        sb.append("td.top{border-bottom:1px solid #a9a9a9;text-align:center;}");
        sb.append("td.time{background-color:#f0f8ff;text-align:right;border-right:1px double #a9a9a9;padding-right:5px;}");
        sb.append("td.timeb{background-color:#f0f8ff;border-bottom:1px solid #a9a9a9;border-right:1px double #a9a9a9;}");
        sb.append("td.contents{background-color:#ffffff;border-bottom:1px dotted #a9a9a9;}");
        sb.append("td.contentsb{background-color:#ffffff;border-bottom:1px solid #a9a9a9;}");
        sb.append("td.ex{background-color:#ffebcd;border:1px solid #8b0000;}");
        sb.append("table.view{border:1px solid #a9a9a9;padding:0px;margin:0px;border-collapse:collapse;width:250px}");
        sb.append("table.view td{border:1px solid #a9a9a9;}");
        sb.append("table.view td.left{width:70px;background-color:#f0f8ff;}");
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
        sb.append("スケジュールの変更&nbsp;&nbsp;");
        sb.append("[<a href=\"/schedule/ScheduleView?ID=");
        sb.append(currentscheduleid);
        sb.append("\">スケジュール表示に戻る</a>]");
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

                    StringBuffer sbSchedule = new StringBuffer();
                    sbSchedule.append("<a href=\"/schedule/ScheduleView?ID=");
                    sbSchedule.append(id);
                    sbSchedule.append("\">");
                    sbSchedule.append(schedule);
                    sbSchedule.append("</a>");

                    scheduleArray[0] = scheduleArray[0] + (new String(sbSchedule)) + "<br>";

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

        sb.append("<form method=\"post\" action=\"/schedule/ScheduleUpdate\">");
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
        if (currentStartTime == null){
            sb.append("<option value=\"\" selected>--時");
            for (int i = 0 ; i <= 23 ; i++){
                sb.append("<option value=\"");
                sb.append(i);
                sb.append("\">");
                sb.append(i);
                sb.append("時");
            }
        }else{
            sb.append("<option value=\"\">--時");

            int shour = Integer.parseInt(currentStartTime.substring(0, 2));

            for (int i = 0 ; i <= 23 ; i++){
                sb.append("<option value=\"");
                sb.append(i);
                sb.append("\"");

                if (i == shour){
                    sb.append(" selected");
                }

                sb.append(">");
                sb.append(i);
                sb.append("時");
            }
        }
        sb.append("</select> ");

        sb.append("<select name=\"SMINUTE\">");
        if (currentStartTime == null){
            sb.append("<option value=\"0\" selected>00分");
            sb.append("<option value=\"30\">30分");
        }else{
            String sminute = currentStartTime.substring(3, 5);
            if (sminute.equals("00")){
                sb.append("<option value=\"0\" selected>00分");
                sb.append("<option value=\"30\">30分");
            }else{
                sb.append("<option value=\"0\">00分");
                sb.append("<option value=\"30\" selected>30分");
            }
        }
        sb.append("</select>");

        sb.append(" -- ");

        sb.append("<select name=\"EHOUR\">");
        if (currentEndTime == null){
            sb.append("<option value=\"\" selected>--時");
            for (int i = 0 ; i <= 23 ; i++){
                sb.append("<option value=\"");
                sb.append(i);
                sb.append("\">");
                sb.append(i);
                sb.append("時");
            }
        }else{
            sb.append("<option value=\"\">--時");

            int ehour = Integer.parseInt(currentEndTime.substring(0, 2));

            for (int i = 0 ; i <= 23 ; i++){
                sb.append("<option value=\"");
                sb.append(i);
                sb.append("\"");

                if (i == ehour){
                    sb.append(" selected");
                }

                sb.append(">");
                sb.append(i);
                sb.append("時");
            }
        }
        sb.append("</select> ");

        sb.append("<select name=\"EMINUTE\">");
        if (currentEndTime == null){
            sb.append("<option value=\"0\" selected>00分");
            sb.append("<option value=\"30\">30分");
        }else{
            String eminute = currentEndTime.substring(3, 5);
            if (eminute.equals("00")){
                sb.append("<option value=\"0\" selected>00分");
                sb.append("<option value=\"30\">30分");
            }else{
                sb.append("<option value=\"0\">00分");
                sb.append("<option value=\"30\" selected>30分");
            }
        }
        sb.append("</select>");

        sb.append("</td>");
        sb.append("</tr>");

        sb.append("<tr>");
        sb.append("<td nowrap>予定</td>");
        sb.append("<td><input type=\"text\" name=\"PLAN\" value=\"");
        sb.append(currentSchedule);
        sb.append("\" size=\"30\" maxlength=\"100\">");
        sb.append("</td>");
        sb.append("</tr>");

        sb.append("<tr>");
        sb.append("<td valign=\"top\" nowrap>メモ</td>");
        sb.append("<td><textarea name=\"MEMO\" cols=\"30\" rows=\"10\" wrap=\"virtual\">");
        sb.append(currentMemo);
        sb.append("</textarea></td>");
        sb.append("</tr>");
        sb.append("</table>");

        sb.append("<p>");

        /* 変更するスケジュールの「ID」を隠し項目として設定 */
        sb.append("<input type=\"hidden\" name=\"ID\" value=\"");
        sb.append(currentscheduleid);
        sb.append("\">");
        
sb.append("<input type=\"submit\" name=\"Register\" value=\"変更する\">");
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
