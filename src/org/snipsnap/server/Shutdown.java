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
package org.snipsnap.server;

import org.mortbay.http.HttpServer;
import org.mortbay.util.Code;
import org.mortbay.util.Log;

import java.util.Iterator;

/**
 * Helper class for shutting down the Server
 * @author Matthias L. Jugel
 * @version $Id$
 */
public class Shutdown {
  /**
   * Shut down complete server ...
   */
  public static void shutdown() {
    Log.event("Application: stopping all servers");
    System.out.println("INFO: Stopping all servers ...");
    Iterator s = HttpServer.getHttpServers().iterator();
    while (s.hasNext()) {
      HttpServer server = (HttpServer) s.next();
      try {
        System.out.println("INFO: stopping " + server);
        server.stop();
      } catch (Exception e) {
        Code.ignore(e);
      }
    }
    System.out.println("SnipSnap shutdown procedure finished.");
    Log.event("Application: stopped all servers");
  }
}
