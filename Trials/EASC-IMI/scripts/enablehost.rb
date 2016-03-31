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
   def connect(hostid)


		begin

		#part de opennebula
		client = Client.new(CREDENTIALS, ENDPOINT)

		host  =  OpenNebula::Host.new_with_id(hostid, client)
		exit -1 if OpenNebula.is_error?(host)
		host.info
		host.enable

		rescue
			return -1
		ensure
			return 0
		end

	end
end

v1 = ARGV[0]
infot = Status.new
infot.connect(v1)
