#!/bin/bash

function quinesencenc()
{
local i=0

consulta=$(sqlite3 -init resource/init.sql  resource/dc4cities.sqlt "SELECT ip FROM wm_to_vm, virtual_machines WHERE activity='WebCrawling' AND date='"$data"' AND wm='"$actualwm"' AND wm_to_vm.vmid=virtual_machines.vmid AND ip!='hackmeplz';")
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
			echo "encendras heritrix per la ip $ipmaquina" >> /tmp/xivatomata.txt
			timeout -s 9 1m scripts/encendreheritrixos.sh $ipmaquina hackmeplz &
			let i=i+1
                  fi
          fi
        fi
done
echo $i

}


data=$(date +%Y-%m-%d)
surt=0
actualwmcanviant=$(sqlite3 -init resource/init.sql  resource/dc4cities.sqlt "SELECT wm FROM working_modes WHERE activity='WebCrawling' AND date='"$data"' AND status='canviant';");
eszerocanviant=$(echo "$actualwmcanviant"|grep -c "WM0")

actualwmcanviat=$(sqlite3 -init resource/init.sql  resource/dc4cities.sqlt "SELECT wm FROM working_modes WHERE activity='WebCrawling' AND date='"$data"' AND status='canviat';");
eszerocanviat=$(echo "$actualwmcanviat"|grep -c "WM0")
if [ "$eszerocanviant" -eq "1" ]; then
        surt=1
elif [ -z "$eszerocanviant" -a "$eszerocanviat" -eq "1" ]; then
        surt=1
fi
actualwm=$actualwmcanviat



while [ "$surt" -eq "0" ]
do

data=$(date +%Y-%m-%d)
temaquines=$(sqlite3 -init resource/init.sql  resource/dc4cities.sqlt "SELECT COUNT(*) FROM wm_to_vm WHERE activity='WebCrawling' AND date='"$data"' AND wm='"$actualwm"';")
if [ "$temaquines" -gt "0" ]; then

quines=$(quinesencenc)
let tempsesperant=$quines*60

sleep $tempsesperant



fi
sleep 5

actualwmcanviant=$(sqlite3 -init resource/init.sql  resource/dc4cities.sqlt "SELECT wm FROM working_modes WHERE activity='WebCrawling' AND date='"$data"' AND status='canviant';");
eszerocanviant=$(echo "$actualwmcanviant"|grep -c "WM0")
actualwmcanviat=$(sqlite3 -init resource/init.sql  resource/dc4cities.sqlt "SELECT wm FROM working_modes WHERE activity='WebCrawling' AND date='"$data"' AND status='canviat';");
eszerocanviat=$(echo "$actualwmcanviat"|grep -c "WM0")
if [ "$eszerocanviant" -eq "1" ]; then
        surt=1
elif [ -z "$eszerocanviant" -a "$eszerocanviat" -eq "1" ]; then
        surt=1
fi
actualwm=$actualwmcanviat


done
