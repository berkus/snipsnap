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
package org.snipsnap.test.server;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.xmlrpc.WebServer;
import org.apache.xmlrpc.XmlRpcException;
import org.snipsnap.config.ServerConfiguration;
import org.snipsnap.server.AdminXmlRpcClient;
import org.snipsnap.server.AdminXmlRpcHandler;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.Hashtable;
import java.net.URL;

public class XmlRpcServerTest extends TestCase {
  private static WebServer xmlRpcServer;
  private static AdminXmlRpcClient xmlRpcClient;
  private static Properties config = new Properties();

  public XmlRpcServerTest(String name) throws IOException {
    super(name);
    System.out.println(this + ".XmlRpcServerTest(" + name + ")");
    config.load(XmlRpcServerTest.class.getResourceAsStream("/conf/snipsnap.conf"));
    config.setProperty(ServerConfiguration.ADMIN_HOST, "localhost");
    config.setProperty(ServerConfiguration.ADMIN_PASS, "test");
    File tmpFile = File.createTempFile("xmlrpctest", "root");
    tmpFile.delete();
    tmpFile.mkdirs();
    config.setProperty(ServerConfiguration.WEBAPP_ROOT, tmpFile.getPath());
  }

  protected void setUp() throws Exception {
    System.out.println(this + ".setUp()");
    super.setUp();
    if (null == xmlRpcServer) {
      xmlRpcServer = new WebServer(Integer.parseInt(config.getProperty(ServerConfiguration.ADMIN_PORT)));
      xmlRpcServer.addHandler("$default", new AdminXmlRpcHandler(config));
      xmlRpcServer.start();
    }

    xmlRpcClient = new AdminXmlRpcClient(config.getProperty(ServerConfiguration.ADMIN_HOST),
                                         config.getProperty(ServerConfiguration.ADMIN_PORT),
                                         config.getProperty(ServerConfiguration.ADMIN_PASS));
  }

  public static Test suite() {
    return new TestSuite(XmlRpcServerTest.class);
  }

  public void testXmlRpcInstall() throws IOException {
    try {
      URL url = new URL("http://localhost:8668/");
      assertEquals(url, xmlRpcClient.install("test", "localhost", "8668", "/"));
    } catch (XmlRpcException e) {
      fail("installation of application failed: " + e.getMessage());
    }
  }

  public void testXmlRpcApplicationList() throws IOException {
    try {
      Hashtable list = xmlRpcClient.getApplications();
      assertEquals(list.size(), 0);
    } catch (XmlRpcException e) {
      fail("listing of applications failed: " + e.getMessage());
    }
  }

  public void testXmlRpcDelete() throws IOException {
    try {
      xmlRpcClient.delete("test", false);
    } catch (XmlRpcException e) {
      fail("deletion of application failed: " + e.getMessage());
    }
  }
}