package projects.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import projects.exception.DbException;

/*
 * This class is used to establish a connection with the projects database
 */

public class DbConnection {

	private static String HOST = "localhost";
	private static String PASSWORD = "projectspass";
	private static int PORT = 3306;
	private static String SCHEMA = "projects";
	private static String USER = "projects";

	public static Connection getConnection() {
		// Creates the url used to get a connection
		String url = String.format("jdbc:mysql://%s:%d/%s?user=%s&password=%s&useSSL=false", HOST, PORT, SCHEMA, USER,
				PASSWORD);

		try {
			// Establishes a connection with the database
			Connection conn = DriverManager.getConnection(url);
			System.out.println("Connection Successful!");
			return conn;

			// If there is a SQLException, it throws a DbException. The DbException class turns a
			// checked exception into an unchecked exception.
		} catch (SQLException e) {
			System.out.println("Connection Failed.");
			throw new DbException(e);
		}
	}

}
