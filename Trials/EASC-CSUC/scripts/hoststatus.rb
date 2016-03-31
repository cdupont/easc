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
   def connect(hostid)


		begin

		#part de opennebula
		client = Client.new(CREDENTIALS, ENDPOINT)

		host  =  OpenNebula::Host.new_with_id(hostid, client)
		exit -1 if OpenNebula.is_error?(host)
		host.info
		status = host.short_state_str

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
