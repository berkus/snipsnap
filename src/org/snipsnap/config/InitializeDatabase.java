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
package org.snipsnap.config;

import org.snipsnap.app.Application;
import org.snipsnap.container.Components;
import org.snipsnap.snip.HomePage;
import org.snipsnap.snip.SnipSpace;
import org.snipsnap.snip.XMLSnipImport;
import org.snipsnap.snip.Snip;
import org.snipsnap.snip.label.RenderLabel;
import org.snipsnap.snip.label.RenderEngineLabel;
import org.snipsnap.snip.storage.JDBCSnipStorage;
import org.snipsnap.snip.storage.JDBCUserStorage;
import org.snipsnap.user.Roles;
import org.snipsnap.user.User;
import org.snipsnap.user.UserManager;
import org.snipsnap.util.ConnectionManager;
import org.snipsnap.render.PlainTextRenderEngine;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Writer;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Properties;

public class InitializeDatabase {

  private static PrintWriter writer;

  public static void setOutput(Writer w) {
    InitializeDatabase.writer = new PrintWriter(w);
  }

  private static void message(String message) {
    writer.println("[" + Application.get().getConfiguration().getName() + "] " + message);
    writer.flush();
  }

  public static void init(Configuration config) throws Exception {
    Application app = Application.get();
    app.setConfiguration(config);

    if (detectInternalDatabase(config)) {
      createInternalDatabase(config);
    }

    // initialize storages
    message("creating storages");
    JDBCSnipStorage.createStorage();
    JDBCUserStorage.createStorage();

    (new File(config.getFilePath())).mkdirs();
    (new File(config.getIndexPath())).mkdirs();

    // get an instance of the snip space
    SnipSpace space = (SnipSpace) Components.getComponent(SnipSpace.class);

    createAdministrator(config);

    // load defaults
    InputStream data = getLocalizedResource("i18n.snipsnap", "snip", config.getLocale());
    XMLSnipImport.load(data, XMLSnipImport.OVERWRITE | XMLSnipImport.IMPORT_USERS | XMLSnipImport.IMPORT_SNIPS);

    // disable notifications and pings before posting the first weblog
    String ping = config.get(Configuration.APP_PERM_WEBLOGSPING);
    String notify = config.get(Configuration.APP_PERM_NOTIFICATION);
    config.set(Configuration.APP_PERM_WEBLOGSPING, "deny");
    config.set(Configuration.APP_PERM_NOTIFICATION, "deny");

    postFirstBlog(config, space);

    config.set(Configuration.APP_PERM_WEBLOGSPING, ping);
    config.set(Configuration.APP_PERM_NOTIFICATION, notify);

    message("loading defaults into configuration space");
    // load other configurations
    createSnip(Configuration.SNIPSNAP_CONFIG_API, "/defaults/apidocs.txt", space);
    createSnip(Configuration.SNIPSNAP_CONFIG_ASIN, "/defaults/asinservices.txt", space);
    createSnip(Configuration.SNIPSNAP_CONFIG_BOOK, "/defaults/bookservices.txt", space);
    createSnip(Configuration.SNIPSNAP_CONFIG_PING, "/defaults/weblogsping.txt", space);
    createSnip(Configuration.SNIPSNAP_CONFIG_ROBOTS, "/defaults/robotdetect.txt", space);
    createSnip(Configuration.SNIPSNAP_CONFIG_ROBOTS_TXT, "/defaults/robots.txt", space);
    createSnip(Configuration.SNIPSNAP_CONFIG_WIKI, "/defaults/intermap.txt", space);

    // last, but not least store to file and configuration snip
    storeConfiguration(config, space);

  }

  private static void createSnip(String name, String file, SnipSpace space) throws IOException {
    String content = getResourceAsString(InitializeDatabase.class.getResourceAsStream(file));
    Snip snip = space.create(name, content);
    snip.getLabels().addLabel(new RenderEngineLabel("RenderEngine", "org.snipsnap.render.PlainTextRenderEngine"));
    space.store(snip);
  }


  private static void postFirstBlog(Configuration config, SnipSpace space) throws IOException {
    message("posting initial weblog entry");
    String weblogPost = getResourceAsString(getLocalizedResource("i18n.welcome", "blog", config.getLocale()));
    String title = weblogPost.substring(0, weblogPost.indexOf('\n'));
    weblogPost = weblogPost.substring(weblogPost.indexOf('\n') + 1);
    space.getBlog().post(weblogPost, title);
  }


