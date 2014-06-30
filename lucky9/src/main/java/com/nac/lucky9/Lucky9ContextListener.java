package com.nac.lucky9;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import com.nac.lucky9.entity.Account;
import com.nac.lucky9.service.AccountService;
import com.nac.lucky9.service.ConfigurationService;
import com.nac.lucky9.util.DBUtil;
import com.nac.lucky9.util.Lucky9Util;
import java.util.Calendar;
import javax.ejb.EJB;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * Web application lifecycle listener.
 *
 * @author camomon
 */
@WebListener()
public class Lucky9ContextListener implements ServletContextListener {

  @EJB
  private AccountService accountService;
  @EJB
  private ConfigurationService configService;

  @Override
  public void contextInitialized(ServletContextEvent sce) {
    createSuperUser();
  }

  public void createSuperUser() {

    EntityManager em = DBUtil.createEntityManager();
    EntityTransaction et = em.getTransaction();
    try {

      Account superUser = accountService.find(configService.getSuperUserId());

      if (superUser == null) {
        et.begin();
        Query insertSuperUser = em.createNativeQuery("INSERT INTO account (id,email,name,credit,banker,password,salt,activation_code,activation_code_expires,activated,create_time,create_account_id) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)");
        insertSuperUser.setParameter(1, configService.getInt("super.user.id"));
        insertSuperUser.setParameter(2, configService.getString("super.user.email"));
        insertSuperUser.setParameter(3, configService.getString("super.user.name"));
        insertSuperUser.setParameter(4, configService.getInt("super.user.credit"));
        insertSuperUser.setParameter(5, true);
        String salt = Lucky9Util.getRandomStr(configService.getInt("salt.size"));
        insertSuperUser.setParameter(6, Lucky9Util.getHash(configService.getString("super.user.password"), salt));
        insertSuperUser.setParameter(7, salt);
        String ac = Lucky9Util.getRandomStr(30);
        insertSuperUser.setParameter(8, ac);
        insertSuperUser.setParameter(9, Calendar.getInstance());
        insertSuperUser.setParameter(10, true);
        insertSuperUser.setParameter(11, Calendar.getInstance());
        insertSuperUser.setParameter(12, configService.getInt("super.user.id"));
        insertSuperUser.executeUpdate();
        et.commit();
      }

    } catch (Exception e) {
      Lucky9.errorInternal(e);
      if (et.isActive()) {
        et.rollback();
      }
    }
  }

  @Override
  public void contextDestroyed(ServletContextEvent sce) {

  }
}
