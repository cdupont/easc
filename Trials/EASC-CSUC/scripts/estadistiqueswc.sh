#!/bin/bash

function calcular(){
  ipactual=$1
  rm -f resource/estadistiqueswc.html
  #pillem els webs que hagin acabat i que siguin d'avui
  wget --timeout=3 -q -O resource/estadistiqueswc.html "http://$ipactual:80/index.html"
  websdone=0
  ultimminut=0
  progressara=0

  if [ -e "resource/estadistiqueswc.html" ]; then
  webactuallinia=$(tail -n1 resource/estadistiqueswc.html)

  if [ -n "$webactuallinia" ]; then

  IFS=';' read -ra camps <<< "$webactuallinia"
  webactual=$(echo "${camps[3]}"| grep -o "www.*cat")
  diainiciweb=$(echo ${camps[0]} | grep -o ".*Z" | sed s/Z//g)
  diafinalweb=$(echo ${camps[1]} | grep -o ".*Z" | sed s/Z//g)
  webscrawled=${camps[2]}

  segonsfinalweb=$(date -d"$diafinalweb" +%s)
  segonsara=$(date +%s)
  diffsegons=$(echo "$segonsara-$segonsfinalweb"|bc -l)

  if [ "$diffsegons"  -lt "300" ]; then

  ultimminut=$webscrawled
  progressara=$webscrawled
  #calculem primer ultim minut
  quantesdest=$(wc -l < resource/estadistiqueswc.html)
  if [ "$quantesdest" -gt "1" ];then

    penultlinia=$(tail -n2 resource/estadistiqueswc.html|head -n1)
    IFS=';' read -ra camps2 <<< "$penultlinia"
    nomwebpenult=$(echo "${camps2[3]}"| grep -o "www.*cat")

    if [ "$webactual" = "$nomwebpenult" ]; then
        webscrawledpenult=${camps2[2]}
        ultimminut=$(echo "$webscrawled-$webscrawledpenult"|bc -l);
    fi

  ######
  ##ara calculem work done per aquella maquina virtual i que no s'hagi tingut en compte
  for kk in `grep "$avui" resource/estadistiqueswc.html|grep -v "$webactual"|grep -o "www.*cat"|sort -u`
  do
      line=$(grep $kk resource/estadistiqueswc.html|tail -n1)
      IFS=';' read -ra camps <<< "$line"
      nomweb=$(echo ${camps[3]}|grep -o "www.*cat")
      diainiciweb=$(echo ${camps[0]} | grep -o ".*T" | sed s/T//g)
      diafinalweb=$(echo ${camps[1]} | grep -o ".*T" | sed s/T//g)

      webscrawled=${camps[2]}

      quants=$(sqlite3 -init resource/init.sql  resource/dc4cities.sqlt "SELECT COUNT(*) FROM fets WHERE object='"$nomweb"'");
      #comprovem si esta al fitxer de webs fets per tots els working mode
      if [ "$quants" -eq "0" ]; then
	sqlite3 -init resource/init.sql  resource/dc4cities.sqlt "INSERT INTO fets VALUES ('WebCrawling','"$nomweb"')";
        #si el dia del inici del video i el de quan ha finalitzat el video es el mateix

	let websdone=websdone+webscrawled;
      fi
  done
  fi
  fi
  fi
  fi

  calculs[0]=$websdone
  calculs[1]=$ultimminut
  calculs[2]=$progressara
  echo ${calculs[@]}

}

avui=$(date +"%Y-%m-%d")
surt=0
actualwmcanviant=$(sqlite3 -init resource/init.sql  resource/dc4cities.sqlt "SELECT wm FROM working_modes WHERE activity='WebCrawling' AND date='"$avui"' AND status='canviant';");
eszerocanviant=$(echo "$actualwmcanviant"|grep -c "WM0")

actualwmcanviat=$(sqlite3 -init resource/init.sql  resource/dc4cities.sqlt "SELECT wm FROM working_modes WHERE activity='WebCrawling' AND date='"$avui"' AND status='canviat';");
eszerocanviat=$(echo "$actualwmcanviat"|grep -c "WM0")
if [ "$eszerocanviant" -eq "1" ]; then
        surt=1
elif [ -z "$actualwmcanviant" -a "$eszerocanviat" -eq "1" ]; then
        surt=1
fi
actualwm=$actualwmcanviat

ultimmintotal=0
mbdonetotal=0
progresstotal=0


while [ "$surt" -eq "0" ]
do

avui=$(date +"%Y-%m-%d")
segonsara=$(date +%Y-%m-%dT%H:%M:%S)

#detectem si ha canviat de dia hi havia un WM executant >0 i llavors el copiem
ahir=$(date +"%Y-%m-%d" --date="1 day ago")
existeixwmdiaactual=$(sqlite3 -init resource/init.sql  resource/dc4cities.sqlt "SELECT COUNT(*) working_modes WHERE date='"$avui"' AND activity='WebCrawling' AND status='canviat';")
if [ "$existeixwmdiaactual" -eq "0" ]; then
        wmahir=$(sqlite3 -init resource/init.sql  resource/dc4cities.sqlt "SELECT wm FROM working_modes WHERE date='"$ahir"' AND activity='WebCrawling' AND status='canviat' AND wm!='WCWM0';")
        if [ -n "$wmahir" ]; then
                sqlite3 -init resource/init.sql  resource/dc4cities.sqlt "INSERT INTO working_modes VALUES ('"$avui"', 'WebCrawling', '"$wmahir"', 'canviat');";
		sqlite3 -init resource/init.sql  resource/dc4cities.sqlt "UPDATE wm_to_vm SET date='"$avui"' WHERE date='"$ahir"' AND activity='WebCrawling' AND wm='"$wmahir"';";
        fi
fi


ultimmintotal=0
websdonetotal=0
progresstotal=0
#hem de recorrer tots les màquines virtuals de l'actual working mode, i veure quines està funcionant bé

consulta=$(sqlite3 -init resource/init.sql  resource/dc4cities.sqlt "SELECT ip FROM wm_to_vm, virtual_machines WHERE virtual_machines.vmid=wm_to_vm.vmid AND activity='WebCrawling' AND date='"$avui"' AND wm='"$actualwm"' AND ip!='hackmeplz';")

arrayquery=($( for j in $consulta ; do echo $j ; done ))
for ipactual in ${arrayquery[@]}
do
  codi=$(curl --connect-timeout 2 -o /dev/null --silent --head --write-out '%{http_code}' "http://$ipactual:80/index.html")
  if [ "$codi" = "200" ]; then
     resultsc=($(calcular $ipactual))
     websdoneara=${resultsc[0]}
     ultimminara=${resultsc[1]}
     progressara=${resultsc[2]}
     let websdonetotal=websdonetotal+websdoneara
     let ultimmintotal=ultimmintotal+ultimminara
     let progresstotal=progresstotal+progressara
  fi
done

progressinmediat=0
let progressinmediat=progresstotal+websdonetotal

existeix=$(sqlite3 -init resource/init.sql  resource/dc4cities.sqlt "SELECT COUNT(*) FROM progress WHERE activity='WebCrawling' AND date='"$avui"'");
if [ "$existeix" -eq "0" ]; then
 sqlite3 -init resource/init.sql  resource/dc4cities.sqlt "INSERT INTO progress VALUES ('WebCrawling', '"$avui"', $websdonetotal, $ultimmintotal, $progressinmediat, '"$segonsara"')"
else
	sqlite3 -init resource/init.sql  resource/dc4cities.sqlt "UPDATE progress SET quantity=quantity+$websdonetotal, last_instant_progress=last_instant_progress+$ultimmintotal, last_minute=$ultimmintotal, date_last_minute='"$segonsara"' WHERE activity='WebCrawling' AND date='"$avui"'"
# sqlite3 -init resource/init.sql  resource/dc4cities.sqlt "UPDATE progress SET quantity=quantity+$websdonetotal, last_instant_progress=$progressinmediat+quantity, last_minute=$ultimmintotal, date_last_minute='"$segonsara"' WHERE activity='WebCrawling' AND date='"$avui"'"
fi

sleep 60
actualwmcanviant=$(sqlite3 -init resource/init.sql  resource/dc4cities.sqlt "SELECT wm FROM working_modes WHERE activity='WebCrawling' AND date='"$avui"' AND status='canviant';");
eszerocanviant=$(echo "$actualwmcanviant"|grep -c "WM0")

actualwmcanviat=$(sqlite3 -init resource/init.sql  resource/dc4cities.sqlt "SELECT wm FROM working_modes WHERE activity='WebCrawling' AND date='"$avui"' AND status='canviat';");
eszerocanviat=$(echo "$actualwmcanviat"|grep -c "WM0")
if [ "$eszerocanviant" -eq "1" ]; then
        surt=1
elif [ -z "$actualwmcanviant" -a "$eszerocanviat" -eq "1" ]; then
        surt=1
fi
actualwm=$actualwmcanviat


done



#canvies a WM0 poso a 0 tot excepte el que ja s'ha fet
existeix=$(sqlite3 -init resource/init.sql  resource/dc4cities.sqlt "SELECT COUNT(*) FROM progress WHERE activity='WebCrawling' AND date='"$avui"'");
if [ "$existeix" -gt "0" ]; then
#sqlite3 -init resource/init.sql  resource/dc4cities.sqlt "UPDATE progress SET last_instant_progress=quantity, last_minute=0, date_last_minute='"$segonsara"' WHERE activity='WebCrawling' AND date='"$avui"'"
	sqlite3 -init resource/init.sql  resource/dc4cities.sqlt "UPDATE progress SET last_minute=0, date_last_minute='"$segonsara"' WHERE activity='WebCrawling' AND date='"$avui"'"
fi
