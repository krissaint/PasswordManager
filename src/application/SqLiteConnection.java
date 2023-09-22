package application;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author baona
 * Establish the connection to Sqlite database
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.swing.JOptionPane;
public class SqLiteConnection {
	private static String dbName = "acc.db";
	public static Connection connector() {
        try {
            Class.forName("org.sqlite.JDBC");
            String dbURL = "jdbc:sqlite:memory:"+dbName;
            Connection conn = DriverManager.getConnection(dbURL);
            return conn;
        } catch (ClassNotFoundException | SQLException e) {
          JOptionPane.showMessageDialog(null, e);
            return null;
        }		
	}
	public static String getDbName() {
		return dbName;
	}
	
}
