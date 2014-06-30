/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nac.lucky9.service;

import com.nac.lucky9.Lucky9;
import java.io.IOException;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Properties;
import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;

/**
 *
 * @author nathaniel camomot <nathaniel.dtit@gmail.com>
 */
@Singleton
@Startup
public class ConfigurationService implements Serializable{

  private static final String PROP_FILE = "conf.properties";
  private Properties properties;

  @PostConstruct
  public void init() {
    properties = new Properties();
    try {
      properties.load(getClass().getClassLoader().getResourceAsStream(PROP_FILE));
    } catch (IOException ex) {
      Lucky9.errorInternal(ex);
    }

    Enumeration e = properties.propertyNames();
    Lucky9.infoInternal("enum properties");
    while (e.hasMoreElements()) {
      String key = (String) e.nextElement();
      Lucky9.infoInternal("prop:" + key + " -- " + properties.getProperty(key));
    }
  }

  public String getString(String name) {
    String value = properties.getProperty(name);
    if (value == null) {
      Lucky9.errorInternal("Property not found");
    }
    return value;
  }

  public int getInt(String name) {
    String value = properties.getProperty(name);
    if (value == null) {
      Lucky9.errorInternal("Property not found");
    }
    return Integer.parseInt(value);
  }

  public Long getSuperUserId() {
    return new Long(getInt("super.user.id"));
  }
}
