# AWS Lambda using Java, Oracle Database, and AWS Secrets Manager - Starter Kit

This is a Starter Kit for developing AWS Lambda based applications to work Oracle database using AWS Secrets Manager. 

## Pre-requisites
1. Apache Maven
2. Java SDK

## AWS Service Requirements
This quick start requires the following AWS services
1. AWS Lambda function
2. AWS Secrets Manager Secret

## Oracle JDBC driver Installation
1. Download Oracle driver for Java from Oracle website https://www.oracle.com/database/technologies/jdbc-drivers-12c-downloads.html. Select the appropriate driver. For e.g. ojdbc.jar
2. Install Oracle Driver to local Maven Environment using one of the scripts provided below
	1. For Mac OS or Unix or Linux - [install_oracle_jdbc_driver.sh](./src/main/resources/install_oracle_jdbc_driver.sh)
	2. For Windows - [install_oracle_jdbc_driver.bat](./AWSLambda-Java-Oracle-Quickstart/src/main/resources/install_oracle_jdbc_driver.bat)
3. Run the installation script. E.g. 
	```
	./install_oracle_jdbc_driver.sh /Users/user_name/Downloads/ojdbc7.jar
	```
4. Expected Output: it installs the jar file to local Maven repository path e.g. ```~/.m2/repository/com/oracle/jdbc/ojdbc/7/```
5. Note: The JDBC jar file can be referenced in the POM.xml using the following declaration
	```
	<!-- Dependency for Oracle JDBC driver -->
	<dependency>
		<groupId>com.oracle.jdbc</groupId>
		<artifactId>ojdbc</artifactId>
		<version>7</version>
	</dependency>
	```

## Build Instructions
1. The source code has Maven nature, you can build it using standard Maven commands e.g. ```mvn -X clean install```. or use the options available in your IDE
2. The above step generates a Jar file e.g. AWSLambda-Java-Oracle-Quickstart-1.0.jar

## Deploy Instructions
1. Setup AWS Secrets Manager Secret. This secrets has the credentials required to connect to Oracle Database
	1. Step 1 
	![Alt](./src/test/resources/Step_one.png)
	2. Step 2 
	![Alt](./src/test/resources/Step_two.png)
	![Alt](./src/test/resources/Step_three.png)
	4. Step 3 
	![Alt](./src/test/resources/Step_four.png)
	5. Step 4 
	![Alt](./src/test/resources/Step_five.png)
	6. Step 5 
	![Alt](./src/test/resources/Step_six.png)
2. Create Lambda Execution IAM Role and attach it to the Lambda function deployed.
3. Deploy **AWSLambdaOracleQuickstart** function
   	1. Runtime = Java 8
   	1. Function package = Use the Jar file generated. Refer section [Build Instructions](#Build-Instructions)
   	2. Lambda Handler = ```com.amazonaws.lambda.oracle.quickstart.AWSLambdaOracleQuickstart```
   	3. Timeout = e.g. 1 minute
	4. Memory = e.g. 128 MB	
	5. Execution Role = created in Step # 2	
	6. Environment variable = as defined in the following table

	| Variable Name                    	| Variable Value          					|
	|----------------------------------	|-------------------------					|
	| region                           	| e.g. us-east-1               				|
	| database_secret_name 				| Name of the AWS Secrets Manager Secret     |


## Best Practices
For the brevity of this article, I intentionally did not discuss additional security topics and best practices. However, security is an important requirement for developing applications on AWS. The following resources may be useful.

1.	It is important to encrypt connection to a database. Refer the following resources from AWS 
	1. [Oracle Security](https://docs.aws.amazon.com/AmazonRDS/latest/UserGuide/CHAP_Oracle.html#Oracle.Concepts.RestrictedDBAPrivileges)
	2. [Oracle Secure Sockets Layer](https://docs.aws.amazon.com/AmazonRDS/latest/UserGuide/Appendix.Oracle.Options.SSL.html) for more details.
2.	Refer c more details around this topic.
	1. [AWS Secrets Manager Best Practices](https://docs.aws.amazon.com/secretsmanager/latest/userguide/best-practices.html) 
	2. [Security in AWS Lambda](https://docs.aws.amazon.com/lambda/latest/dg/lambda-security.html)
	3. [Security Overview of AWS Lambda](https://d1.awsstatic.com/whitepapers/Overview-AWS-Lambda-Security.pdf)
3.	We’ve discussed a process of installing Oracle JDBC driver in a local environment. However, for production use cases, this model will not be practical. A better of maintaining third party libraries that are not available on Maven central repository is using Repository Manager within your organization. Refer Apache Maven's documentation for more details [here](https://maven.apache.org/repository-management.html)

## License Summary
This sample code is made available under the MIT license. See the LICENSE file.