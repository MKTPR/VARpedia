#!/bin/bash
sudo apt install openjdk-11-jdk
sudo update-java-alternatives -s java-1.11.0-openjdk-amd64
/usr/bin/java --module-path /home/student/Downloads/openjfx-13-rc+2_linux-x64_bin-sdk/javafx-sdk-13/lib --add-modules javafx.base,javafx.controls,javafx.media,javafx.graphics,javafx.fxml -jar VARpedia-release.jar




