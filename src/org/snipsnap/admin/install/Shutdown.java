/*
 * This file is part of "SnipSnap Wiki/Weblog".
 *
 * Copyright (c) 2002 Stephan J. Schmidt, Matthias L. Jugel
 * All Rights Reserved.
 *
 * Please visit http://snipsnap.org/ for updates and contact.
 *
 * --LICENSE NOTICE--
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 * --LICENSE NOTICE--
 */
package org.snipsnap.admin.install;

import org.snipsnap.config.AppConfiguration;
import org.snipsnap.config.Configuration;
import org.snipsnap.snip.SnipLink;
import org.snipsnap.admin.CommandHandler;
import org.mortbay.http.HttpServer;
import org.mortbay.util.Code;
import org.mortbay.util.Log;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;

/**
 * Application configuration servlet.
 * @author Matthias L. Jugel
 * @version $Id$
 */
public class Shutdown extends HttpServlet {

  public void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {

    HttpSession session = request.getSession();
    if (session != null) {
      String user = request.getParameter("login");
      String pass = request.getParameter("password");

      Configuration config = (Configuration) session.getAttribute(CommandHandler.ATT_CONFIG);

      // don't do anything before user name and password are checked
      if (config != null &&
        config.getAdminLogin().equals(user) &&
        config.getAdminPassword().equals(pass)) {
        // shut down server ...
        org.snipsnap.server.Shutdown.shutdown();
        response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, "Server has been shut down.");
        return;
      }
    }

    response.sendRedirect(SnipLink.absoluteLink(request, "/"));
  }

  private void writeMessage(PrintWriter out, String message) {
    out.println(message);
    out.flush();
  }

}
