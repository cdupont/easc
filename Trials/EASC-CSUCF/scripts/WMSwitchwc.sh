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
                esvideo=$(echo $task | grep -c "VideoTranscoding")
                if [ "$esvideo" -gt "0" ]; then
			actualwm="${vtname}0"
		else
			actualwm="${wcname}0"
		fi
	fi
echo $actualwm
}

function enable_host(){
	id=$1
	clusterstatus=$(ruby scripts/hoststatus.rb $id $datacenter)
	if [ "$clusterstatus" != "on" ]; then
		ruby scripts/enablehost.rb $id $datacenter
	fi
        clusterstatus=$(ruby scripts/hoststatus.rb $id $datacenter)
        while [ "$clusterstatus" != "on" ]
        do
                sleep 5
                clusterstatus=$(ruby scripts/hoststatus.rb $id $datacenter)
        done
}

function disable_host(){
#abans de fer el disable he de comprovar que la maquina ja no esta en aquell host
#es comprovar que esta en running i a la vegada en el host
        id=$1
        name=$2
        check_still_running $id
        clusterstatus=$(ruby scripts/hoststatus.rb $id $datacenter)
        if [ "$clusterstatus" != "off" ]; then
                ruby scripts/disablehost.rb $id $datacenter
        fi
        clusterstatus=$(ruby scripts/hoststatus.rb $id $datacenter)
        while [ "$clusterstatus" != "off" ]
        do
                sleep 5
                clusterstatus=$(ruby scripts/hoststatus.rb $id $datacenter)
        done
}

function check_vm_host(){
	#retrieve all virtual machines
        consulta=$(sqlite3 -init resource/init.sql  resource/dc4cities.sqlt "SELECT vmid FROM wm_to_vm WHERE activity='"$activitywc"';");

	arrayquery=($( for j in $consulta ; do echo $j ; done ))
	for a in ${arrayquery[@]}
	do
		id_valid=$(echo $a | grep -o -P "[1-9][0-9]+")
		#check if owns to host
		owned_host=$(ruby scripts/vmhost.rb $id_valid $datacenter |grep -c "$host2")

		while [ "$owned_host" -gt "0" ]
		do
			#migrate vm (if it's not already doing it)
			lcm_state=$(ruby scripts/vmlcmstatus.rb $id_valid $datacenter)
			if [ "$lcm_state" = "RUNNING" ]; then
				ruby scripts/vmmigrate.rb $id_valid $host1id $datacenter
			fi
			sleep 5
                        owned_host=$(ruby scripts/vmhost.rb $id_valid $datacenter |grep -c "$host2")
		done
	done

}

function check_still_running(){
        hostid=$1

        quantesvmhost=$(ruby scripts/hostvm.rb $hostid $datacenter | grep "[0-9]" | wc -l)
        while [ "$quantesvmhost" -gt "0" ]
        do
                quantesvmhost=$(ruby scripts/hostvm.rb $hostid $datacenter | grep "[0-9]" | wc -l)
		sleep 5
        done
}

function check_nodes(){

for kk in `seq 1 2`
do
        #first video transcoding
        actual_wm_vt=$(return_actual_wm "$activityvt")
        #now web crawling
        actual_wm_wc=$(return_actual_wm "$activitywc")
	sleep 2
done

        #case vt4 and wc>0
        if [ "$actual_wm_vt" = "${vtname}4" -a "$actual_wm_wc" != "${wcname}0" ]; then
                dummy=0
        #if both of them are 0 two nodes off
        elif [ "$actual_wm_vt" = "${vtname}0" -a "$actual_wm_wc" = "${wcname}0" ]; then
                disable_host $host1id $host1
                disable_host $host2id $host2
                if [ "$ipmi" = "true" ]; then
                        ipmitool -E -H $host1ipmi -U dc4cipmi -I lan power soft
                        ipmitool -E -H $host2ipmi -U dc4cipmi -I lan power soft
                fi
        #rest of cases just one node on
        else
                host2status=$(ruby scripts/hoststatus.rb $host2id $datacenter)
                if [ "$host2status" != "off" ]; then
                        #migrate virtual machines
                        check_vm_host
                        #get node cluster00 off
                        disable_host $host2id $host2
                        if [ "$ipmi" = "true" ]; then
                                ipmitool -E -H $host2ipmi -U dc4cipmi -I lan power soft
                        fi
                fi
        fi
}

function check_nodes_all_on(){

for kk in `seq 1 2`
do
        #first video transcoding
        actual_wm_vt=$(return_actual_wm "$activityvt")
        #now web crawling
        actual_wm_wc=$(return_actual_wm "$activitywc")
	sleep 2
done

        #case vt4 and wc>0
        if [ "$actual_wm_vt" = "${vtname}4" -a "$actual_wm_wc" != "${wcname}0" ]; then
                #get on both of them
                if [ "$ipmi" = "true" ]; then
                        ipmitool -E -H $host1ipmi -U dc4cipmi -I lan power on
                        ipmitool -E -H $host2ipmi -U dc4cipmi -I lan power on
                fi
                enable_host $host1id
                enable_host $host2id

                #wait until both nodes on
                host1status=$(ruby scripts/hoststatus.rb $host1id $datacenter)
                host2status=$(ruby scripts/hoststatus.rb $host2id $datacenter)
                while [ "$host1status" != "on" -a "$host2status" != "on" ]
                do
                        sleep 5
                        host1status=$(ruby scripts/hoststatus.rb $host1id $datacenter)
                        host2status=$(ruby scripts/hoststatus.rb $host2id $datacenter)
                done
        elif [ "$actual_wm_vt" != "${vtname}0" -o "$actual_wm_wc" != "${wcname}0" ]; then
                if [ "$ipmi" = "true" ]; then
                        ipmitool -E -H $host1ipmi -U dc4cipmi -I lan power on
                fi
                enable_host $host1id
	fi
}