  private static void storeConfiguration(Configuration config, SnipSpace space) throws IOException {
    message("storing configuration file for bootstrapping SnipSnap");
    config.setInstalled("true");
    File configFile = new File(Application.get().getConfiguration().getWebInfDir(), "application.conf");
    config.storeBootstrap(new FileOutputStream(configFile));

    message("creating configuration snip '" + Configuration.SNIPSNAP_CONFIG + "'");
    ByteArrayOutputStream configStream = new ByteArrayOutputStream();
    config.store(configStream);
    Snip snip = space.create(Configuration.SNIPSNAP_CONFIG, new String(configStream.toString("UTF-8")));
    snip.getLabels().addLabel(new RenderEngineLabel("RenderEngine", "org.snipsnap.render.PlainTextRenderEngine"));
    space.store(snip);
  }

  private static User createAdministrator(Configuration config) {
    // create admin account
    message("creating administrator account and snip");
    UserManager um = UserManager.getInstance();
    User admin = um.load(config.getAdminLogin());
    if (admin != null) {
      message("overriding administrator: " + admin);
      um.remove(admin);
    }
    admin = um.create(config.getAdminLogin(), config.getAdminPassword(), config.getAdminEmail());
    admin.getRoles().add(Roles.ADMIN);
    admin.getRoles().add(Roles.EDITOR);
    um.store(admin);

    // make sure the encrypted password is stored
    config.setAdminPassword(admin.getPasswd());

    // set current user and create it's homepage
    Application.get().setUser(admin);
    HomePage.create(config.getAdminLogin());

    return admin;
  }

  private static boolean detectInternalDatabase(Configuration config) {
    return "org.snipsnap.util.MckoiEmbeddedJDBCDriver".equals(config.getJdbcDriver());
  }

  private static void createInternalDatabase(Configuration config) throws IOException, SQLException {
    message("creating internal database");
    // make sure we use the default user/password
    config.setJdbcUser("snipsnap");
    config.setJdbcPassword("snipsnap");

    // create directories
    File dbDir = new File(config.getWebInfDir(), "db");
    dbDir.mkdir();

    // store default configurationn file
    String dbConfFile = config.getJdbcUrl().substring("jdbc:mckoi:local://".length());
    Properties dbConf = new Properties();
    dbConf.load(InitializeDatabase.class.getResourceAsStream("/defaults/mckoidb.conf"));
    dbConf.store(new FileOutputStream(dbConfFile),
                 "SnipSnap Database configuration: " +
                 config.get(Configuration.APP_NAME));

    // store original value of the jdbc url
    String jdbcUrl = config.getProperties().getProperty(Configuration.APP_JDBC_URL);
    // create database (by modifying the jdbc url)
    config.set(Configuration.APP_JDBC_URL, jdbcUrl + "?create=true");
    ConnectionManager.getConnection().close();
    // revert to original setting
    config.setJdbcUrl(jdbcUrl);
  }

  private static String getResourceAsString(InputStream is) throws IOException {
    BufferedReader in = new BufferedReader(new InputStreamReader(is));
    StringBuffer contents = new StringBuffer();
    String line = null;
    while ((line = in.readLine()) != null) {
      contents.append(line).append("\n");
    }
    return contents.toString();
  }

  /**
   * Get the input stream for a localized resource file given the resource base
   * name and its extension and locale.
   * @param resource the base name of the resource
   * @param ext the file name extension (like .snip, .blog)
   * @param locale the locale currently used
   * @return the input stream of the resource or null if none was found
   */
  private static InputStream getLocalizedResource(String resource, String ext, Locale locale) {
    InputStream is = findLocalizedResource(resource, ext, locale);
    if (is == null) {
      is = getResource(resource, null, null, null, ext);
    }
    return is;
  }

  /**
   * Method to find a certain resource based on locale information.
   * @param base the base name of the resource
   * @param ext extension of the resource appended as ".ext"
   * @return the resource found or null
   */
  private static InputStream findLocalizedResource(String base, String ext, Locale locale) {
    String language = locale.getLanguage();
    String country = locale.getCountry();
    String variant = locale.getVariant();

    InputStream is = null;
    if ((is = getResource(base, language, country, variant, ext)) != null) {
      return is;
    } else if ((is = getResource(base, language, country, null, ext)) != null) {
      return is;
    } else if ((is = getResource(base, language, null, null, ext)) != null) {
      return is;
    }
    return is;
  }

  /**
   * Loads a resource from the CLASSPATH by appending language, country, variant to the base name.
   * Example: messages_en_US.snip or messages_en.snip
   * @param base the base name of the file
   * @param ext extension appended as ".ext" to the resource name
   * @return an input stream of the resource or null
   */
  private static InputStream getResource(String base, String language, String country, String variant, String ext) {
    String file = "/"+base.replace('.', '/') +
      (language != null ? "_" + language : "") +
      (country != null ? "_" + country : "") +
      (variant != null ? "_" + variant : "") +
      "." + ext;
    InputStream is = InitializeDatabase.class.getResourceAsStream(file);
    //System.out.println("InitializeDatabase: find: "+file+": "+(is != null));
    return is;
  }
}
