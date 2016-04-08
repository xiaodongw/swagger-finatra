#!/usr/bin/env bash
gradle clean uploadArchives -PscalaVersion=2.10.6 -PfinatraVersion=1.6.0
gradle clean uploadArchives -PscalaVersion=2.10.6 -PfinatraVersion=2.1.5
gradle clean uploadArchives -PscalaVersion=2.11.7 -PfinatraVersion=2.1.5
