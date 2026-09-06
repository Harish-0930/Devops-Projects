#!/bin/bash

set -e

echo "========================================="
echo " Java 17 + Maven Installation"
echo "========================================="

echo
echo "[*] Updating package repositories..."
sudo apt update

echo
echo "[*] Installing Java 17 JDK..."
sudo apt install -y openjdk-17-jdk

echo
echo "[*] Installing Maven..."
sudo apt install -y maven

echo
echo "[*] Configuring JAVA_HOME..."

JAVA_HOME_PATH="/usr/lib/jvm/java-17-openjdk-amd64"

if [ -d "$JAVA_HOME_PATH" ]; then
    echo "export JAVA_HOME=$JAVA_HOME_PATH" >> ~/.bashrc
    echo 'export PATH=$JAVA_HOME/bin:$PATH' >> ~/.bashrc

    export JAVA_HOME="$JAVA_HOME_PATH"
    export PATH="$JAVA_HOME/bin:$PATH"
else
    echo "[X] Java 17 installation directory not found."
    exit 1
fi

echo
echo "========================================="
echo " Installation Verification"
echo "========================================="

echo
echo "[*] Java version:"
java -version

echo
echo "[*] Maven version:"
mvn -version

echo
echo "========================================="
echo " Java 17 + Maven Installation Completed!"
echo "========================================="