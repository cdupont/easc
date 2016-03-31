#!/bin/bash
###############################################################################
##
## This script is used to change working modes (WM) of the
## EASC-IaaS. Each WM will call a different caping using
## the Plug4Green caping mechanism. See below a list of
## available working modes with their description.
##
## WM0 aggressive   -> VM at 100%
## WM1 moderate     -> VM at 50%
##
## The working is passed as argument and must be a valid string.
##
##
###############################################################################

# Command line parameters, should invoke with working mode code and VM id.
WM=$1;
VM=$2;

# Logging file uses command name and parameters to ease debug.
LOGFILE="${0}-${1}-${2}.log"


## Output on the stdout and on a log file.
## @arg $1 a message to log
## @arg $2 the output filename
function echo_and_log(){
	echo $1
	echo $1 >> $2
}

## Print a small help message.
function print_help(){
	echo "Usage:"
	echo "    $0 <working_mode> <vm_id>"
	echo ""
	echo "    <working_mode> "
	echo "        code of the working mode, e.g. WM0, WM1"
	echo ""
	echo "    <vm_id>"
	echo "        VM id, depends on OpenStack"
	echo ""
}

## Check and only accept if the two arguments are give.
if [ $# != 2 ] ; then
	echo_and_log "ERROR! Missing parameters!" $LOGFILE
	print_help
	exit 1
fi

# Debug.
echo "Switching to Working Mode $WM `date`" >> $LOGFILE;

# Switch the working mode.
if [ "WM0" = "${WM}" ]; then
	echo_and_log "Switching to moderate mode, throlling to 100%!" $LOGFILE
	curl -i -X PUT -H "Content-Type: text/plain" -d 100 http://localhost:7777/v1/plug4green/${VM}/VMCPUDemand
elif [ "WM1" = "${WM}" ]; then
	echo_and_log "Switching to moderate mode, throlling to 50%!" $LOGFILE
	curl -i -X PUT -H "Content-Type: text/plain" -d 50 http://localhost:7777/v1/plug4green/${VM}/VMCPUDemand
else
	echo_and_log "ERROR! Unknonw working mode!" $LOGFILE
	exit 1
fi

# Debug.
echo_and_log "Scaled to Working Mode $WM" $LOGFILE
touch ${WM}
