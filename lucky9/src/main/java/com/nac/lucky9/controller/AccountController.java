/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nac.lucky9.controller;

import com.nac.lucky9.entity.Account;
import com.nac.lucky9.service.AccountService;
import java.io.Serializable;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import org.omnifaces.util.Faces;

/**
 *
 * @author nathaniel camomot <nathaniel.dtit@gmail.com>
 */
@Named
@SessionScoped
public class AccountController implements Serializable {

  private Long sessionAccountId = null;

  public Long getSessionAccountId() {
    return sessionAccountId;
  }

  public void setSessionAccountId(Long sessionAccountId) {
    this.sessionAccountId = sessionAccountId;
  }

}
