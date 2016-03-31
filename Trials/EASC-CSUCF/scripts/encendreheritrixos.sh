#!/bin/bash

function encenheritrix(){
i=$1

###
#encenem heritrixos i els hi passem la ip i la web a indexar
        webaindexar=$(head -n1 resource/websaindexar.txt)
        linies=$(wc -l resource/websaindexar.txt | awk '{print $1}')
        ((linies--))
        tail -n$linies resource/websaindexar.txt > resource/temp.txt
        mv resource/temp.txt resource/websaindexar.txt
        codi="000"
        piretrixok=1
        while [ "$piretrixok" -eq "1" ]
        do
          piretrixok=$(nc -z -w5 $i 22; echo $?)
        sleep 2
        done

        ssh -i resource/.ssh/user/id_rsa -t -t -o "StrictHostKeyChecking no" -n user@$piretrixip -p$piretrixport "sudo /dades/eines/SE2/loader/dc4cities.sh $i $webaindexar" > /tmp/heritrix.log&
        sleep 10

}


    a=("$@")
    ((last_idx=${#a[@]} - 1))
    datacenter=${a[last_idx]}
    unset a[last_idx]

# include parse_yaml function
. scripts/parse_yaml.sh

# read yaml file
eval $(parse_yaml resource/configruby.yaml "config_")
piretrixip=$(eval echo \$config_$datacenter\_piretrixip)
piretrixport=$(eval echo \$config_$datacenter\_piretrixport)

#detectem que el piretrix ja esta ences
codi="000"
piretrixok=1
while [ "$piretrixok" -eq "1" ]
do
  piretrixok=$(nc -z -w5 $piretrixip $piretrixport; echo $?)
sleep 2
done

###
#encenem heritrixos i els hi passem la ip i la web a indexar
for i in "${a[@]}" ; do
	encenheritrix $i
done
