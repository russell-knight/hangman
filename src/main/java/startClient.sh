#!/usr/bin/env bash

if [[ "$#" -ne 2 ]]
then
  echo "Usage: ./startClient.sh <host> <port number>"
  echo "where <host name> is a valid host <port number> is a valid port number (from 1024 to 49151)"
  exit 1
fi
hostName="$1"
portNumber="$2"

if ! [[ "$portNumber" =~ ^[0-9]+$ ]]
    then
        echo "<port number> must be an integer from 1024 to 49151"
        exit 1
fi
if [[ "$portNumber" -lt 1024 ]] || [[ "$portNumber" -gt 49151 ]]
then
  echo "<port number> must be an integer from 1024 to 49151"
fi

java Client "$hostName" "$portNumber"