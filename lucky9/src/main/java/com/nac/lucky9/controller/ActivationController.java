/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nac.lucky9.controller;

import com.nac.lucky9.Lucky9;
import com.nac.lucky9.entity.Account;
import com.nac.lucky9.service.AccountService;
import com.nac.lucky9.service.ConfigurationService;
import com.nac.lucky9.service.EmailService;
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
import org.apache.commons.mail.EmailException;
import org.omnifaces.util.Faces;

/**
 *
 * @author nathaniel camomot <nathaniel.dtit@gmail.com>
 */
@Named
@RequestScoped
public class ActivationController implements Serializable {

  @NotNull
  @Size(min = 0, max = 255)
  private String activationCode;
  @Inject
  private AccountController accountController;
  @EJB
  private AccountService accountService;
  @EJB
  private EmailService emailService;
  @EJB
  private ConfigurationService confService;

  public void activate() {

    if (activationCode == null) {
      return;
    }

    Account account = accountService.findAccountByActivationCode(activationCode);

    if (account == null) {
      Lucky9.error("Activation failed. Invalid code.");
      return;
    }

    if (account.isActivated()) {
      Lucky9.error("Activation failed. Already activated.");
      return;
    }

    if (Calendar.getInstance().after(account.getActivationCodeExpires())) {
      Lucky9.error("Activation failed. Late activation.");
      return;
    }

    account.setActivated(true);

    try {
      accountService.edit(account, confService.getSuperUserId());
    } catch (Throwable ex) {
      Lucky9.error(ex);
    }

    accountController.setSessionAccountId(account.getId());

    try {
      Faces.redirect("game/index.xhtml");
    } catch (IOException ex) {
      Lucky9.error(ex);
    }
  }

  public void resend() {
    Long id = accountController.getSessionAccountId();

    if (id == null) {
      Lucky9.error("Not logged in.");
      return;
    }

    Account account = accountService.find(id);

    if (account == null) {
      Lucky9.error("Invalid account.");
      return;
    }

    if (account.isActivated()) {
      Lucky9.error("Already activated.");
      return;
    }

    String ac = Lucky9Util.getRandomStr(confService.getInt("activation.code.size"));
    account.setActivationCode(ac);

    Calendar expires = Calendar.getInstance();
    expires.add(Calendar.DATE, confService.getInt("activation.code.expires.days"));
    account.setActivationCodeExpires(expires);

    try {
      accountService.edit(account, confService.getSuperUserId());
    } catch (Exception ex) {
      Lucky9.error(ex);
      return;
    }

    try {
      emailService.sendActivationMail(account.getEmail(), ac);
    } catch (EmailException | IOException ex) {
      Lucky9.error(ex);
      return;
    }

    Lucky9.info("Activation mail sent.");
  }

  public String getActivationCode() {
    return activationCode;
  }

  public void setActivationCode(String activationCode) {
    this.activationCode = activationCode;
  }
}
