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
package org.snipsnap.snip.filter.macro.list;

import org.snipsnap.snip.SnipLink;
import org.snipsnap.snip.filter.macro.ListoutputMacro;
import org.snipsnap.util.Nameable;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.Iterator;

/**
 * Simple list formatter.
 * @author Matthias L. Jugel
 * @version $Id$
 */
public class VerticalListFormatter implements ListoutputMacro.ListFormatter {

  /**
   * Display a simple vertical list.
   */
  public void format(Writer writer, String listComment, Collection c, String emptyText, boolean showSize)
    throws IOException {
    writer.write("<div id=\"list\"><div class=\"list-title\">");
    writer.write(listComment);
    if (showSize) {
      writer.write(" (");
      writer.write("" + c.size());
      writer.write(")");
    }
    writer.write("</div>");
    if (c.size() > 0) {
      writer.write("<ul>");
      Iterator nameIterator = c.iterator();
      while (nameIterator.hasNext()) {
        Object object = nameIterator.next();
        String name = object instanceof Nameable ? ((Nameable) object).getName() : object.toString();
        writer.write("<li>");
        SnipLink.appendLink(writer, name);
        writer.write("</li>");
      }
      writer.write("</ul>");
    } else {
      writer.write(emptyText);
    }
    writer.write("</div>");
    return;
  }
}
