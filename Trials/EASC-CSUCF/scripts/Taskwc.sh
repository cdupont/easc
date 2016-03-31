#!/bin/bash

function changewm(){
        actualwm=$1;
        nextwm=$2;
        nvmls=$(echo $nextwm | grep -o "${wcname}[0-9]\+" | grep -o "[0-9]\+");
        type=$3;

#si no hi ha webs a capturar no fem res
quantes=$(wc -l  resource/websaindexar.txt | awk '{print $1}')
eselzero=$(echo $nextwm | grep -ic "${wcname}0")
data=$(date +%Y-%m-%d)

if [ "$eselzero" -eq "1" ];then
 quantes=1
fi
if [ "$quantes" -ne "0" ]; then

nvmlsabans=$(echo $actualwm | grep -o "${wcname}[0-9]\+" | grep -o "[0-9]\+");

sqlite3 -init resource/init.sql  resource/dc4cities.sqlt "UPDATE wm_to_vm SET wm='"$nextwm"' WHERE activity='"$activity"' AND date='"$data"' AND wm='"$actualwm"';"

if [ "$nvmls" -gt "$nvmlsabans" ]; then

nvmls=$(echo "$nvmls-$nvmlsabans"|bc -l)

pirexisteix=$(sqlite3 -init resource/init.sql  resource/dc4cities.sqlt "SELECT COUNT(*) FROM virtual_machines WHERE ip='"$piretrixip"';")

if [ "$pirexisteix" -eq "0" ]; then

#abans d'encendre la maquina piretrix hem d'alliberar la ip
source ${home}/.rvm/scripts/rvm
rvm use $rubyversion 1>/dev/null

ruby scripts/virtualnop.rb $networkid release $piretrixip $datacenter

rvm use system 1>/dev/null

#encenem la maquina piretrix
identificador=$(econe-run-instances -K $userecone -S $passwordecone -U $endpointone -t m1.large $piretriximage|awk '{print $3}');

#esperem fins que piretrix estigui ences
while [ -z $piretrixipara ]; do
  piretrixipara=$(econe-describe-instances -K $userecone -S $passwordecone -U $endpointone | grep $identificador | grep -o "${patroip}[0-9]\+")
done

sqlite3 -init resource/init.sql  resource/dc4cities.sqlt "INSERT INTO virtual_machines VALUES ('"$identificador"','"$piretrixip"');"
sqlite3 -init resource/init.sql  resource/dc4cities.sqlt "INSERT INTO wm_to_vm VALUES ('"$activity"', '"$data"', '"$nextwm"', '"$identificador"');"

fi

#encenem les maquines que capturen
	for kk in `seq 1 $nvmls`
	do
		identificador=$(econe-run-instances -K $userecone -S $passwordecone -U $endpointone -t m1.small $padicatimage|awk '{print $3}');
		sleep 5;
		ippadicaptura=$(econe-describe-instances -K $userecone -S $passwordecone -U $endpointone | grep "$identificador" | grep -o "${patroip}[0-9]\+");
		sqlite3 -init resource/init.sql  resource/dc4cities.sqlt "INSERT INTO virtual_machines VALUES ('"$identificador"','"$ippadicaptura"');"
		sqlite3 -init resource/init.sql  resource/dc4cities.sqlt "INSERT INTO wm_to_vm VALUES ('"$activity"', '"$data"', '"$nextwm"', '"$identificador"');"
	done

#aqui hauriem de comprovar si totes les màquines virtuals s'han encès
#si alguna està en FAILED hauriem de tornar-hi fent un grep -v de les FAILED
rvm use $rubyversion 1>/dev/null

declare -A fallades
consulta=$(sqlite3 -init resource/init.sql  resource/dc4cities.sqlt "SELECT vmid FROM wm_to_vm WHERE activity='"$activity"' AND date='"$data"' AND wm='"$nextwm"';")
arrayquery=($( for j in $consulta ; do echo $j ; done ))
for a in ${arrayquery[@]}
do

  idvalid=$(echo $a | grep -o "[1-9][0-9]\+")

  status=$(ruby scripts/vmstatus.rb $idvalid $datacenter)
  while [ "$status" != "FAILED" -a "$status" != "ACTIVE" ]; do
   status=$(ruby scripts/vmstatus.rb $idvalid $datacenter)
   sleep 2
  done

  status=$(ruby scripts/vmstatus.rb $idvalid $datacenter)
  i=1
  while [ "$status" == "FAILED" -a "$i" -lt 4 ]
  #intentem fer-li un recreate 3 cops si no ens en sortim fem un delete
  do
   ruby scripts/vmdelete.rb $idvalid true $datacenter
   sleep 15
   status=$(ruby scripts/vmstatus.rb $idvalid $datacenter)

   while [ "$status" != "FAILED" -a "$status" != "ACTIVE" ]; do
     status=$(ruby scripts/vmstatus.rb $idvalid $datacenter)

     sleep 2
   done
   ((i=i+1))
   status=$(ruby scripts/vmstatus.rb $idvalid $datacenter)
  done

  if [ "$status" == "FAILED" ]; then
   ruby scripts/vmdelete.rb $idvalid false $datacenter
   fallades=("${fallades[@]}" "$a")
  fi

done

for i in "${fallades[@]}"
do
        sqlite3 -init resource/init.sql  resource/dc4cities.sqlt "DELETE FROM wm_to_vm WHERE activity='"$activity"' AND date='"$data"' AND wm='"$nextwm"' AND vmid='"$i"';"
	sqlite3 -init resource/init.sql  resource/dc4cities.sqlt "DELETE FROM virtual_machines WHERE vmid='"$i"';"
done
##fi comprovacions ara nomes queden les que estan bé


else

declare -A matar

quantara=1

consulta=$(sqlite3 -init resource/init.sql  resource/dc4cities.sqlt "SELECT wm_to_vm.vmid, ip FROM wm_to_vm, virtual_machines WHERE virtual_machines.vmid=wm_to_vm.vmid AND activity='"$activity"' AND date='"$data"' AND wm='"$nextwm"' AND ip!='"$piretrixip"';")

arrayquery=($( for j in $consulta ; do echo $j ; done ))
for a in ${arrayquery[@]}
  do
    IFS='|' read -ra camps <<< "$a"
    idvalid=${camps[0]}
    ip=${camps[1]}

    codi=$(curl --connect-timeout 2 -o /dev/null --silent --head --write-out '%{http_code}' "http://$ip:80/index.html")
    if [ "$codi" = "200" ]; then
      wget -q -O scripts/indexwc.html "http://$ip:80/index.html"
      lastline=$(tail -n1 scripts/indexwc.html)
      IFS=';' read -ra videocamps <<< "$lastline"
      quantqueda=$(echo "500-${videocamps[2]}"|bc -l)
    else
      quantqueda=$(echo "$quantara+100000"|bc -l)
      quantara=$(echo "$quantara+1"|bc -l)
    fi

    indexexisteix=""
    indexexisteix=${matar[$quantqueda]}

    if [ ! -z "$indexexisteix" ]; then
      quantqueda=$(echo "$quantqueda+$quantara"|bc -l)
      quantara=$(echo "$quantara+5"|bc -l)
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
    econe-terminate-instances -K $userecone -S $passwordecone -U $endpointone ${matarordenats[$indexmata]} 1>/dev/null 2>/dev/null
    idoriginal=${matarordenats[$indexmata]}
    idvalid=$(echo $idoriginal | grep -o "[1-9][0-9]\+")
    statuskilled="ACTIVE"
    rvm use $rubyversion 1>/dev/null
    while [ "$statuskilled" != "DONE" ]
    do
      statuskilled=$(ruby scripts/vmstatus.rb $idvalid $datacenter)
      sleep 5
    done
    sqlite3 -init resource/init.sql  resource/dc4cities.sqlt "DELETE FROM wm_to_vm WHERE activity='"$activity"' AND date='"$data"' AND wm='"$nextwm"' AND vmid='"$idoriginal"';"
    sqlite3 -init resource/init.sql  resource/dc4cities.sqlt "DELETE FROM virtual_machines WHERE vmid='"$idoriginal"';"
  done

##si despres de tot encara hi ha maquines enceses la matem directament sense pietat
quanteshanquedat=$(sqlite3 -init resource/init.sql  resource/dc4cities.sqlt "SELECT COUNT(*) FROM virtual_machines, wm_to_vm WHERE activity='"$activity"' AND date='"$data"' AND wm='"$nextwm"' AND virtual_machines.vmid=wm_to_vm.vmid AND ip!='"$piretrixip"';")
if [ "$quanteshanquedat" -gt "$nvmls" ]; then

   araquantesmates=$(echo "$quanteshanquedat-$nvmls"|bc -l)
   consulta=$(sqlite3 -init resource/init.sql  resource/dc4cities.sqlt "SELECT wm_to_vm.vmid FROM virtual_machines, wm_to_vm WHERE activity='"$activity"' AND date='"$data"' AND wm='"$nextwm"' AND virtual_machines.vmid=wm_to_vm.vmid AND ip!='"$piretrixip"';")
   arrayquery=($( for j in $consulta ; do echo $j ; done ))
   for a in `seq 0 $(($araquantesmates-1))`
  do
        idoriginal=${arrayquery[$a]}
        idvalid=$(echo $idoriginal | grep -o "[1-9][0-9]\+")
        ruby scripts/vmdelete.rb $idvalid false $datacenter
        sqlite3 -init resource/init.sql  resource/dc4cities.sqlt "DELETE FROM wm_to_vm WHERE activity='"$activity"' AND date='"$data"' AND wm='"$nextwm"' AND vmid='"$idoriginal"';"
        sqlite3 -init resource/init.sql  resource/dc4cities.sqlt "DELETE FROM virtual_machines WHERE vmid='"$idoriginal"';"
  done
fi


#si el working mode es 0 hem de matar piretrix
if [ "$eselzero" -eq "1" ];then
    rvm use system 1>/dev/null
    pirid=$(sqlite3 -init resource/init.sql  resource/dc4cities.sqlt "SELECT vmid FROM virtual_machines WHERE ip='"$piretrixip"';")
    econe-terminate-instances -K $userecone -S $passwordecone -U $endpointone $pirid 1>/dev/null 2>/dev/null
    idvalid=$(echo $pirid | grep -o "[1-9][0-9]\+")
    statuskilled="ACTIVE"
    rvm use $rubyversion 1>/dev/null
    while [ "$statuskilled" != "DONE" ]
    do
      statuskilled=$(ruby scripts/vmstatus.rb $idvalid $datacenter)
      sleep 5
    done
    sqlite3 -init resource/init.sql  resource/dc4cities.sqlt "DELETE FROM wm_to_vm WHERE activity='"$activity"' AND date='"$data"' AND wm='"$nextwm"' AND vmid='"$pirid"';"
    sqlite3 -init resource/init.sql  resource/dc4cities.sqlt "DELETE FROM virtual_machines WHERE vmid='"$pirid"';"

#abans d'acabar hem de fer el hold de la ip de piretrix
source ${home}/.rvm/scripts/rvm
rvm use $rubyversion 1>/dev/null
ruby scripts/virtualnop.rb $networkid hold $piretrixip $datacenter
fi

fi

fi

}

