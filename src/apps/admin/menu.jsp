<!--
  ** Admin menu.
  ** @author Matthias L. Jugel
  ** @version $Id$
  -->

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<c:if test="${config.configured && admin != null}">
  (<i>You need to select an application to enable the <b>User Management</b>!</i>)
  <table class="menu" border="0" cellpadding="8" cellspacing="0">
    <tr>
      <!-- overview -->
      <c:choose>
        <c:when test="${admin != null && page == '/welcome.jsp'}">
          <td align="center" width="32%" class="menuitem-active">
            <a href="../">Overview</a>
          </td>
        </c:when>
        <c:otherwise>
          <td align="center" width="32%" class="menuitem-inactive">
            Overview
          </td>
        </c:otherwise>
      </c:choose>

      <!-- user management -->
      <c:choose>
        <c:when test="${admin != null && page == '/user.jsp' && usermanager != null}">
          <td align="center" width="32%" class="menuitem-active">
            <a href="../exec/user">User Management</a>
          </td>
        </c:when>
        <c:otherwise>
          <td align="center" width="32%" class="menuitem-inactive">
            User Management
          </td>
        </c:otherwise>
      </c:choose>

      <!-- login/logoff -->
      <c:choose>
        <c:when test="${admin != null && config.configured}">
          <td align="center" width="32%" class="menuitem-inactive">
            <a href="../">Logoff</a>
          </td>
        </c:when>
        <c:otherwise>
          <td align="center" width="32%" class="menuitem-active">
            Login
          </td>
        </c:otherwise>
      </c:choose>
    </tr>
  </table>
</c:if>


