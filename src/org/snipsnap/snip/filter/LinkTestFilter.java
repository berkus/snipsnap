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
/*
 * LinkTestFilter finds [text] in its input and transforms this
 * to <link name="text">
 * Additonally it checks, if the link really exists.
 *
 * @author stephan
 * @team sonicteam
 * @version $Id$
 */
package com.neotis.snip.filter;

import com.neotis.snip.Snip;
import com.neotis.util.Transliterate;
import org.apache.oro.text.regex.*;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class LinkTestFilter extends Filter {

  LinkTester linkTester;
  int limit = Util.SUBSTITUTE_ALL;
  int interps = Perl5Substitution.INTERPOLATE_ALL;
  PatternMatcher matcher = new Perl5Matcher();
  Pattern pattern = null;
  PatternCompiler compiler = new Perl5Compiler();
  String _substitute;
  Transliterate trans;

  public LinkTestFilter(LinkTester linkTester) {
    this.linkTester = linkTester;
    trans = new Transliterate("romaji.properties");

    try {
      pattern = compiler.compile("\\[(.*?)\\]");
    } catch (MalformedPatternException e) {
      System.err.println("error compiling pattern: " + e);
    }

    // super("\\[(.*?)\\]", "<link href=\"$1\"/>");
  }

  public String filter(String input, Snip snip) {
    StringBuffer buffer = new StringBuffer("");

    PatternMatcherInput patternMatcherInput = new PatternMatcherInput(input);

    int lastmatch = 0;

    // Loop until there are no more matches left.
    MatchResult result;
    while (matcher.contains(patternMatcherInput, pattern)) {
      // Since we're still in the loop, fetch match that was found.
      result = matcher.getMatch();
      buffer.append(input.substring(lastmatch, result.beginOffset(0)));
      String key = result.group(1);
      if (key.startsWith("&#")) {
        key = trans.nativeToAscii(key);
      }

      if (key != null) {
        int colonIndex = key.indexOf(':');
        System.err.println("Colon=" + colonIndex);
        if (-1 != colonIndex) {
          String extSpace = key.substring(0, colonIndex);
          key = key.substring(colonIndex+1);
          buffer.append("<a href=\"http://www.langreiter.com/space/");
          try {
            //TODO 1.4 buffer.append(URLEncoder.encode(key, "iso-8859-1"));
            buffer.append(URLEncoder.encode(key));
          } catch (Exception e) {
            buffer.append(key);
          }
          buffer.append("\">");
          buffer.append(key);
          buffer.append("@");
          buffer.append(extSpace);
          buffer.append("</a>");
        } else {
          if (linkTester.exists(key)) {
            buffer.append("<a href=\"../space/");
            try {
              // TODO 1.4 buffer.append(URLEncoder.encode(key, "iso-8859-1"));
              buffer.append(URLEncoder.encode(key));
            } catch (Exception e) {
              buffer.append(key);
            }
            buffer.append("\">").append(result.group(1)).append("</a>");
          } else {
            buffer.append("[create <a href=\"" +
                          "../exec/edit?name=");
            try {
              // TODO 1.4 buffer.append(URLEncoder.encode(key, "iso-8859-1"));
              buffer.append(URLEncoder.encode(key));
            } catch (Exception e) {
              buffer.append(key);
            }
            buffer.append("\">").append(result.group(1)).append("</a>]");
          }
        }
      } else {
        buffer.append(result.group(1)).append("*link error*");
      }


/*
      try {
        buffer.append(URLEncoder.encode(result.group(1), "ISO-8859-1"));
      } catch (UnsupportedEncodingException e) {
        cat.error("unsupported encoding", e);
        buffer.append(result.group(1));
      }
*/
      lastmatch = result.endOffset(0);
    }
    buffer.append(input.substring(lastmatch));
    return buffer.toString();

  }
}
