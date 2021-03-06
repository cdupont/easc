#!/bin/bash --login

function return_actual_wm(){
	task=$1
        actualwmcanviant=$(sqlite3 -init resource/init.sql  resource/dc4cities.sqlt "SELECT wm FROM working_modes WHERE activity='"$task"' AND date='"$data"' AND status='canviant';");
        actualwmcanviat=$(sqlite3 -init resource/init.sql  resource/dc4cities.sqlt "SELECT wm FROM working_modes WHERE activity='"$task"' AND date='"$data"' AND status='canviat';")
	if [ -n "$actualwmcanviant" ]; then
		actualwm=$actualwmcanviant
	elif [ -n "$actualwmcanviat" ]; then
		actualwm=$actualwmcanviat
	else
		if [ "$task" = "VideoTranscoding" ]; then
			actualwm="VTWM0"
		else
			actualwm="WCWM0"
		fi
	fi
echo $actualwm
}

function enable_host(){
        id=$1
        clusterstatus=$(ruby scripts/hoststatus.rb $id)
        if [ "$clusterstatus" != "on" ]; then
                ruby scripts/enablehost.rb $id
        fi
        clusterstatus=$(ruby scripts/hoststatus.rb $id)
        while [ "$clusterstatus" != "on" ]
        do
                sleep 5
                clusterstatus=$(ruby scripts/hoststatus.rb $id)
        done
}

function disable_host(){
#abans de fer el disable he de comprovar que la maquina ja no esta en aquell host
#es comprovar que esta en running i a la vegada en el host
        id=$1
        name=$2
        check_still_running $id
        clusterstatus=$(ruby scripts/hoststatus.rb $id)
        if [ "$clusterstatus" != "off" ]; then
                ruby scripts/disablehost.rb $id
        fi
        clusterstatus=$(ruby scripts/hoststatus.rb $id)
        while [ "$clusterstatus" != "off" ]
        do
                sleep 5
                clusterstatus=$(ruby scripts/hoststatus.rb $id)
        done
}

function check_vm_host(){
	#retrieve all virtual machines
	consulta=$(sqlite3 -init resource/init.sql  resource/dc4cities.sqlt "SELECT vmid FROM wm_to_vm WHERE activity='VideoTranscoding';");

	arrayquery=($( for j in $consulta ; do echo $j ; done ))
	for a in ${arrayquery[@]}
	do
		id_valid=$(echo $a | grep -o -P "[1-9][0-9]+")
		#check if owns to host
		owned_host=$(ruby scripts/vmhost.rb $id_valid|grep -c "cluster00")

		while [ "$owned_host" -gt "0" ]
		do
			#migrate vm (if it's not already doing it)
			lcm_state=$(ruby scripts/vmlcmstatus.rb $id_valid)
			if [ "$lcm_state" = "RUNNING" ]; then
				ruby scripts/vmmigrate.rb $id_valid 6
			fi
			sleep 5
                        owned_host=$(ruby scripts/vmhost.rb $id_valid|grep -c "cluster00")
		done
	done

}

function check_still_running(){
        hostid=$1

        quantesvmhost=$(ruby scripts/hostvm.rb $hostid | grep "[0-9]" | wc -l)
        while [ "$quantesvmhost" -gt "0" ]
        do
                quantesvmhost=$(ruby scripts/hostvm.rb $hostid | grep "[0-9]" | wc -l)
		sleep 5
        done
}

#cluster01=6
#cluster00=3
function check_nodes(){
	#first video transcoding
	actual_wm_vt=$(return_actual_wm "VideoTranscoding")
	#now web crawling
	actual_wm_wc=$(return_actual_wm "WebCrawling")

	#case vt4 and wc>0
	if [ "$actual_wm_vt" = "VTWM4" -a "$actual_wm_wc" != "WCWM0" ]; then
		dummy=0
	#if both of them are 0 two nodes off
	elif [ "$actual_wm_vt" = "VTWM0" -a "$actual_wm_wc" = "WCWM0" ]; then
		disable_host 6 cluster01
		disable_host 3 cluster00
	#rest of cases just one node on
	else
		cluster00status=$(ruby scripts/hoststatus.rb 3)
		if [ "$cluster00status" != "off" ]; then
			#migrate virtual machines
			check_vm_host
			#get node cluster00 off
			disable_host 3 cluster00
		fi
	fi
}

