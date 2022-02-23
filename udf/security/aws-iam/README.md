# AWS IAM UDF

This directory contains a series of Snowflake UDFs for working with AWS IAM policy documents.  The full use of these UDFs is described in this [series of blog posts](https://medium.com/@greg.harris.snowflake/analyzing-aws-iam-relationships-using-snowflake-d97776792c9).

This code is offered as an example with **no support**.  If you do run into issues, please submit them using the Github Issue tracking system and we will try our best to help.

## Building

This repository uses Bazel as its build system.  Please reference the upstream [Installing Bazel](https://docs.bazel.build/versions/main/install.html) documentation for how to setup Bazel on your system.

Once Bazel has been installed you can build and test the code with the `//aws-iam:udf_test` target as shown below:

```bash
$ bazel test //aws-iam:udf_test
INFO: Analyzed target //aws-iam:udf_test (5 packages loaded, 170 targets configured).
...
```

## Installation

The `//aws-iam:install` target will build and install the UDF into your Snowflake instance.  Before you can do so you must set the following environment variables:

| Environment Variable | Usage                                                         |
| -------------------- | ------------------------------------------------------------- |
| SNOWSQL_BINARY       | Path to the snowsql binary on your local system               |
| SNOWFLAKE_ACCOUNT    | The Snowflake account to install the UDF into                 |
| SNOWFLAKE_USER       | The Snowflake user to install the UDF under                   |
| SNOWFLAKE_DATABASE   | The Snowflake database to install the UDF under               |
| SNOWFLAKE_ROLE       | The Snowflake role to install the UDF under                   |
| SNOWFLAKE_SCHEMA     | The Snowflake schema to install the UDF under                 |
| SNOWFLAKE_WAREHOUSE  | A valid Snowflake warehouse to use to verify the installation |

Please see the [upstream documentation](https://docs.snowflake.com/en/user-guide/snowsql-install-config.html) for how to install SnowSQL on your machine.

You can invoke the `//aws-iam:install` target as shown below:

```bash
$ bazel run //aws-iam:install
INFO: Analyzed target //aws-iam:install (0 packages loaded, 4 targets configured).
...
```

## Querying

For examples on how to query the UDF please see our [blog post](https://medium.com/@greg.harris.snowflake/analyzing-aws-iam-relationships-using-snowflake-d97776792c9).