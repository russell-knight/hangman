#!/usr/bin/env bash
# The purpose of this script is to launch a server for the hangman game
# it takes a port number as a command-line argument and attempts to
# start a server on that port.


if [[ "$#" -ne 1 ]]
then
  echo "Usage: ./startServer.sh <port number>"
  echo "where <port number> is a valid port number (from 1024 to 49151)"
  exit 1
fi
portNumber="$1"
if ! [[ "$portNumber" =~ ^[0-9]+$ ]]
    then
        echo "<port number> must be an integer from 1024 to 49151"
        exit 1
fi
if [[ "$portNumber" -lt 1024 ]] || [[ "$portNumber" -gt 49151 ]]
then
  echo "<port number> must be an integer from 1024 to 49151"
fi

java Server "$portNumber"