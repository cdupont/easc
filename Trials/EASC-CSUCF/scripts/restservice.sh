#!/bin/bash
rm -f out
mkfifo out
trap "rm -f out" EXIT
while true
do
  cat out | nc -l 8500 > >( # parse the netcat output, to build the answer redirected to the pipe "out".
    export REQUEST=
    while read line
    do
      line=$(echo "$line" | tr -d '[\r\n]')

      if echo "$line" | grep -qE '^GET /' # if line starts with "GET /"
      then
        REQUEST=$(echo "$line" | cut -d ' ' -f2) # extract the request
      elif [ "x$line" = x ] # empty line / end of request
      then
        HTTP_200="HTTP/1.1 200 OK"
        HTTP_LOCATION="Location:"
        HTTP_404="HTTP/1.1 404 Not Found"
        # call a script here
        # Note: REQUEST is exported, so the script can parse it (to answer 200/403/404 status code + content)
#curl -i http://localhost:8500/changeWM?activity=VideoTranscoding\&wm=$1
        if echo $REQUEST | grep -qE '^/changeWM'
        then
#changeWM?activity=VideoTranscoding\&wm=$wm\&datacenter=$datacenter
		activity=$(echo $REQUEST|grep -o "activity=.*&wm" | sed s,"activity=",,g| sed s,"&wm",,g)
                wm=$(echo $REQUEST|grep -o "wm=.*&datacenter" | sed s,"wm=",,g| sed s,"&datacenter",,g)
		datacenter=$(echo $REQUEST|grep -o "datacenter=.*"|sed s,"datacenter=",,g)
		if [ "$activity" = "VideoTranscoding" ]; then
			scripts/WMSwitchvt.sh $wm $datacenter
		else
			scripts/WMSwitchwc.sh $wm $datacenter
		fi
            printf "%s\n%s %s\n\n%s\n" "$HTTP_200" "$HTTP_LOCATION" $REQUEST "Switched to $wm" > out
	elif echo $REQUEST | grep -qE '^/getWMStatistics'
	then
		activity=$(echo $REQUEST|grep -o "activity=.*&type" | sed s,"activity=",,g| sed s,"&type",,g)
		type=$(echo $REQUEST|grep -o "type=.*&datacenter" | sed s,"type=",,g| sed s,"&datacenter",,g)
		datacenter=$(echo $REQUEST|grep -o "datacenter=.*"|sed s,"datacenter=",,g)
		resultat=$(scripts/getWMStatistics.sh $activity $type $datacenter)
		printf "%s\n%s %s\n\n%s\n" "$HTTP_200" "$HTTP_LOCATION" $REQUEST $resultat > out
        else
            printf "%s\n%s %s\n\n%s\n" "$HTTP_404" "$HTTP_LOCATION" $REQUEST "Resource $REQUEST NOT FOUND!" > out
        fi
      fi
    done
  )
done
