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
package com.neotis.net;

import com.neotis.app.Application;
import com.neotis.snip.Snip;
import com.neotis.snip.SnipSpace;
import com.neotis.user.User;
import com.neotis.user.UserManager;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Servlet to store comments.
 * @author Matthias L. Jugel
 * @version $Id$
 */
public class PostStoreServlet extends HttpServlet {
  public void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {

    String name = request.getParameter("post");
    if (request.getParameter("cancel") == null) {
      String content = request.getParameter("content");

      HttpSession session = request.getSession();
      Application app = null;
      if (session != null) {
        app = (Application) session.getAttribute("app");
        User user = app.getUser();
        if (UserManager.getInstance().isAuthenticated(user)) {
          SnipSpace.getInstance().post(content, app);
        } else {
          response.sendError(HttpServletResponse.SC_FORBIDDEN);
        }
      }
    }

    response.sendRedirect("/space/start");
  }
}