#!/bin/bash

activity=$1
type=$2

todaytext=$(date +%Y-%m-%d)
value=0
if [ "$type" = "instant" ]; then
  result=$(sqlite3 -init resource/init.sql resource/dc4cities.sqlt "SELECT last_minute, date_last_minute FROM progress WHERE date='"$todaytext"' AND activity='"$activity"';")
  IFS='|' read -ra camps <<< "$result"
  date_last_minute=${camps[1]}
  seconds_last_minute=$(date --date="$date_last_minute" +%s)
  seconds_now=$(date +%s)
  let diff_seconds=seconds_now-seconds_last_minute
  if [ "$diff_seconds" -lt "300" ]; then
    value=${camps[0]}
  fi
else
  value=$(sqlite3 -init resource/init.sql resource/dc4cities.sqlt "SELECT last_instant_progress FROM progress WHERE date='"$todaytext"' AND activity='"$activity"';")
fi

if [ -z "$value" ]; then
	value=0
fi

echo $value
