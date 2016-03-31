#!/bin/bash

WM=${1}
# Converting WM variable to lower-case
WM=$( tr '[:upper:]' '[:lower:]' <<<"$WM" )
SERVER="localhost"

HELP="\nHELP: This script is used to switch the Working Mode of the report generation\n\nUsage: bash ${0##*/} wm_to_switch_to \n"

if [ -z "${WM}" ] || ( [ "wm0" != "${WM}" ] && [ "wm1" != "${WM}" ] && [ "wm7" != "${WM}" ] && [ "wm14" != "${WM}" ] && [ "wm4" != "${WM}" ] && [ "wm10" != "${WM}" ] && [ "wm13" != "${WM}" ] ); then
echo "Please provide a valid working mode: WM0, WM1, WM7, WM14"
echo -e $HELP
exit
fi

echo -e "Requesting working mode ${WM}...\n"
# Request wm change via REST API or other interface to the worker manager and analyze the result
wget -O /dev/null "http://${SERVER}:8080/tn-trial-web/TrialController?wm=${WM}&operation=switch&submit=submit"
echo -e "Working mode changed successfully. \n"
