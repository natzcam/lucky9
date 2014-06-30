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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.apache.commons.mail.EmailException;
import org.omnifaces.util.Faces;

/**
 *
 * @author nathaniel
 */
@Named
@RequestScoped
public class SignupController implements Serializable {

  @NotNull
  @Size(min = 0, max = 255)
  private String email;
  @NotNull
  @Size(min = 0, max = 255)
  private String rawPassword;
  @NotNull
  @Size(min = 0, max = 255)
  private String confirmPassword;
  @EJB
  private AccountService accountService;
  @EJB
  private EmailService emailService;
  @EJB
  private ConfigurationService confService;
  private Pattern pattern = Pattern.compile("\\s");

  public void signup() {
    if (!rawPassword.equals(confirmPassword)) {
      Lucky9.error("Password and Password Confirmation does not match...");
      return;
    }

    if (rawPassword.length() < 8) {
      Lucky9.error("Password should be at least 8 characters long.");
      return;
    }

    Matcher m = pattern.matcher(rawPassword);
    if (m.find()) {
      Lucky9.error("Password should not contain whitespace.");
      return;
    }

    Account current = accountService.findAccountByEmail(email);

    if (current != null) {
      Lucky9.error("User already exists.");
      return;
    }

    current = new Account();
    current.addCredit(2000);
    current.setEmail(email);

    String ac = Lucky9Util.getRandomStr(confService.getInt("activation.code.size"));
    String salt = Lucky9Util.getRandomStr(confService.getInt("salt.size"));

    current.setPassword(Lucky9Util.getHash(rawPassword, salt));
    current.setSalt(salt);
    current.setActivationCode(ac);

    Calendar expires = Calendar.getInstance();
    expires.add(Calendar.DATE, confService.getInt("activation.code.expires.days"));
    current.setActivationCodeExpires(expires);

    try {
      accountService.create(current, confService.getSuperUserId());
    } catch (Exception ex) {
      Lucky9.error(ex);
      return;
    }

    try {
      emailService.sendActivationMail(current.getEmail(), ac);
    } catch (EmailException | IOException ex) {
      Lucky9.error(ex);
      return;
    }

    try {
      Faces.redirect("login.xhtml");
    } catch (IOException ex) {
      Lucky9.error(ex);
    }
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getRawPassword() {
    return rawPassword;
  }

  public void setRawPassword(String rawPassword) {
    this.rawPassword = rawPassword;
  }

  public String getConfirmPassword() {
    return confirmPassword;
  }

  public void setConfirmPassword(String confirmPassword) {
    this.confirmPassword = confirmPassword;
  }

}
