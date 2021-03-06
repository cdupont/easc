#!/usr/bin/ruby


##############################################################################
# Environment Configuration
##############################################################################
$: << "/home/dc4cities/dc4cities/lib/opennebula-4.4.1/src/oca/ruby"

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
   def connect(vmid)

		servidorsactius=0


		begin

		#part de opennebula
		client = Client.new(CREDENTIALS, ENDPOINT)

		vm  =  OpenNebula::VirtualMachine.new_with_id(vmid, client)
		exit -1 if OpenNebula.is_error?(vm)
		vm.info
		status = vm.lcm_state_str

		rescue
			return -1
		ensure
			return status
		end

	end
end

v1 = ARGV[0]
infot = Status.new
puts infot.connect(v1)
