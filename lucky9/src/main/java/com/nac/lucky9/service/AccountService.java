/*
 *  * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nac.lucky9.service;

import com.nac.lucky9.Lucky9;
import com.nac.lucky9.entity.Account;
import java.io.Serializable;
import java.util.Calendar;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 *
 * @author nathaniel camomot <nathaniel.dtit@gmail.com>
 */
@Stateless
public class AccountService extends AbstractService<Account> implements Serializable {

  public AccountService() {
    super(Account.class);
  }

  public Account findAccountByEmail(String email) {
    EntityManager em = getEM();
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<Account> cq = cb.createQuery(Account.class);
    Root<Account> root = cq.from(Account.class);
    cq.select(root).where(cb.equal(root.get("email"), email));
    TypedQuery<Account> tq = em.createQuery(cq);

    List<Account> result = tq.getResultList();
    if (result.isEmpty()) {
      return null;
    } else {
      return result.get(0);
    }
  }

  public Account findAccountByActivationCode(String activationCode) {
    EntityManager em = getEM();
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<Account> cq = cb.createQuery(Account.class);
    Root<Account> root = cq.from(Account.class);
    cq.select(root).where(cb.equal(root.get("activationCode"), activationCode));
    TypedQuery<Account> tq = em.createQuery(cq);

    List<Account> result = tq.getResultList();
    if (result.isEmpty()) {
      return null;
    } else {
      return result.get(0);
    }
  }

  public List<Account> findBankerAccounts() {
    EntityManager em = getEM();
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<Account> cq = cb.createQuery(Account.class);
    Root<Account> root = cq.from(Account.class);
    cq.select(root).where(cb.equal(root.get("banker"), true));
    TypedQuery<Account> tq = em.createQuery(cq);
    return tq.getResultList();
  }

  public void savePlayers(List<Account> players, Long editAccountId) {
    EntityTransaction et = null;
    try {
      EntityManager em = getEM();
      et = em.getTransaction();
      et.begin();
      for (Account entity : players) {
        entity.setEditAccount(new Account(editAccountId));
        entity.setEditTime(Calendar.getInstance());
        em.merge(entity);
      }
      et.commit();
    } catch (Exception e) {
      Lucky9.errorInternal("on catch");
      Lucky9.errorInternal(e);
      if (et != null && et.isActive()) {
        et.rollback();
      }
      throw e;
    }
  }
}
