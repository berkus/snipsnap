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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.io.Writer;
import java.io.IOException;

/**
 * Formats a list as AtoZ listing separated by the alphabetical characters.
 * @author Matthias L. Jugel
 * @version $Id$
 */
public class AtoZListFormatter implements ListoutputMacro.ListFormatter {

  /**
   * Create an A to Z index
   */
  public void format(Writer writer, String listComment, Collection c, String emptyText, boolean showSize)
    throws IOException {
    if (c.size() > 0) {
      Iterator it = c.iterator();
      Map atozMap = new HashMap();
      List numberRestList = new ArrayList();
      List otherRestList = new ArrayList();
      while (it.hasNext()) {
        Object object = it.next();
        String name = object instanceof Nameable ? ((Nameable)object).getName() : object.toString();
        String indexChar = name.substring(0, 1).toUpperCase();

        if (indexChar.charAt(0) >= 'A' && indexChar.charAt(0) <= 'Z') {
          if (!atozMap.containsKey(indexChar)) {
            atozMap.put(indexChar, new ArrayList());
          }
          List list = (List) atozMap.get(indexChar);
          list.add(name);
        } else if (indexChar.charAt(0) >= '0' && indexChar.charAt(0) <= '9') {
          numberRestList.add(name);
        } else {
          otherRestList.add(name);
        }
      }

      writer.write("<table width=\"100%\" id=\"index\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
      for(int idxChar = 'A'; idxChar <= 'Z'; idxChar++) {
        writer.write("<tr>");
        for(int i = 0; i < 5 && idxChar + i <= 'Z'; i++) {
          String ch = "" + (char)(idxChar + i);
          writer.write("<th><b> &nbsp;<a href=\"#idx"+ch+"\">");
          writer.write(ch);
          writer.write("</a></b></th>");
          writer.write("<th>...</th><th>");
          writer.write(""+(atozMap.get(ch) == null ? 0 : ((List)atozMap.get(ch)).size()));
          writer.write("&nbsp; </th>");
        }
        idxChar += 5;
        if(idxChar >= 'Z') {
          writer.write("<th><b> &nbsp;<a href=\"#idx0-9\">0-9</a></b></th>");
          writer.write("<th>...</th><th>");
          writer.write(""+numberRestList.size());
          writer.write("&nbsp; </th>");
          writer.write("<th><b> &nbsp;<a href=\"#idx@\">@</a></b></th>");
          writer.write("<th>...</th><th>");
          writer.write(""+otherRestList.size());
          writer.write("&nbsp; </th>");
          writer.write("<th></th></th></th><th></th><th></th>");
        }
        writer.write("</tr>");

      }
      writer.write("</table>");

      writer.write("<div class=\"list-title\">");
      writer.write(listComment);
      if(showSize) {
        writer.write(" (");
        writer.write(""+c.size());
        writer.write(")");
      }
      writer.write("</div>");
      writer.write("<table width=\"100%\" id=\"index\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
      for (int ch = 'A'; ch <= 'Z'; ch += 2) {
        String left = "" + (char) ch;
        String right = "" + (char) (ch + 1);

        insertCharHeader(writer, left, right);
        addRows(writer, (List) atozMap.get(left), (List) atozMap.get(right));
      }
      insertCharHeader(writer, "0-9", "@");
      addRows(writer, numberRestList, otherRestList);
      writer.write("</table>");
    } else {
      writer.write(emptyText);
    }
  }

  private void addRows(Writer writer, List listLeft, List listRight) throws IOException {
    Iterator leftIt = listLeft != null ? listLeft.iterator() : new EmptyIterator();
    Iterator rightIt = listRight != null ? listRight.iterator() : new EmptyIterator();

    while (leftIt.hasNext() || rightIt.hasNext()) {
      String leftName = (String) (leftIt != null && leftIt.hasNext() ? leftIt.next() : null);
      String rightName = (String) (rightIt != null && rightIt.hasNext() ? rightIt.next() : null);
      insertRow(writer, leftName, rightName, false);
    }
  }

  private void insertCharHeader(Writer writer, String leftHeader, String rightHeader) throws IOException {
    writer.write("<tr><th>");
    writer.write("<b><a name=\"idx");
    writer.write(leftHeader);
    writer.write("\"></a>");
    writer.write(leftHeader);
    writer.write("</b></th><th> </th><th>");
    writer.write("<b><a name=\"idx");
    writer.write(rightHeader);
    writer.write("\"></a>");
    writer.write(rightHeader);
    writer.write("</b></th></tr>");
  }

  private void insertRow(Writer writer, String left, String right, boolean odd) throws IOException {
    writer.write("<tr><td>");
    if (left != null) {
      SnipLink.appendLink(writer, left);
    }
    writer.write("</td><td> </td><td>");
    if (right != null) {
      SnipLink.appendLink(writer, right);
    }
    writer.write("</td></tr>");
  }

  private class EmptyIterator implements Iterator {
    public boolean hasNext() {
      return false;
    }

    public Object next() {
      return null;
    }

    public void remove() {
    }
  }
}
