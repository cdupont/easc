#!/bin/bash --login

activity=$1
wm=$2
datacenter=$3

# include parse_yaml function
. scripts/parse_yaml.sh

# read yaml file
eval $(parse_yaml resource/configruby.yaml "config_")
url=$(eval echo \$config_$datacenter\_url)
activitydc=$(eval echo \$config_$datacenter\_$activity)

curl -s --max-time 5 --connect-timeout 5 -i $url:8500/changeWM?activity=$activity\&wm=$wm\&datacenter=$datacenter

wmstatus=$(curl -s $url:8500/getWMStatus?activity=$activitydc\&wm=$wm)
while [ "$wmstatus" = "canviant" ]
do
	wmstatus=$(curl -s $url:8500/getWMStatus?activity=$activitydc\&wm=$wm)
	sleep 5
done
