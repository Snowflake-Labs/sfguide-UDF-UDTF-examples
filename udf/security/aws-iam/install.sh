#!/bin/bash

# Script to install a UDF that has been built by Bazel.  See README for details.

export LC_ALL=C.UTF-8
export LANG=C.UTF-8

set -eu
set -o pipefail

"${SNOWSQL_BINARY}" -a "${SNOWFLAKE_ACCOUNT}" -u "${SNOWFLAKE_USER}" --authenticator=externalbrowser <<EOF
USE ROLE ${SNOWFLAKE_ROLE};
USE WAREHOUSE ${SNOWFLAKE_WAREHOUSE};
USE DATABASE ${SNOWFLAKE_DATABASE};
USE SCHEMA ${SNOWFLAKE_SCHEMA};

PUT 
	file://$(pwd)/aws-iam/udf_deploy.jar
	@~/uarlos/
	auto_compress = true
	overwrite = true
	;

CREATE OR REPLACE FUNCTION 
	aws_list_granted_arns(policy VARCHAR, action VARCHAR) 
	RETURNS ARRAY
	LANGUAGE java
	IMPORTS = ('@~/uarlos/udf_deploy.jar')
	HANDLER='com.snowflake.redteam.uarlos.UDF.getAuthorizedArns'
	;

CREATE OR REPLACE FUNCTION 
	aws_list_granted_accounts(policy VARCHAR, action VARCHAR) 
	RETURNS ARRAY
	LANGUAGE java
	IMPORTS = ('@~/uarlos/udf_deploy.jar')
	HANDLER='com.snowflake.redteam.uarlos.UDF.getAuthorizedAccounts'
	;

CREATE OR REPLACE FUNCTION 
	aws_simulate_policies(policies OBJECT, action VARCHAR, resource VARCHAR, principal VARCHAR) 
	RETURNS OBJECT
	LANGUAGE java
	IMPORTS = ('@~/uarlos/udf_deploy.jar')
	HANDLER='com.snowflake.redteam.uarlos.UDF.simulatePolicies'
	;

EOF