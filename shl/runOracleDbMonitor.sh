#!/bin/bash
#
# common environment
SHLDIR="$(dirname "$0")"               # same as script directory
PROJECTDIR="$(dirname "${SHLDIR}")"    # one level up from script directory.

#
# oracle environment (ORACLE_HOME)
if [[ -z "${ORACLE_HOME}" ]] ; then
  source "${HOME}/ora190.env"
fi

#
# java environment
if [[ -z "${JAVA_HOME}" ]] ; then
  JAVA_HOME="${HOME}/tools/jdk1.8"
fi

#
# start
"${JAVA_HOME}/bin/java" -cp "${PROJECTDIR}/build/libs/oracleDbMonitor-1.0.jar:${ORACLE_HOME}/jdbc/lib/ojdbc8.jar" oracleDbMonitor/OracleDbMonitor "$@"
