/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nac.lucky9.entity;

import java.io.Serializable;
import java.util.Calendar;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

/**
 *
 * @author nathaniel camomot <nathaniel.dtit@gmail.com>
 */
@MappedSuperclass
public class BaseEntity implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  protected Long id;
  @NotNull
  @Column(name = "create_time", nullable = false)
  @Temporal(TemporalType.TIMESTAMP)
  protected Calendar createTime;
  @Column(name = "edit_time")
  @Temporal(TemporalType.TIMESTAMP)
  protected Calendar editTime;
  @JoinColumn(name = "create_account_id", referencedColumnName = "id", nullable = false)
  @ManyToOne(optional = false, cascade = CascadeType.ALL)
  protected Account createAccount;
  @JoinColumn(name = "edit_account_id", referencedColumnName = "id")
  @ManyToOne(cascade = CascadeType.ALL)
  protected Account editAccount;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Calendar getCreateTime() {
    return createTime;
  }

  public void setCreateTime(Calendar createTime) {
    this.createTime = createTime;
  }

  public Calendar getEditTime() {
    return editTime;
  }

  public void setEditTime(Calendar editTime) {
    this.editTime = editTime;
  }

  public Account getCreateAccount() {
    return createAccount;
  }

  public void setCreateAccount(Account createAccount) {
    this.createAccount = createAccount;
  }

  public Account getEditAccount() {
    return editAccount;
  }

  public void setEditAccount(Account editAccount) {
    this.editAccount = editAccount;
  }
}
