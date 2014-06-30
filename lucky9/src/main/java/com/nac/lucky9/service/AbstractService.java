/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nac.lucky9.service;

import com.nac.lucky9.Lucky9;
import com.nac.lucky9.entity.Account;
import com.nac.lucky9.entity.BaseEntity;
import com.nac.lucky9.util.DBUtil;
import com.nac.lucky9.util.Lucky9Util;
import java.util.Calendar;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.validation.ConstraintViolationException;

/**
 *
 * @author nathaniel camomot <nathaniel.dtit@gmail.com>
 * @param <T>
 */
public abstract class AbstractService<T extends BaseEntity> {

  private final Class<T> entityClass;
  private EntityManager entityManager = null;

  public AbstractService(Class<T> entityClass) {
    this.entityClass = entityClass;
  }

  protected EntityManager getEM() {
    if (entityManager == null) {
      entityManager = DBUtil.createEntityManager();
    }
    return entityManager;
  }

  public void create(T entity, Long createAccountId) throws Exception {
    EntityManager em = getEM();
    EntityTransaction et = em.getTransaction();
    try {
      entity.setCreateAccount(new Account(createAccountId));
      entity.setCreateTime(Calendar.getInstance());
      et.begin();
      em.persist(entity);
      et.commit();
    } catch (ConstraintViolationException e) {
      Lucky9Util.printConstraintViolations(e);
      throw e;
    } catch (Exception e) {
      Lucky9.errorInternal(e);
      if (et.isActive()) {
        et.rollback();
      }
      throw e;
    }
  }

  public void edit(T entity, Long editAccountId) throws Exception {
    EntityManager em = getEM();
    EntityTransaction et = em.getTransaction();
    try {
      entity.setEditAccount(new Account(editAccountId));
      entity.setEditTime(Calendar.getInstance());
      et.begin();
      em.merge(entity);
      et.commit();
    } catch (Exception e) {
      Lucky9.errorInternal(e);
      if (et.isActive()) {
        et.rollback();
      }
      throw e;
    }
  }

  public void remove(T entity) throws Exception {
    EntityManager em = getEM();
    EntityTransaction et = em.getTransaction();
    try {
      et.begin();
      entity = em.merge(entity);
      em.remove(entity);
      et.commit();
    } catch (Exception e) {
      Lucky9.errorInternal(e);
      if (et.isActive()) {
        et.rollback();
      }
      throw e;
    }
  }

  public void refresh(T entity) {
    EntityManager em = getEM();
    em.refresh(entity);
  }

  public T find(Object id) {
    return getEM().find(entityClass, id);
  }

  public List<T> findAll() {
    EntityManager em = getEM();
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<T> cq = cb.createQuery(entityClass);
    Root<T> root = cq.from(entityClass);
    cq.select(root);
    return em.createQuery(cq).getResultList();
  }

  public List<T> findRange(int first, int max) {
    EntityManager em = getEM();
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<T> cq = cb.createQuery(entityClass);
    Root<T> root = cq.from(entityClass);
    cq.select(root);
    TypedQuery<T> q = em.createQuery(cq);
    q.setMaxResults(max);
    q.setFirstResult(first);
    return q.getResultList();
  }

  public int count() {
    EntityManager em = getEM();
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<Long> cq = cb.createQuery(Long.class);
    Root<Long> root = cq.from(Long.class);
    cq.select(cb.count(root));
    TypedQuery<Long> q = em.createQuery(cq);
    return q.getSingleResult().intValue();
  }
}
