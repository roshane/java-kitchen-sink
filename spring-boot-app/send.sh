#!/bin/bash

for i in {1..10}; do
  sleep 1
  UUID="$(uuidgen)"
  PAYLOAD="{ \"id\":\"$UUID\",\"message\":\"hello there\"}"
  echo "Send $UUID"
  curl -s 'http://0:8080' -H 'content-type:application/json' -d "$PAYLOAD"
  echo
done
