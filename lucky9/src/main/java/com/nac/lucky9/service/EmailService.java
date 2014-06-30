/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nac.lucky9.service;

import com.nac.lucky9.Lucky9;
import com.nac.lucky9.util.Lucky9Util;
import java.io.IOException;
import java.io.Serializable;
import java.net.URLEncoder;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.omnifaces.util.Faces;

/**
 *
 * @author nathaniel camomot <nathaniel.dtit@gmail.com>
 */
@Stateless
public class EmailService implements Serializable{

  @EJB
  private ConfigurationService confService;

  public void sendActivationMail(String accountEmail, String activationCode) throws EmailException, IOException {
    
    HttpServletRequest origRequest = (HttpServletRequest) Faces.getExternalContext().getRequest();
    String message = "Please click on this link to activate your Lucky9 account: " + Lucky9Util.getServerUrl(origRequest) + "signup.xhtml"
            + "?ac=" + URLEncoder.encode(activationCode, "UTF-8");
    Lucky9.infoInternal("mail message: " + message);
    
    Email mail = new SimpleEmail();
    mail.setHostName(confService.getString("smtp.host"));
    mail.setSmtpPort(Integer.parseInt(confService.getString("smtp.port")));
    mail.setDebug(true);
    mail.setAuthenticator(new DefaultAuthenticator(confService.getString("super.user.email"), confService.getString("super.user.password")));
    mail.setStartTLSEnabled(true);
    mail.setFrom(confService.getString("super.user.email"));
    mail.setSubject("Lucky9 - Activate account");
    mail.setMsg(message);

    mail.addTo(accountEmail);
    mail.getMailSession().getProperties().put("mail.smtp.ssl.trust", confService.getString("smtp.host"));

    mail.send();
  }
}
