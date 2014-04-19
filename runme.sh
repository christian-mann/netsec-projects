#!/bin/bash

color()(set -o pipefail;"$@" 2>&1>&3|sed $'s,.*,\e[31m&\e[m,'>&2)3>&1
color java -classpath 'bin:lib/argparse4j.jar:lib/PackagedPacketDriver.jar:adapter/SimplePacketDriver.jar:lib/jnr-netdb-1.1.1.jar:lib/javatuples-1.2.jar' -D'java.library.path'='adapter' edu.mann.netsec.$*
