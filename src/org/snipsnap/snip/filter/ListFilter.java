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
 * NewlineFilter finds # in its input and transforms this
 * to <newline/>
 *
 * @author stephan
 * @team sonicteam
 * @version $Id$
 */
package org.snipsnap.snip.filter;

import org.snipsnap.snip.filter.regex.RegexReplaceFilter;

public class ListFilter extends RegexReplaceFilter {

  public ListFilter() {
    super("^[:space:]*([-*])[:space:]?(?![-*])(.*)$", "<li class=\"$1\">$2</li>");
    addRegex("^[:space:]*([iIaA])\\.[:space:]?(?![iIaA]\\.)(.*)$", "<li class=\"$1\">$2</li>");
    addRegex("^[:space:]*\\d+\\.[:space:]?(?!\\d+\\.)(.*)$", "<li class=\"enumerated\">$1</li>");
    addRegex("((<li[^>]*>.*?</li>[\r]?[\n]?)+)", "<ul>$1</ul>\n", RegexReplaceFilter.SINGLELINE);
  };
}
