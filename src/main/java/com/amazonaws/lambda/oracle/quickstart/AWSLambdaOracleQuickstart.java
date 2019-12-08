// Copyright 2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0

package com.amazonaws.lambda.oracle.quickstart;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

/**
 * 
 * This is a sample Lambda class with handler function. It performs the
 * following steps: 1. Connect to AWS Secrets Manager and fetch secret value
 * using a secret name 2. Parse secrets value to Database username, password and
 * other values 3. Connect to an Oracle Database - actual business logic is left
 * to the user to try.
 * 
 * Important Note 1: Do not print confidential information (e.g. database
 * credentials) to CloudWatch console
 * 
 * Important Note 2: Do not return confidential information from handler
 * function.
 * 
 * @author Ravi Itha, Amazon Web Services, Inc.
 *
 */
public class AWSLambdaOracleQuickstart implements RequestHandler<Object, String> {

	@Override
	public String handleRequest(Object input, Context context) {

		context.getLogger().log("Input: " + input);

		String region = Optional.ofNullable(System.getenv("region")).orElse("");
		String secretName = Optional.ofNullable(System.getenv("database_secret_name")).orElse("my_oracle_database");
		String query = "select * from table_name";

		Connection connection = null;
		DatabaseCredentials dbCreds = null;
		SecretsManagerUtil smUtil = new SecretsManagerUtil();
		String secretString = smUtil.getSecretUsingSecretsCache(region, secretName);

		if (Optional.ofNullable(secretString).isPresent()) {
			dbCreds = smUtil.parseSecretString(secretString);
		}
		if (Optional.ofNullable(dbCreds.getUserName()).isPresent()
				&& Optional.ofNullable(dbCreds.getPassword()).isPresent()
				&& Optional.ofNullable(dbCreds.getDbHost()).isPresent()
				&& Optional.ofNullable(dbCreds.getDbName()).isPresent()
				&& Optional.ofNullable(dbCreds.getDbPort()).isPresent()) {
			DatabaseUtil dbUtil = new DatabaseUtil();
			connection = dbUtil.getConnection(dbCreds);
		}
		if (Optional.ofNullable(connection).isPresent()) {
			runSampleQuery(connection, query);
		}
		return "Lambda function to get a list of Databases completed successfully!";
	}

	/**
	 * This method will run sample query against Oracle Database.
	 * 
	 * @param connection
	 * @param query
	 */
	public void runSampleQuery(Connection connection, String query) {
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		try {
			prepStmt = connection.prepareStatement(query);
			// prepStmt.setString(1, "value");
			// prepStmt.setString(2, "value");
			rs = prepStmt.executeQuery();
			while (rs.next()) {
				// do something
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}