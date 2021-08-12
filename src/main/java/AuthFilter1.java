import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class AuthFilter1 implements Filter{

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain){

        try{

            String target = ((HttpServletRequest)request).getRequestURI();

            HttpSession session = ((HttpServletRequest)request).getSession();

            if (session == null){
                session = ((HttpServletRequest)request).getSession(true);

                ((HttpServletResponse)response).sendRedirect("/schedule/LoginPage");
            }else{
                Object loginCheck = session.getAttribute("login");
                if (loginCheck == null){
                    ((HttpServletResponse)response).sendRedirect("/schedule/LoginPage");
                }
            }

            chain.doFilter(request, response);

        }catch (ServletException se){
        }catch (IOException e){
        }

    }

    public void init(FilterConfig filterConfig) throws ServletException{
    }

    public void destroy(){
    }
}
