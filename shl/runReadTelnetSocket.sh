#!/bin/bash
#
# common environment
SHLDIR="$(dirname "$0")"               # same as script directory
PROJECTDIR="$(dirname "${SHLDIR}")"    # one level up from script directory.

#
# java environment
if [[ -z "${JAVA_HOME}" ]] ; then
  JAVA_HOME="${HOME}/tools/jdk1.8"
fi

#
# start
"${JAVA_HOME}/bin/java" -cp "${PROJECTDIR}/build/libs/oracleDbMonitor-1.0.jar" oracleDbMonitor/ReadTelnetSocket "$@"