function check_nodes_all_on(){
        #first video transcoding
        actual_wm_vt=$(return_actual_wm "VideoTranscoding")
        #now web crawling
        actual_wm_wc=$(return_actual_wm "WebCrawling")

        #case vt4 and wc>0
        if [ "$actual_wm_vt" = "VTWM4" -a "$actual_wm_wc" != "WCWM0" ]; then
                #get on both of them
                enable_host 6
                enable_host 3

                #wait until both nodes on
                cluster01status=$(ruby scripts/hoststatus.rb 6)
                cluster00status=$(ruby scripts/hoststatus.rb 3)
                while [ "$cluster01status" != "on" -a "$cluster00status" != "on" ]
                do
                        sleep 5
                        cluster01status=$(ruby scripts/hoststatus.rb 6)
                        cluster00status=$(ruby scripts/hoststatus.rb 3)
                done
	elif [ "$actual_wm_vt" != "VTWM0" -o "$actual_wm_wc" != "WCWM0" ]; then
		enable_host 6
	fi
}


function check_wm_yesterday(){
	#detectem si ha canviat de dia hi havia un WM executant >0 i llavors el copiem
	avui=$(date +%Y-%m-%d)
	ahir=$(date +"%Y-%m-%d" --date="1 day ago")
	existeixwmdiaactual=$(sqlite3 -init resource/init.sql  resource/dc4cities.sqlt "SELECT COUNT(*) FROM working_modes WHERE date='"$avui"' AND activity='VideoTranscoding' AND status='canviat';")
	if [ "$existeixwmdiaactual" -eq "0" ]; then
		wmahir=$(sqlite3 -init resource/init.sql  resource/dc4cities.sqlt "SELECT wm FROM working_modes WHERE date='"$ahir"' AND activity='VideoTranscoding' AND status='canviat' AND wm!='VTWM0';")
		if [ -n "$wmahir" ]; then
			sqlite3 -init resource/init.sql  resource/dc4cities.sqlt "INSERT INTO working_modes VALUES ('"$avui"', 'VideoTranscoding', '"$wmahir"', 'canviat');";
			sqlite3 -init resource/init.sql  resource/dc4cities.sqlt "UPDATE wm_to_vm SET date='"$avui"' WHERE date='"$ahir"' AND activity='VideoTranscoding' AND wm='"$wmahir"';";
		fi
	fi
}

#Exemple script to switch working modes
nextwm=$1
echo "Switching to Working Mode $nextwm"

data=$(date +%Y-%m-%d)

#si ahir ja hi havia un wm executant-se el copiem cap a avui
check_wm_yesterday

actualwm=$(sqlite3 -init resource/init.sql  resource/dc4cities.sqlt "SELECT wm FROM working_modes WHERE activity='VideoTranscoding' AND date='"$data"' AND status='canviat';");

if [ -z "$actualwm" ]; then
	sqlite3 -init resource/init.sql  resource/dc4cities.sqlt "INSERT INTO working_modes VALUES ('"$data"', 'VideoTranscoding', 'VTWM0', 'canviat');";
	actualwm="VTWM0"
fi

if [ "$actualwm" != "$nextwm" ]; then

sqlite3 -init resource/init.sql  resource/dc4cities.sqlt "INSERT INTO working_modes VALUES ('"$data"', 'VideoTranscoding', '"$nextwm"', 'canviant');";

eszero=$(echo $nextwm|grep -c "WM0")
if [ "$eszero" -eq "0" ]; then
        if ! [ "$(pidof -x estadistiquesvt.sh)" ]; then
           nohup scripts/estadistiquesvt.sh 2>/dev/null&
        fi
fi

source /home/ois/.rvm/scripts/rvm
rvm use ruby-2.1.2 1>/dev/null

#control nodes on/off and migrate virtual machines
check_nodes_all_on

  #change between working modes, controlling the previous and actual working mode
  scripts/Taskvt.sh changewm $actualwm $nextwm

sqlite3 -init resource/init.sql  resource/dc4cities.sqlt "DELETE FROM working_modes WHERE activity='VideoTranscoding' AND date='"$data"' AND wm='"$actualwm"' AND status='canviat';"
sqlite3 -init resource/init.sql  resource/dc4cities.sqlt "UPDATE working_modes SET status='canviat' WHERE  activity='VideoTranscoding' AND date='"$data"' AND wm='"$nextwm"' AND status='canviant';";

#mentre hi hagi un que encara esta canviant no fem res doncs potser encara esta migrant virtuals
encaracanviant=$(sqlite3 -init resource/init.sql  resource/dc4cities.sqlt "SELECT COUNT(*) FROM working_modes WHERE status='canviant'";)

while [ "$encaracanviant" -gt "0" ]
do
        sleep 5
        encaracanviant=$(sqlite3 -init resource/init.sql  resource/dc4cities.sqlt "SELECT COUNT(*) FROM working_modes WHERE status='canviant'";)
done
fi
check_nodes
