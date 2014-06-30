/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nac.lucky9;

import com.nac.lucky9.controller.AccountController;
import java.io.IOException;
import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author nathaniel camomot <nathaniel.dtit@gmail.com>
 */
@WebFilter(filterName = "Lucky9Filter", urlPatterns = {"/game/*"})
public class Lucky9Filter implements Filter {

  private String login;
  @Inject
  private AccountController accountController;

  @Override
  public void init(FilterConfig fc) throws ServletException {
    login = fc.getServletContext().getContextPath() + "/login.xhtml";
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain fc) throws IOException, ServletException {

    HttpServletRequest req = (HttpServletRequest) request;
    HttpServletResponse res = (HttpServletResponse) response;
    if (accountController.getSessionAccountId() != null) {
      fc.doFilter(request, response);
    } else {
      res.sendRedirect(login);
    }
  }

  @Override
  public void destroy() {
  }
}
