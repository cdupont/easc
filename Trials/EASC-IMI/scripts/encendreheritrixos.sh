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
        echo "encenent heritrix de la maquina $i" >> /tmp/heritrix.log
        codi="000"
        piretrixok=1
        while [ "$piretrixok" -eq "1" ]
        do
          piretrixok=$(nc -z -w5 $i 22; echo $?)
        sleep 2
        done

        ssh -i /home/dc4cities/.ssh/user/id_rsa -t -t -o "StrictHostKeyChecking no" -n user@$IPPIRETRIX -p22 "sudo /dades/eines/SE2/loader/dc4cities.sh $i $webaindexar" >> /tmp/heritrix.log&
        sleep 10

}


    a=("$@")
    ((last_idx=${#a[@]} - 1))
    IPPIRETRIX=${a[last_idx]}
    unset a[last_idx]

#detectem que el piretrix ja esta ences
codi="000"
piretrixok=1
while [ "$piretrixok" -eq "1" ]
do
  piretrixok=$(nc -z -w5 $IPPIRETRIX 22; echo $?)
sleep 2
done

###
#encenem heritrixos i els hi passem la ip i la web a indexar
for i in "${a[@]}" ; do
	encenheritrix $i
done