task=$1
actualwm=$2
nextwm=$3
datacenter=$4

# include parse_yaml function
. scripts/parse_yaml.sh

# read yaml file
eval $(parse_yaml resource/configruby.yaml "config_")

home=$(eval echo \$config_$datacenter\_home)
rubyversion=$(eval echo \$config_$datacenter\_rubyversion)
padicatimage=$(eval echo \$config_$datacenter\_padicatimage)
piretriximage=$(eval echo \$config_$datacenter\_piretriximage)
vtname=$(eval echo \$config_$datacenter\_vtname)
wcname=$(eval echo \$config_$datacenter\_wcname)
patroip=$(eval echo \$config_$datacenter\_patroip)
endpointone=$(eval echo \$config_$datacenter\_endpointone)
userecone=$(eval echo \$config_$datacenter\_userecone)
passwordecone=$(eval echo \$config_$datacenter\_passwordecone)
networkid=$(eval echo \$config_$datacenter\_networkid)
piretrixip=$(eval echo \$config_$datacenter\_piretrixip)
activity=$(eval echo \$config_$datacenter\_WebCrawling)

source ${home}/.rvm/scripts/rvm

case "$task" in

changewm)  echo "starting tasks";
        rvm use system 1>/dev/null
        changewm $actualwm $nextwm m1.xlarge;
    ;;
*) echo "parameter $task incorrect"
   ;;
esac
