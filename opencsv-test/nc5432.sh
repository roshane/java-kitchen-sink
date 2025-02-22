#!/bin/bash

CIDR_RANGE="192.168.1.0/24"

IFS=. read -r i1 i2 i3 i4 <<< ${CIDR_RANGE%/*}
NETWORK_PREFIX="$i1.$i2.$i3."

for ((i=1; i<=254; i++))
do
  IP="$NETWORK_PREFIX$i"
  # Check if port 5432 is open
  nc -zv -w 1 $IP 5432 2>&1 | grep -q "open" && echo "$IP:5432 is open"
done
