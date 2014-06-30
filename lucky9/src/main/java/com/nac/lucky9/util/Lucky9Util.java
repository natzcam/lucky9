/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nac.lucky9.util;

import com.nac.lucky9.Lucky9;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.xml.bind.DatatypeConverter;

/**
 * //https://www.owasp.org/index.php/Hashing_Java#Hardening_against_the_attacker.27s_attack
 *
 * @author nathaniel
 */
public class Lucky9Util {

  private final static int ITERATION = 1000;
  private static MessageDigest DIGEST;
  private static SecureRandom RANDOM;

  public static String getRandomStr(int length) {
    if (RANDOM == null) {
      try {
        RANDOM = SecureRandom.getInstance("SHA1PRNG");
      } catch (NoSuchAlgorithmException ex) {
        Lucky9.errorInternal(ex);
      }
    }
    byte[] b = new byte[length];
    RANDOM.nextBytes(b);
    return byteToBase64(b);
  }

  public static String getHash(String password, String salt) {
    if (DIGEST == null) {
      try {
        DIGEST = MessageDigest.getInstance("SHA-256");
      } catch (NoSuchAlgorithmException ex) {
        Lucky9.errorInternal(ex);
      }
    }
    byte[] input = null;
    try {
      DIGEST.reset();
      DIGEST.update(base64ToByte(salt));
      input = DIGEST.digest(password.getBytes("UTF-8"));
    } catch (UnsupportedEncodingException ex) {
      Lucky9.errorInternal(ex);
    }
    for (int i = 0; i < ITERATION; i++) {
      DIGEST.reset();
      input = DIGEST.digest(input);
    }
    return byteToBase64(input);
  }

  public static byte[] base64ToByte(String data) {
    return DatatypeConverter.parseBase64Binary(data);
  }

  public static String byteToBase64(byte[] data) {
    return DatatypeConverter.printBase64Binary(data);
  }

  public static String getServerUrl(HttpServletRequest request) {
    StringBuilder sb = new StringBuilder();
    String protocol = request.isSecure()? "https://": "http://";
    Lucky9.infoInternal("protocol" + protocol);
    sb.append(request.isSecure()? "https://": "http://");
    sb.append(request.getServerName());
    if (request.getServerPort() != 80) {
      sb.append(":");
      sb.append(request.getServerPort());
    }
    if(request.getContextPath().endsWith("/")){
       sb.append(request.getContextPath());
    }else{
      sb.append(request.getContextPath()).append("/");
    }
    return sb.toString();
  }
  
  public static void printConstraintViolations(ConstraintViolationException e){
     Set<ConstraintViolation<?>> constraintViolations = e.getConstraintViolations();
      for (ConstraintViolation<?> constraintViolation : constraintViolations) {
        Lucky9.errorInternal(constraintViolation.getPropertyPath().toString() + ":" + constraintViolation.getMessage());
      }
  }
}
