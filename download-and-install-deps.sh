#!/bin/bash

# This script downloads and installs the maven dependencies that are currently
# not yet hosted in a repository. Run this if you get unknown dependency errors.

set -o nounset
set -o errexit
set -o pipefail

readonly jenkins_base="https://jenkins-2.sse.uni-hildesheim.de/view/Teaching/job/Teaching_StudentMgmt-Backend-API-Gen/lastSuccessfulBuild/artifact"

readonly backend_jar="${jenkins_base}/API/target/StudentMgmt-Backend-API.jar"
readonly backend_src="${jenkins_base}/API/target/StudentMgmt-Backend-API-src.jar"
readonly backend_pom="${jenkins_base}/pom.xml"

curl -LO "${backend_jar}"
curl -LO "${backend_src}"
curl -LO "${backend_pom}"

mvn install:install-file -Dfile="StudentMgmt-Backend-API.jar" -DpomFile="pom.xml" -Dsources="StudentMgmt-Backend-API-src.jar"



readonly sparky_jar="${jenkins_base}/Sparky/target/Sparky-API.jar"
readonly sparky_src="${jenkins_base}/Sparky/target/Sparky-API-src.jar"
readonly sparky_pom="${jenkins_base}/pom-Sparky.xml"

curl -LO "${sparky_jar}"
curl -LO "${sparky_src}"
curl -LO "${sparky_pom}"

mvn install:install-file -Dfile="Sparky-API.jar" -DpomFile="pom-Sparky.xml" -Dsources="Sparky-API-src.jar"
