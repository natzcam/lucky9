/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nac.lucky9.entity;

import com.nac.lucky9.game.Card;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author nathaniel camomot <nathaniel.dtit@gmail.com>
 *
 */
@Entity
@Table(name = "account")
public class Account extends BaseEntity implements Serializable {

  @NotNull
  @Size(min = 0, max = 255)
  @Column(name = "email", nullable = false, unique = true, length = 255)
  private String email;
  @NotNull
  @Size(min = 0, max = 255)
  @Column(name = "name", nullable = false, length = 255)
  private String name;
  @NotNull
  @Size(min = 0, max = 255)
  @Column(name = "password", nullable = false, length = 255)
  private String password;
  @NotNull
  @Size(min = 0, max = 255)
  @Column(name = "activation_code", nullable = false, length = 255)
  private String activationCode;
  @NotNull
  @Column(name = "activation_code_expires", nullable = false)
  @Temporal(TemporalType.TIMESTAMP)
  private Calendar activationCodeExpires;
  @Column(name = "activated", nullable = false)
  private boolean activated = false;
  @NotNull
  @Size(min = 0, max = 255)
  @Column(name = "salt", nullable = false, length = 255)
  private String salt;
  @Column(name = "banker", nullable = false)
  private boolean banker = false;
  @Column(name = "credit", nullable = false)
  private int credit = 0;
  @Transient
  private int bet = 0;
  @Transient
  private int win = 0;
  @Transient
  private boolean hit = false;
  @Transient
  private List<Card> cards = new ArrayList<>();

  public Account() {
  }

  public boolean isHit() {
    return hit;
  }

  public void setHit(boolean hit) {
    this.hit = hit;
  }

  public void setWin(int win) {
    this.win = win;
  }
  
  public void addWin(int win) {
    this.win += win;
  }

  public int getWin() {
    return win;
  }

  public boolean isBanker() {
    return banker;
  }

  public void setBanker(boolean banker) {
    this.banker = banker;
  }

  public String getName() {
    if (name == null) {
      name = email;
    }
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void addCredit(int value) {
    credit += value;
  }

  public int getScore() {
    int score = 0;
    for (Card card : cards) {
      score += card.getType().value();
    }
    return score % 10;
  }

  public String getScoreBreakdown() {
    StringBuilder breakdown = new StringBuilder();
    int total = 0;
    boolean f = false;
    for (Card card : cards) {
      if (f) {
        breakdown.append(" + ");
      }
      f = true;
      int pt = card.getType().value();
      breakdown.append(pt);
      total += pt;
    }
    if (total > 0) {
      breakdown.append(" = ").append(total);
    }
    return breakdown.toString();
  }

  public int getBet() {
    return bet;
  }

  public int getCredit() {
    return credit;
  }

  public List<Card> getCards() {
    return cards;
  }

  public void setBet(int bet) {
    this.bet = bet;
  }

  public Account(Long id) {
    this.id = id;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
    if (name == null) {
      name = email;
    }
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getActivationCode() {
    return activationCode;
  }

  public void setActivationCode(String activationCode) {
    this.activationCode = activationCode;
  }

  public boolean isActivated() {
    return activated;
  }

  public void setActivated(boolean activated) {
    this.activated = activated;
  }

  public String getSalt() {
    return salt;
  }

  public void setSalt(String salt) {
    this.salt = salt;
  }

  public Calendar getActivationCodeExpires() {
    return activationCodeExpires;
  }

  public void setActivationCodeExpires(Calendar activationCodeExpires) {
    this.activationCodeExpires = activationCodeExpires;
  }

  @Override
  public int hashCode() {
    int hash = 5;
    hash = 59 * hash + Objects.hashCode(this.id);
    return hash;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final Account other = (Account) obj;

    return Objects.equals(this.id, other.id);
  }

  @Override
  public String toString() {
    return "Account[" + id + "]";
  }
}
