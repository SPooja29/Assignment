package com.pooja.jdbc;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.derby.jdbc.EmbeddedDriver;

public class EmbeddedDatabaseDemo {

   public static void main(String[] args) {
      EmbeddedDatabaseDemo e =
         new EmbeddedDatabaseDemo();
      e.testDerby("Term from app");
   }
   public void testDerby(String name) {
	   int count = 0;
      Connection conn = null;	 
      PreparedStatement pstmt;
      Statement stmt;
      ResultSet rs = null;
      String createSQL = "create table term ("
      + "id integer not null generated always as"
      + " identity (start with 1000, increment by 1),"
      + "name varchar(30) not null,"
      + "constraint primary_key primary key (name))";

      try {
         Driver derbyEmbeddedDriver = new EmbeddedDriver();
         DriverManager.registerDriver(derbyEmbeddedDriver);
         conn = DriverManager.getConnection("jdbc:derby://localhost:1527/myDB;create=true;user=me;password=mine");
         conn.setAutoCommit(false);
         stmt = conn.createStatement();
         stmt.execute(createSQL);
		 
         pstmt = conn.prepareStatement("insert into term((name) values(?))");
         pstmt.setString(1, name);
         pstmt.executeUpdate();

         rs = stmt.executeQuery("select * from term");
         while (rs.next()) {
        	 count++;
            System.out.printf("%d %s \n",
            rs.getInt(1), rs.getString(2)
           );
         }
		 System.out.printf("The Size of the term table :" + count);
         stmt.execute("drop table term");

         conn.commit();

      } catch (SQLException ex) {
         System.out.println("in connection" + ex);
      }

      try {
         DriverManager.getConnection
            ("jdbc:derby:;shutdown=true");
      } catch (SQLException ex) {
         if (((ex.getErrorCode() == 50000) &&
            ("XJ015".equals(ex.getSQLState())))) {
               System.out.println("Derby shut down normally");
         } else {
            System.err.println("Derby did not shut down normally");
            System.err.println(ex.getMessage());
         }
      }
   }
}