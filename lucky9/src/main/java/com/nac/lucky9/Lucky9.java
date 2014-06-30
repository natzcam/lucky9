/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nac.lucky9;

import org.omnifaces.util.Messages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author nathaniel
 */
public class Lucky9 {

  private static final Logger LOGGER = LoggerFactory.getLogger(Lucky9.class);

  public static void error(Throwable e) {
    Messages.addGlobalError(e.getMessage());
    LOGGER.error(e.getMessage(), e);
  }

  public static void error(String msg) {
    Messages.addGlobalError(msg);
    LOGGER.error(msg);
  }

  public static void info(String info) {
    Messages.addGlobalInfo(info);
    LOGGER.info(info);   
  }

  public static void error(String clientId, Throwable e) {
    Messages.addGlobalError(clientId, e.getMessage());
    LOGGER.error(e.getMessage(), e);
  }

  public static void error(String clientId, String msg) {
    Messages.addGlobalError(clientId, msg);
    LOGGER.error(msg);
  }

  public static void info(String clientId, String info) {
    Messages.addGlobalInfo(clientId, info);
    LOGGER.info(info);
  }

  public static void errorInternal(Throwable e) {
    LOGGER.error(e.getMessage(), e);
  }

  public static void errorInternal(String msg) {
    LOGGER.error(msg);
  }

  public static void infoInternal(String info) {
    LOGGER.info(info);
  }
}
