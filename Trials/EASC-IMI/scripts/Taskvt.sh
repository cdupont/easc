#!/bin/bash

function changewm(){
        actualwm=$1;
        nextwm=$2;
        nvmls=$(echo $nextwm | grep -o "VT[0-9]\+" | grep -o "[0-9]\+");
        type=$3;
        tasca="VT";
        imatge=${imatges[$tasca]};


data=$(date +%Y-%m-%d)

nvmlsabans=$(echo $actualwm | grep -o "VT[0-9]\+" | grep -o "[0-9]\+");

sqlite3 -init resource/init.sql  resource/dc4cities.sqlt "UPDATE wm_to_vm SET wm='"$nextwm"' WHERE activity='VideoTranscoding' AND date='"$data"' AND wm='"$actualwm"';"

if [ "$nvmls" -gt "$nvmlsabans" ]; then

nvmls=$(echo "$nvmls-$nvmlsabans"|bc -l)

	#encenem les maquines que codifiquen
	for kk in `seq 1 $nvmls`
	do
		identificador=$(econe-run-instances -K user -S password -U http://sunstone:4568/ -t $type $imatge| awk '{print $3}');
		sleep 5;
		iptranscoding=$(econe-describe-instances -K user -S password -U http://sunstone:4568/ | grep "$identificador" | grep -o "10.0.200.[0-9]\+");
                sqlite3 -init resource/init.sql  resource/dc4cities.sqlt "INSERT INTO virtual_machines VALUES ('"$identificador"','"$iptranscoding"');"
                sqlite3 -init resource/init.sql  resource/dc4cities.sqlt "INSERT INTO wm_to_vm VALUES ('VideoTranscoding', '"$data"', '"$nextwm"', '"$identificador"');"
	done

else

declare -A matar

consulta=$(sqlite3 -init resource/init.sql  resource/dc4cities.sqlt "SELECT wm_to_vm.vmid, ip FROM wm_to_vm, virtual_machines WHERE virtual_machines.vmid=wm_to_vm.vmid AND activity='VideoTranscoding' AND date='"$data"' AND wm='"$nextwm"';")

arrayquery=($( for j in $consulta ; do echo $j ; done ))
for a in ${arrayquery[@]}
  do
    IFS='|' read -ra camps <<< "$a"
    idvalid=${camps[0]}
    ip=${camps[1]}
 
    quantqueda=100000
    codi=$(curl --connect-timeout 2 -o /dev/null --silent --head --write-out '%{http_code}' "http://$ip:80/indexvt.html")
    if [ "$codi" = "200" ]; then
      rm -f scripts/indexvt.html
      wget --timeout=3 -q -O scripts/indexvt.html "http://$ip:80/index.html"
      if [ -e "scripts/indexvt.html" ]; then
	      lastline=$(tail -n1 scripts/indexvt.html)
	      IFS=';' read -ra videocamps <<< "$lastline"
	      quantqueda=$(echo "scale=2; (${videocamps[4]}/${videocamps[0]})*100-${videocamps[4]}" | bc -l)
      fi
    fi

    matar[$quantqueda]=$idvalid
    progressorder=("${progressorder[@]}" "$quantqueda")
done

  IFS=$'\n' sorted=($(sort -r <<<"${progressorder[*]}"))

  for i in "${!sorted[@]}"
  do
    matarordenats=("${matarordenats[@]}" "${matar[${sorted[$i]}]}")
  done

  quantesmates=$(echo "$nvmlsabans-$nvmls-1"|bc -l)
  for indexmata in `seq 0 $quantesmates`
  do
    rvm use system 1>/dev/null
    econe-terminate-instances -K user -S password -U http://sunstone:4568/ ${matarordenats[$indexmata]} 1>/dev/null 2>/dev/null
    idoriginal=${matarordenats[$indexmata]}
    idvalid=$(echo $idoriginal | grep -o "[1-9][0-9]\+")
    statuskilled="ACTIVE"
    rvm use ruby-2.1.4 1>/dev/null
    while [ "$statuskilled" != "DONE" ]
    do
      statuskilled=$(ruby scripts/vmstatus.rb $idvalid)
      sleep 5
    done
    sqlite3 -init resource/init.sql  resource/dc4cities.sqlt "DELETE FROM wm_to_vm WHERE activity='VideoTranscoding' AND date='"$data"' AND wm='"$nextwm"' AND vmid='"$idoriginal"';"
    sqlite3 -init resource/init.sql  resource/dc4cities.sqlt "DELETE FROM virtual_machines WHERE vmid='"$idoriginal"';"
  done

##si despres de tot encara hi ha maquines enceses la matem directament sense pietat
quanteshanquedat=$(sqlite3 -init resource/init.sql  resource/dc4cities.sqlt "SELECT COUNT(*) FROM wm_to_vm WHERE activity='VideoTranscoding' AND date='"$data"' AND wm='"$nextwm"';")
if [ "$quanteshanquedat" -gt "$nvmls" ]; then

   araquantesmates=$(echo "$quanteshanquedat-$nvmls"|bc -l)
   consulta=$(sqlite3 -init resource/init.sql  resource/dc4cities.sqlt "SELECT vmid FROM wm_to_vm WHERE activity='VideoTranscoding' AND date='"$data"' AND wm='"$nextwm"';")
   arrayquery=($( for j in $consulta ; do echo $j ; done ))
   for a in `seq 0 $(($araquantesmates-1))`
  do
	idoriginal=${arrayquery[$a]}
        idvalid=$(echo $idoriginal | grep -o "[1-9][0-9]\+")
        ruby scripts/vmdelete.rb $idvalid false
	sqlite3 -init resource/init.sql  resource/dc4cities.sqlt "DELETE FROM wm_to_vm WHERE activity='VideoTranscoding' AND date='"$data"' AND wm='"$nextwm"' AND vmid='"$idoriginal"';"
	sqlite3 -init resource/init.sql  resource/dc4cities.sqlt "DELETE FROM virtual_machines WHERE vmid='"$idoriginal"';"
  done
fi


fi

#aqui hauriem de comprovar si totes les màquines virtuals s'han encès
#si alguna està en FAILED hauriem de tornar-hi fent un grep -v de les FAILED
rvm use ruby-2.1.4 1>/dev/null

declare -A fallades
consulta=$(sqlite3 -init resource/init.sql  resource/dc4cities.sqlt "SELECT vmid FROM wm_to_vm WHERE activity='VideoTranscoding' AND date='"$data"' AND wm='"$nextwm"';")
arrayquery=($( for j in $consulta ; do echo $j ; done ))
for a in ${arrayquery[@]}
do

  idvalid=$(echo $a | grep -o "[1-9][0-9]\+")

  status=$(ruby scripts/vmstatus.rb $idvalid)
  while [ "$status" != "FAILED" -a "$status" != "ACTIVE" ]; do
   status=$(ruby scripts/vmstatus.rb $idvalid)
   sleep 2
  done

  status=$(ruby scripts/vmstatus.rb $idvalid)
  i=1
  while [ "$status" == "FAILED" -a "$i" -lt 4 ]
  #intentem fer-li un recreate 3 cops si no ens en sortim fem un delete
  do
   ruby scripts/vmdelete.rb $idvalid true
   sleep 15
   status=$(ruby scripts/vmstatus.rb $idvalid)

   while [ "$status" != "FAILED" -a "$status" != "ACTIVE" ]; do
     status=$(ruby scripts/vmstatus.rb $idvalid)

     sleep 2
   done
   ((i=i+1))
   status=$(ruby scripts/vmstatus.rb $idvalid)
  done

  if [ "$status" == "FAILED" ]; then
   ruby scripts/vmdelete.rb $idvalid false
   fallades=("${fallades[@]}" "$a")
  fi

done

for i in "${fallades[@]}"
do
        sqlite3 -init resource/init.sql  resource/dc4cities.sqlt "DELETE FROM wm_to_vm WHERE activity='VideoTranscoding' AND date='"$data"' AND wm='"$nextwm"' AND vmid='"$i"';"
        sqlite3 -init resource/init.sql  resource/dc4cities.sqlt "DELETE FROM virtual_machines WHERE vmid='"$i"';"
done
##fi comprovacions ara nomes queden les que estan bé


}


source /home/dc4cities/.rvm/scripts/rvm

declare -A imatges
imatges[VT]="ami-00000061"

case "$1" in

changewm)  echo "starting tasks";
        rvm use system 1>/dev/null
        changewm $2 $3 m1.xlarge;
    ;;
*) echo "parameter $1 incorrect"
   ;;
esac
