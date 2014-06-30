package com.nac.lucky9.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import com.ibm.nosql.json.api.BasicDBList;
import com.ibm.nosql.json.api.BasicDBObject;
import com.ibm.nosql.json.util.JSON;
import javax.persistence.EntityManager;

public class DBUtil {

  private static EntityManagerFactory emf = null;

  private static void createEntityManagerFactory() {
    String driver = "org.apache.derby.jdbc.ClientDriver";
    String user = "app";
    String password = "app";
    String databaseUrl = "jdbc:derby://localhost:1527/sample";

    String VCAP_SERVICES = System.getenv("VCAP_SERVICES");
    if (VCAP_SERVICES != null) {

      System.out.println("VCAP_SERVICES content: " + VCAP_SERVICES);

      BasicDBObject obj = (BasicDBObject) JSON.parse(VCAP_SERVICES);
      String thekey = null;
      Set<String> keys = obj.keySet();

      for (String eachkey : keys) {
        if (eachkey.contains("SQLDB-1.0")) {
          thekey = eachkey;
        }
      }

      BasicDBList list = (BasicDBList) obj.get(thekey);
      obj = (BasicDBObject) list.get("0");
      obj = (BasicDBObject) obj.get("credentials");
      String databaseHost = (String) obj.get("host");
      String databaseName = (String) obj.get("db");
      Integer port = (Integer) obj.get("port");
      user = (String) obj.get("username");
      password = (String) obj.get("password");
      databaseUrl = "jdbc:db2://" + databaseHost + ":" + port + "/" + databaseName;
      driver = "com.ibm.db2.jcc.DB2Driver";

      System.out.println(databaseUrl);

    } else {
      System.out.println("VCAP_SERVICES is null. Fallback to connect local connection.");
    }

    // programmatically create a JPA EntityManager
    Map<String, Object> properties = new HashMap<>();
    properties.put("javax.persistence.jdbc.driver", driver);
    properties.put("javax.persistence.jdbc.url", databaseUrl);
    properties.put("javax.persistence.jdbc.user", user);
    properties.put("javax.persistence.jdbc.password", password);
    // "openjpa-todo" was defined at persistence.xml
    emf = Persistence.createEntityManagerFactory(
            "lucky9PU", properties);
  }

  public static EntityManager createEntityManager() {
    if (emf == null) {
      createEntityManagerFactory();
    }
    return emf.createEntityManager();
  }
}
