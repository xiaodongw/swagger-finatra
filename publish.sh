#!/usr/bin/env bash
gradle clean uploadArchives -PscalaVersion=2.10.6 -PfinatraVersion=2.2.0
gradle clean uploadArchives -PscalaVersion=2.11.7 -PfinatraVersion=2.2.0
