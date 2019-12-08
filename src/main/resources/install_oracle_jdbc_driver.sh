# Copyright 2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: MIT-0

# this shell script needs one argument which is the fully qualified oracle jdbc driver e.g. ojdbc7.jar

set -x
export LIB_PATH=$1
echo "Deploying Oracle JDBC driver (jar) to local Maven repository"
mvn install:install-file -Dfile=$LIB_PATH -DgroupId=com.oracle.jdbc -DartifactId=ojdbc -Dversion=7 -Dpackaging=jar