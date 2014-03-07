package com.numina.tophits.action;

import com.numina.tophits.utils.InternalDerbyDbManager;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;

/**
 *
 * @author Bhaskar
 */
public class Logout extends HttpServlet {
    Logger log = Logger.getLogger(this.getClass().getName());
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        HttpSession session = request.getSession(true);
        String employeeid = (String) session.getAttribute("employeeId");
        log.info("LogOut> EmployeeId:"+employeeid);
        try {
            String sqlQuery = "update EMPLOYEE set logstatus='idle' where employeeid='" + employeeid + "'";
            InternalDerbyDbManager.executeUpdate(sqlQuery);
            session.setAttribute("employeeId", null);
            session.setAttribute("clientId", null);
            session.setAttribute("LoginFlag", "0");
            RequestDispatcher rd = request.getRequestDispatcher("index.jsp");
            session.invalidate();
            session = request.getSession(true);
            session.setAttribute("LoginFlag", "0");
            rd.forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error in Logout: "+e.getMessage());
        } finally {
            out.close();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }

}
