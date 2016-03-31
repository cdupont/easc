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
   def connect(vmid, host)

		servidorsactius=0


		begin

		#part de opennebula
		client = Client.new(CREDENTIALS, ENDPOINT)

		vm  =  OpenNebula::VirtualMachine.new_with_id(vmid, client)
		exit -1 if OpenNebula.is_error?(vm)
		vm.info
		vm.migrate(host)

		rescue
			return -1
		ensure
			return 0
		end

	end
end

v1 = ARGV[0]
v2 = ARGV[1]
infot = Status.new
puts infot.connect(v1, v2)
