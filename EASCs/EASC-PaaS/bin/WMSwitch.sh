#!/bin/bash
#Exemple script to switch working modes

WM=$1;
# Converting WM variable to lower-case
#WM=$( tr '[:upper:]' '[:lower:]' <<<"$WM" )

#if [ -z "${WM}" ] || ( [ "wm0" != "${WM}" ] 
fname="aFileToSeeIfIhaveBeenExecuted.file";
touch $fname;
echo "Switching to Working Mode $WM `date`" >> $fname;
#echo "Switching to Working Mode $WM `date`";

if [ "WM0" = "${WM}" ]; then
	cf scale helloworld-php -i 1 -k 200M -m 250M -f >> $fname;
	echo "Scaled to Working Mode $WM" >> $fname;
	touch wm0
elif [ "WM1" = "${WM}" ]; then
	cf scale helloworld-php -i 1 -k 300M -m 300M -f >> $fname;
	echo "Scaled to Working Mode $WM" >> $fname >> $fname;
	touch wm1
elif [ "WM2" = "${WM}" ]; then
	cf scale helloworld-php -i 2 -k 200M -m 250M -f >> $fname;
	echo "Scaled to Working Mode $WM" >> $fname >> $fname;
	touch wm2
elif [ "WM3" = "${WM}" ]; then
	cf scale helloworld-php -i 2 -k 300M -m 300M -f >> $fname;
	echo "Scaled to Working Mode $WM" >> $fname >> $fname;
	touch wm3
elif [ "WM4" = "${WM}" ]; then
	cf scale helloworld-php -i 3 -k 200M -m 250M -f >> $fname;
	echo "Scaled to Working Mode $WM" >> $fname >> $fname;
	touch wm4
fi 
