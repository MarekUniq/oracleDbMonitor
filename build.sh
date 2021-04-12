#!/bin/bash
#

# gradle
if [[ -z "${GRADLE}" ]] ; then
  export GRADLE="../gradle/bin/gradle --no-daemon"
fi
echo  "# GRADLE=${GRADLE}"

#
# oracle environment (ORACLE_HOME)
if [[ -z "${ORACLE_HOME}" ]] ; then
  source "${HOME}/ora190.env"
fi
export ORACLE_HOME_190="${ORACLE_HOME}"
echo  "# ORACLE_HOME_190=${ORACLE_HOME_190}"

#
# java environment
echo  "${JAVA_HOME}"
if [[ -z "${JAVA_HOME}" ]] ; then
  export JAVA_HOME="${HOME}/tools/openjdk1.8"
fi
echo  "# JAVA_HOME=${JAVA_HOME}"


# clean
${GRADLE} clean
# build
${GRADLE} build

# end

