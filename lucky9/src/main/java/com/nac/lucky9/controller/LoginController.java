/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nac.lucky9.controller;

import com.nac.lucky9.Lucky9;
import com.nac.lucky9.entity.Account;
import com.nac.lucky9.service.AccountService;
import com.nac.lucky9.util.Lucky9Util;
import java.io.IOException;
import java.io.Serializable;
import java.util.Calendar;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.omnifaces.util.Faces;

/**
 *
 * @author nathaniel
 */
@Named
@RequestScoped
public class LoginController implements Serializable {

  @NotNull
  @Size(min = 0, max = 255)
  private String email;
  @NotNull
  @Size(min = 0, max = 255)
  private String password;
  @Inject
  private AccountController accountController;
  @EJB
  private AccountService accountService;

  public void login() {

    Account account = accountService.findAccountByEmail(email);

    if (account == null) {
      Lucky9.error("Login failed.");
      return;
    }

    String hash = Lucky9Util.getHash(password, account.getSalt());

    if (!hash.equals(account.getPassword())) {
      Lucky9.error("Login failed.");
      return;
    }

    if (account.isActivated() || Calendar.getInstance().before(account.getActivationCodeExpires())) {
      accountController.setSessionAccountId(account.getId());
      try {
        Faces.redirect("game/index.xhtml");
      } catch (IOException ex) {
        Lucky9.error(ex);
      }
    } else {
      try {
        accountService.remove(account);
      } catch (Exception ex) {
        Lucky9.error(ex);
        return;
      }

      Lucky9.error("Login failed. Activation time expired");
    }

  }

  public String logout() {
    accountController.setSessionAccountId(null);
    return "/login?faces-redirect=true";
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }
}
