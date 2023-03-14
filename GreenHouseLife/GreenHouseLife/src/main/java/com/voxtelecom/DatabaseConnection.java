package com.voxtelecom;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
	
	public Connection getConn() {
		String url = "jdbc:mysql://localhost:3306/greenhouse?useUnicode=yes&characterEncoding=UTF-8&useSSL=false&allowPublicKeyRetrieval=true";
		Connection con = null;
		System.out.println("Can you reach this?");
		try {
			con = DriverManager.getConnection(url, "root", "psycho");
			System.out.println("Did you get to this?");
		}catch(SQLException ex) {
			ex.printStackTrace();
		}
		return con;
	}
}