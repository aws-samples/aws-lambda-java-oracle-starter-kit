// Copyright 2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0

package com.amazonaws.lambda.oracle.quickstart;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseUtil {

	public Connection getConnection(DatabaseCredentials dbCreds) {
		Connection connection = null;
		String url = "jdbc:oracle:thin:@" + dbCreds.getDbHost() + ":" + dbCreds.getDbPort() + ":" + dbCreds.getDbName();
		try {
			connection = DriverManager.getConnection(url, dbCreds.getUserName(), dbCreds.getPassword());
			System.out.println("Created connection object successfully. Object: " + connection);
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Could not get a connection to database.");
		}
		return connection;
	}
}
