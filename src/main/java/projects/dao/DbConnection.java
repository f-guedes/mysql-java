package projects.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import projects.exception.DbException;

public class DbConnection {
  private static final String USERNAME = "projects";
  private static final String PASSWORD = "projects";
  private static final String SCHEMA = "projects";
  private static final String HOST = "localhost";
  private static final int PORT = 3306;
  
  public static Connection getConnection() {
    String uri = String.format("jdbc:mysql://%s:%d/%s", HOST, PORT, SCHEMA);
    
    try {
      Connection conn = DriverManager.getConnection(uri, USERNAME, PASSWORD);
      System.out.println("You're connected to " + uri);
      return conn;
    } catch (SQLException e) {
      String message = "Connection failed at " + uri;
      System.out.println(message);
      throw new DbException(message);
    }
  }

}
