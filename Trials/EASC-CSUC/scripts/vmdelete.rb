#!/usr/bin/ruby


##############################################################################
# Environment Configuration
##############################################################################
$: << "/home/ois/dc4cities/codioriginal/opennebula-4.4.1/src/oca/ruby"

##############################################################################
# Required libraries
##############################################################################
require 'opennebula'
include OpenNebula

		# OpenNebula credentials
		CREDENTIALS = "user:password"
		# XML_RPC endpoint where OpenNebula is listening
		ENDPOINT    = "http://sunstone:2633/RPC2"

		#correspondencia ips maquines

class Status 
   def connect(vmid, recreate)

		servidorsactius=0


		begin

		#part de opennebula
		client = Client.new(CREDENTIALS, ENDPOINT)

		vm  =  OpenNebula::VirtualMachine.new_with_id(vmid, client)
		exit -1 if OpenNebula.is_error?(vm)
		vm.info
		if (recreate == "true")
			vm.delete(true)
		else
			vm.delete
		end
		end

	end
end

v1 = ARGV[0]
infot = Status.new
infot.connect(ARGV[0], ARGV[1])
