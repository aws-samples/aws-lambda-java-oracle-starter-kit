// Copyright 2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0

package com.amazonaws.lambda.oracle.quickstart;

import java.util.Base64;
import java.util.Optional;

import com.amazonaws.lambda.oracle.quickstart.DatabaseCredentials;
import com.amazonaws.secretsmanager.caching.SecretCache;
import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder;
import com.amazonaws.services.secretsmanager.model.DecryptionFailureException;
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest;
import com.amazonaws.services.secretsmanager.model.GetSecretValueResult;
import com.amazonaws.services.secretsmanager.model.InternalServiceErrorException;
import com.amazonaws.services.secretsmanager.model.InvalidParameterException;
import com.amazonaws.services.secretsmanager.model.InvalidRequestException;
import com.amazonaws.services.secretsmanager.model.ResourceNotFoundException;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

public class SecretsManagerTest {

	/**
	 * Important Note: Do not try to print confidential information (e.g. database
	 * credentials) to CloudWatch console.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		String secretString = getSecretUsingSecretsCache("us-east-1", "secrets_demo");
		if (Optional.ofNullable(secretString).isPresent()) {
			DatabaseCredentials dbCreds = parseSecretString(secretString);
			System.out.printf("Username: %s, Host: %s, Port: %s, DatabaseName: %s \n",
					dbCreds.getUserName(), dbCreds.getDbHost(), dbCreds.getDbPort(),
					dbCreds.getDbName());
		} else {
			System.out.println("Secret String is null. Will not parse it.");
		}

		String secretString2 = getSecretUsingSecretsManager("us-east-1", "secrets_demo");
		if (Optional.ofNullable(secretString2).isPresent()) {
			DatabaseCredentials dbCreds2 = parseSecretString(secretString2);
			System.out.printf("Username: %s, Host: %s, Port: %s, DatabaseName: %s \n",
					dbCreds2.getUserName(), dbCreds2.getDbHost(), dbCreds2.getDbPort(),
					dbCreds2.getDbName());
		} else {
			System.out.println("Secret String is null. Will not parse it.");
		}
	}

	/**
	 * Parse Secret Value from AWS Secrets Manager using Secrets Managers Client
	 * Builder See
	 * https://docs.aws.amazon.com/secretsmanager/latest/apireference/API_GetSecretValue.html
	 * 
	 * @param region
	 * @param secretName
	 */
	public static String getSecretUsingSecretsManager(String region, String secretName) {

		String secret = null;
		GetSecretValueResult getSecretValueResult = null;
		AWSSecretsManager client = AWSSecretsManagerClientBuilder.standard().withRegion(region).build();
		GetSecretValueRequest getSecretValueRequest = new GetSecretValueRequest().withSecretId(secretName);
		try {
			getSecretValueResult = client.getSecretValue(getSecretValueRequest);
			// Decrypts secret using the associated KMS CMK. Depending on whether the secret
			// is a string or binary, one of these fields will be populated.
			if (getSecretValueResult.getSecretString() != null) {
				secret = getSecretValueResult.getSecretString();
			} else {
				secret = new String(Base64.getDecoder().decode(getSecretValueResult.getSecretBinary()).array());
			}
		} catch (DecryptionFailureException e) {
			// Secrets Manager can't decrypt the protected secret text using the provided
			// KMS key.
			e.printStackTrace();
		} catch (InternalServiceErrorException e) {
			// An error occurred on the server side.
			e.printStackTrace();
		} catch (InvalidParameterException e) {
			// You provided an invalid value for a parameter.
			e.printStackTrace();
		} catch (InvalidRequestException e) {
			// You provided a parameter value that is not valid for the current state of the
			// resource.
			e.printStackTrace();
		} catch (ResourceNotFoundException e) {
			// We can't find the resource that you asked for.
			e.printStackTrace();
			System.out.println("No database credentials found with the provided secret name which is: " + secretName);
		}
		return secret;
	}

	/**
	 * Parse Secret Value from AWS Secrets Manager using Secrets Cache Java Library
	 * 
	 * @param region
	 * @param secretName
	 * @return
	 */
	public static String getSecretUsingSecretsCache(String region, String secretName) {
		String secret = null;
		SecretCache cache = new SecretCache(AWSSecretsManagerClientBuilder.standard().withRegion(region));
		try {
			secret = cache.getSecretString(secretName);
			cache.close();
		} catch (ResourceNotFoundException e) {
			e.printStackTrace();
			System.out.println("No database credentials found with the provided secret name which is: " + secretName);
		}
		return secret;
	}

	/**
	 * Parse Secret Value to Database credentials
	 * 
	 * @param secretString
	 * @return
	 */
	public static DatabaseCredentials parseSecretString(String secretString) {
		Gson gson = new Gson();
		DatabaseCredentials dbCreds = new DatabaseCredentials();

		if (!secretString.equalsIgnoreCase("")) {
			try {
				JsonElement element = gson.fromJson(secretString, JsonElement.class);
				JsonObject jsonObject = element.getAsJsonObject();
				dbCreds.setUserName(jsonObject.get("username").getAsString());
				dbCreds.setPassword(jsonObject.get("password").getAsString());
				dbCreds.setDbHost(jsonObject.get("host").getAsString());
				dbCreds.setDbPort(jsonObject.get("port").getAsString());
				dbCreds.setDbName(jsonObject.get("dbname").getAsString());
			} catch (JsonSyntaxException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("Could not parse Databse Credentials from AWS Secrets Manager.");
		}
		return dbCreds;
	}
}
