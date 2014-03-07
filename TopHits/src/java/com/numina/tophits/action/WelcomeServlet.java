/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.numina.tophits.action;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

/**
 * 04-Feb-2014
 *
 * @author Bhaskar
 */
public class WelcomeServlet extends HttpServlet {
    static Logger log = Logger.getLogger(WelcomeServlet.class.getName());
    private void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String computerName = null;
        String remoteAddress = request.getRemoteAddr();
        try {
            InetAddress inetAddress = InetAddress.getByName(remoteAddress);
            computerName = inetAddress.getHostName();

            if (computerName.equalsIgnoreCase("localhost")) {
                computerName = java.net.InetAddress.getLocalHost().getCanonicalHostName();
            }
            //System.out.print("Current MAC address: ");
            //NetworkInterface network = NetworkInterface.getByInetAddress(inetAddress);
            //byte[] mac = network.getHardwareAddress();
            //StringBuilder sb = new StringBuilder();
            //for (int i = 0; i < mac.length; i++) {
            //    sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
            //}
            //System.out.println(sb.toString());

            //Add Cookie to the device as unique Id
            Cookie cookie = null;
            Cookie[] cookies = null;
            boolean hasCookie = false;
            // Get an array of Cookies associated with this domain
            cookies = request.getCookies();
            if (cookies != null) {
                for (int i = 0; i < cookies.length; i++) {
                    cookie = cookies[i];
                    if (cookie.getName().equalsIgnoreCase("deviceId")) {
                        hasCookie = true;
                        break;
                    }
                }
                if (hasCookie) {
                    //Read the cookie value
                    //System.out.println(">>>>>>>>>>>>>>>>>>>>>> Cookie device Id:" + "Name: " + cookie.getName() + ", Value: " + cookie.getValue());
                } else {
                    Cookie deviceId = new Cookie("deviceId", "" + Math.random());
                    deviceId.setMaxAge(60 * 60 * 24 * 365 * 50);
                    //System.out.println(">>>>>>>>>>>>>>>>>>>>>> New Device Cookie @ Step 2.");
                    response.addCookie(deviceId);
                }
            } else {
                // Create cookies for deviceId.      
                Cookie deviceId = new Cookie("deviceId", "" + Math.random());
                deviceId.setMaxAge(60 * 60 * 24 * 365 * 50);
                    //System.out.println(">>>>>>>>>>>>>>>>>>>>>> New Device Cookie @ Step 1.");
                response.addCookie(deviceId);
            }

        } catch (Exception e) {
            e.printStackTrace();
            log.error("UnknownHost detected in StartAction. "+e.getMessage(), e);
        }
        if (computerName.trim().length() > 0) {
            computerName = computerName.toUpperCase();
        }
        log.info("Accessed from IP Address: " + remoteAddress);
        response.sendRedirect("index.jsp");
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
        return "Servlet For Welcome and get the unique Id of the Device.";
    }

}