function check_wm_yesterday(){
        #detectem si ha canviat de dia hi havia un WM executant >0 i llavors el copiem
        avui=$(date +%Y-%m-%d)
        ahir=$(date +"%Y-%m-%d" --date="1 day ago")
        existeixwmdiaactual=$(sqlite3 -init resource/init.sql  resource/dc4cities.sqlt "SELECT COUNT(*) FROM working_modes WHERE date='"$avui"' AND activity='"$activitywc"' AND status='canviat';")
        if [ "$existeixwmdiaactual" -eq "0" ]; then
                wmahir=$(sqlite3 -init resource/init.sql  resource/dc4cities.sqlt "SELECT wm FROM working_modes WHERE date='"$ahir"' AND activity='"$activitywc"' AND status='canviat' AND wm!='"${wcname}0"';")
                if [ -n "$wmahir" ]; then
                        sqlite3 -init resource/init.sql  resource/dc4cities.sqlt "INSERT INTO working_modes VALUES ('"$avui"', '"$activitywc"', '"$wmahir"', 'canviat');";
                        sqlite3 -init resource/init.sql  resource/dc4cities.sqlt "UPDATE wm_to_vm SET date='"$avui"' WHERE date='"$ahir"' AND activity='"$activitywc"' AND wm='"$wmahir"';";
                fi
        fi
}

#Exemple script to switch working modes
nextwm=$1
datacenter=$2

# include parse_yaml function
. scripts/parse_yaml.sh

# read yaml file
eval $(parse_yaml resource/configruby.yaml "config_")

vtname=$(eval echo \$config_$datacenter\_vtname)
wcname=$(eval echo \$config_$datacenter\_wcname)
home=$(eval echo \$config_$datacenter\_home)
rubyversion=$(eval echo \$config_$datacenter\_rubyversion)
host1=$(eval echo \$config_$datacenter\_host1)
host2=$(eval echo \$config_$datacenter\_host2)
host1id=$(eval echo \$config_$datacenter\_host1id)
host2id=$(eval echo \$config_$datacenter\_host2id)
activityvt=$(eval echo \$config_$datacenter\_VideoTranscoding)
activitywc=$(eval echo \$config_$datacenter\_WebCrawling)
ipmi=$(eval echo \$config_$datacenter\_ipmi)
if [ "$ipmi" = "true" ];then
        host1ipmi=$(eval echo \$config_$datacenter\_host1ipmi)
        host2ipmi=$(eval echo \$config_$datacenter\_host2ipmi)
fi

echo "Switching to Working Mode $nextwm"

data=$(date +%Y-%m-%d)

#si ahir ja hi havia un wm executant-se el copiem cap a avui
check_wm_yesterday

actualwm=$(sqlite3 -init resource/init.sql  resource/dc4cities.sqlt "SELECT wm FROM working_modes WHERE activity='"$activitywc"' AND date='"$data"' AND status='canviat';");

if [ -z "$actualwm" ]; then
	sqlite3 -init resource/init.sql  resource/dc4cities.sqlt "INSERT INTO working_modes VALUES ('"$data"', '"$activitywc"', '"${wcname}0"', 'canviat');";
	actualwm="${wcname}0"
fi

if [ "$actualwm" != "$nextwm" ]; then

#comprovem que mataheritrixos estigui corrent si no llavors l'encenem
if ! [ "$(pidof -x mataheritrixos.sh)" ]; then
nohup scripts/mataheritrixos.sh $datacenter&
fi

sqlite3 -init resource/init.sql  resource/dc4cities.sqlt "INSERT INTO working_modes VALUES ('"$data"', '"$activitywc"', '"$nextwm"', 'canviant');";

eszero=$(echo $nextwm|grep -c "${wcname}0")
if [ "$eszero" -eq "0" ]; then
        if ! [ "$(pidof -x estadistiqueswc.sh)" ]; then
           nohup scripts/estadistiqueswc.sh $datacenter 2>/dev/null&
        fi
fi

source ${home}/.rvm/scripts/rvm
rvm use $rubyversion 1>/dev/null


#control nodes on/off and migrate virtual machines
check_nodes_all_on

  #change between working modes, controlling the previous and actual working mode
  scripts/Taskwc.sh changewm $actualwm $nextwm $datacenter

sqlite3 -init resource/init.sql  resource/dc4cities.sqlt "DELETE FROM working_modes WHERE activity='"$activitywc"' AND date='"$data"' AND wm='"$actualwm"' AND status='canviat';"
sqlite3 -init resource/init.sql  resource/dc4cities.sqlt "UPDATE working_modes SET status='canviat' WHERE  activity='"$activitywc"' AND date='"$data"' AND wm='"$nextwm"' AND status='canviant';";

#mentre hi hagi un que encara esta canviant no fem res doncs potser encara esta migrant virtuals
encaracanviant=$(sqlite3 -init resource/init.sql  resource/dc4cities.sqlt "SELECT COUNT(*) FROM working_modes WHERE status='canviant'";)

while [ "$encaracanviant" -gt "0" ]
do
        sleep 5
        encaracanviant=$(sqlite3 -init resource/init.sql  resource/dc4cities.sqlt "SELECT COUNT(*) FROM working_modes WHERE status='canviant'";)
done
fi
check_nodes
