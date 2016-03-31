#!/bin/bash

function quinesencenc()
{
local i=0

consulta=$(sqlite3 -init resource/init.sql  resource/dc4cities.sqlt "SELECT ip FROM wm_to_vm, virtual_machines WHERE activity='"$activity"' AND date='"$data"' AND wm='"$actualwm"' AND wm_to_vm.vmid=virtual_machines.vmid AND ip!='"$piretrixip"';")
arrayquery=($( for j in $consulta ; do echo $j ; done ))

for ipmaquina in ${arrayquery[@]}
do
        codi=$(curl --connect-timeout 2 -o /dev/null --silent --head --write-out '%{http_code}' "http://$ipmaquina/status.html")
        if [ "$codi" = "200" ]
        then
          rm -f resource/statusmata.html; wget --timeout=3 -q -O resource/statusmata.html "http://$ipmaquina/status.html"
          if [ -e "resource/statusmata.html" ]; then
                  status=$(cat resource/statusmata.html)
                  if [ "$status" != "RUNNING" ]; then
			timeout -s 9 1m scripts/encendreheritrixos.sh $ipmaquina $datacenter &
			let i=i+1
                  fi
          fi
        fi
done
echo $i

}

datacenter=$1

# include parse_yaml function
. scripts/parse_yaml.sh

# read yaml file
eval $(parse_yaml resource/configruby.yaml "config_")

vtname=$(eval echo \$config_$datacenter\_vtname)
wcname=$(eval echo \$config_$datacenter\_wcname)
piretrixip=$(eval echo \$config_$datacenter\_piretrixip)
activity=$(eval echo \$config_$datacenter\_WebCrawling)

data=$(date +%Y-%m-%d)
surt=0
actualwmcanviant=$(sqlite3 -init resource/init.sql  resource/dc4cities.sqlt "SELECT wm FROM working_modes WHERE activity='"$activity"' AND date='"$data"' AND status='canviant';");
eszerocanviant=$(echo "$actualwmcanviant"|grep -c "${wcname}0")

actualwmcanviat=$(sqlite3 -init resource/init.sql  resource/dc4cities.sqlt "SELECT wm FROM working_modes WHERE activity='"$activity"' AND date='"$data"' AND status='canviat';");
eszerocanviat=$(echo "$actualwmcanviat"|grep -c "${wcname}0")
if [ "$eszerocanviant" -eq "1" ]; then
        surt=1
elif [ -z "$eszerocanviant" -a "$eszerocanviat" -eq "1" ]; then
        surt=1
fi
actualwm=$actualwmcanviat



while [ "$surt" -eq "0" ]
do

data=$(date +%Y-%m-%d)
temaquines=$(sqlite3 -init resource/init.sql  resource/dc4cities.sqlt "SELECT COUNT(*) FROM wm_to_vm WHERE activity='"$activity"' AND date='"$data"' AND wm='"$actualwm"';")
if [ "$temaquines" -gt "0" ]; then

quines=$(quinesencenc)
let tempsesperant=$quines*60

sleep $tempsesperant



fi
sleep 5

actualwmcanviant=$(sqlite3 -init resource/init.sql  resource/dc4cities.sqlt "SELECT wm FROM working_modes WHERE activity='"$activity"' AND date='"$data"' AND status='canviant';");
eszerocanviant=$(echo "$actualwmcanviant"|grep -c "${wcname}0")
actualwmcanviat=$(sqlite3 -init resource/init.sql  resource/dc4cities.sqlt "SELECT wm FROM working_modes WHERE activity='"$activity"' AND date='"$data"' AND status='canviat';");
eszerocanviat=$(echo "$actualwmcanviat"|grep -c "${wcname}0")
if [ "$eszerocanviant" -eq "1" ]; then
        surt=1
elif [ -z "$eszerocanviant" -a "$eszerocanviat" -eq "1" ]; then
        surt=1
fi
actualwm=$actualwmcanviat


done
