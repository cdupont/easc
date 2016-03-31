#!/bin/bash

function round(){
	toconvert=$1
	rounded=$(echo $toconvert | awk '{printf("%d\n",$1 + 0.5)}')
	echo $rounded
}

function calcular(){
  ipactual=$1
  rm -f resource/estadistiquesvt.html
  #pillem els webs que hagin acabat i que siguin d'avui
  wget --timeout=3 -q -O resource/estadistiquesvt.html "http://$ipactual:80/index.html"
  mbdone=0
  ultimminut=0
  progressara=0

  if [ -e "resource/estadistiquesvt.html" ]; then
  videoactuallinia=$(tail -n1 resource/estadistiquesvt.html)

  if [ -n "$videoactuallinia" ]; then

  IFS=';' read -ra camps <<< "$videoactuallinia"
  nomvideo=${camps[5]}
  diainicivideo=$(echo ${camps[1]} | grep -o ".*Z" | sed s/Z//g)
  diafinalvideo=$(echo ${camps[2]} | grep -o ".*Z" | sed s/Z//g)

  mbconverted=$(round ${camps[4]})

  segonsfinalvideo=$(date -d"$diafinalvideo" +%s)
  segonsara=$(date +%s)
  diffsegons=$(echo "$segonsara-$segonsfinalvideo"|bc -l)

  if [ "$diffsegons"  -lt "300" ]; then

  ultimminut=$mbconverted
  progressara=$mbconverted
  #calculem primer ultim minut
  quantesdest=$(wc -l < resource/estadistiquesvt.html)
  if [ "$quantesdest" -gt "1" ];then

    penultlinia=$(tail -n2 resource/estadistiquesvt.html|head -n1)
    IFS=';' read -ra camps2 <<< "$penultlinia"
    nomvideopenult=${camps2[5]}

    if [ "$nomvideo" = "$nomvideopenult" ]; then
        mbconvertedpenult=$(round ${camps2[4]})
        ultimminut=$(echo "$mbconverted-$mbconvertedpenult"|bc -l);
    fi

  ######
  ##ara calculem work done per aquella maquina virtual i que no s'hagi tingut en compte
  for kk in `grep "100\.0" resource/estadistiquesvt.html|grep "$avui" |grep -w -v "$nomvideo"`
  do
      line=$(grep $kk resource/estadistiquesvt.html|tail -n1)
      IFS=';' read -ra camps <<< "$line"
      nomvideo=${camps[5]}
      diainicivideo=$(echo ${camps[1]} | grep -o ".*Z" | sed s/Z//g)
      diafinalvideo=$(echo ${camps[2]} | grep -o ".*Z" | sed s/Z//g)

      mbconverted=$(round ${camps[4]})

      quants=$(sqlite3 -init resource/init.sql  resource/dc4cities.sqlt "SELECT COUNT(*) FROM fets WHERE object='"$nomvideo"'");
      #comprovem si esta al fitxer de webs fets per tots els working mode
      if [ "$quants" -eq "0" ]; then
	sqlite3 -init resource/init.sql  resource/dc4cities.sqlt "INSERT INTO fets VALUES ('VideoTranscoding','"$nomvideo"')";
        #si el dia del inici del video i el de quan ha finalitzat el video es el mateix

	let mbdone=mbdone+mbconverted;
      fi
  done
  fi
  fi
  fi
  fi

  calculs[0]=$mbdone
  calculs[1]=$ultimminut
  calculs[2]=$progressara
  echo ${calculs[@]}

}


avui=$(date +"%Y-%m-%d")
surt=0
actualwmcanviant=$(sqlite3 -init resource/init.sql  resource/dc4cities.sqlt "SELECT wm FROM working_modes WHERE activity='VideoTranscoding' AND date='"$avui"' AND status='canviant';");
eszerocanviant=$(echo "$actualwmcanviant"|grep -c "WM0")

actualwmcanviat=$(sqlite3 -init resource/init.sql  resource/dc4cities.sqlt "SELECT wm FROM working_modes WHERE activity='VideoTranscoding' AND date='"$avui"' AND status='canviat';");
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
existeixwmdiaactual=$(sqlite3 -init resource/init.sql  resource/dc4cities.sqlt "SELECT COUNT(*) working_modes WHERE date='"$avui"' AND activity='VideoTranscoding' AND status='canviat';")
if [ "$existeixwmdiaactual" -eq "0" ]; then
	wmahir=$(sqlite3 -init resource/init.sql  resource/dc4cities.sqlt "SELECT wm FROM working_modes WHERE date='"$ahir"' AND activity='VideoTranscoding' AND status='canviat' AND wm!='VTWM0';")
	if [ -n "$wmahir" ]; then
		sqlite3 -init resource/init.sql  resource/dc4cities.sqlt "INSERT INTO working_modes VALUES ('"$avui"', 'VideoTranscoding', '"$wmahir"', 'canviat');";
		sqlite3 -init resource/init.sql  resource/dc4cities.sqlt "UPDATE wm_to_vm SET date='"$avui"' WHERE date='"$ahir"' AND activity='VideoTranscoding' AND wm='"$wmahir"';";
	fi
fi

ultimmintotal=0
mbdonetotal=0
progresstotal=0

consulta=$(sqlite3 -init resource/init.sql  resource/dc4cities.sqlt "SELECT ip FROM wm_to_vm, virtual_machines WHERE virtual_machines.vmid=wm_to_vm.vmid AND activity='VideoTranscoding' AND date='"$avui"' AND wm='"$actualwm"';")

#hem de recorrer tots les màquines virtuals de l'actual working mode, i veure quines està funcionant bé
arrayquery=($( for j in $consulta ; do echo $j ; done ))
for ipactual in ${arrayquery[@]}
do
  codi=$(curl --connect-timeout 2 -o /dev/null --silent --head --write-out '%{http_code}' "http://$ipactual:80/index.html")
  if [ "$codi" = "200" ]; then
     resultsc=($(calcular $ipactual))
     mbdoneara=${resultsc[0]}
     ultimminara=${resultsc[1]}
     progressara=${resultsc[2]}
     let mbdonetotal=mbdonetotal+mbdoneara
     let ultimmintotal=ultimmintotal+ultimminara
     let progresstotal=progresstotal+progressara
  fi
done

progressinmediat=0
let progressinmediat=progresstotal+mbdonetotal

existeix=$(sqlite3 -init resource/init.sql  resource/dc4cities.sqlt "SELECT COUNT(*) FROM progress WHERE activity='VideoTranscoding' AND date='"$avui"'");
if [ "$existeix" -eq "0" ]; then
 sqlite3 -init resource/init.sql  resource/dc4cities.sqlt "INSERT INTO progress VALUES ('VideoTranscoding', '"$avui"', $mbdonetotal, $ultimmintotal, $progressinmediat, '"$segonsara"')"
else
	sqlite3 -init resource/init.sql  resource/dc4cities.sqlt "UPDATE progress SET quantity=quantity+$mbdonetotal, last_instant_progress=last_instant_progress+$ultimmintotal, last_minute=$ultimmintotal, date_last_minute='"$segonsara"' WHERE activity='VideoTranscoding' AND date='"$avui"'"
#sqlite3 -init resource/init.sql  resource/dc4cities.sqlt "UPDATE progress SET quantity=quantity+$mbdonetotal, last_instant_progress=$progressinmediat+quantity, last_minute=$ultimmintotal, date_last_minute='"$segonsara"' WHERE activity='VideoTranscoding' AND date='"$avui"'"
fi

sleep 60
actualwmcanviant=$(sqlite3 -init resource/init.sql  resource/dc4cities.sqlt "SELECT wm FROM working_modes WHERE activity='VideoTranscoding' AND date='"$avui"' AND status='canviant';");
eszerocanviant=$(echo "$actualwmcanviant"|grep -c "WM0")

actualwmcanviat=$(sqlite3 -init resource/init.sql  resource/dc4cities.sqlt "SELECT wm FROM working_modes WHERE activity='VideoTranscoding' AND date='"$avui"' AND status='canviat';");
eszerocanviat=$(echo "$actualwmcanviat"|grep -c "WM0")
if [ "$eszerocanviant" -eq "1" ]; then
        surt=1
elif [ -z "$actualwmcanviant" -a "$eszerocanviat" -eq "1" ]; then
        surt=1
fi
actualwm=$actualwmcanviat

done



#canvies a WM0 poso a 0 tot excepte el que ja s'ha fet
existeix=$(sqlite3 -init resource/init.sql  resource/dc4cities.sqlt "SELECT COUNT(*) FROM progress WHERE activity='VideoTranscoding' AND date='"$avui"'");
if [ "$existeix" -gt "0" ]; then
	sqlite3 -init resource/init.sql  resource/dc4cities.sqlt "UPDATE progress SET last_minute=0, date_last_minute='"$segonsara"' WHERE activity='VideoTranscoding' AND date='"$avui"'"
#sqlite3 -init resource/init.sql  resource/dc4cities.sqlt "UPDATE progress SET last_instant_progress=quantity, last_minute=0, date_last_minute='"$segonsara"' WHERE activity='VideoTranscoding' AND date='"$avui"'"
fi
