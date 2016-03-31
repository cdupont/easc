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
   def connect(vnid,operation,ip)


		#part de opennebula
		client = Client.new(CREDENTIALS, ENDPOINT)

		vn  =  OpenNebula::VirtualNetwork.new_with_id(vnid, client)
		exit -1 if OpenNebula.is_error?(vn)
		vn.info
		if (operation=="hold")
			vn.hold(ip)
		else
			vn.release(ip)
		end


	end
end

v1 = ARGV[0]
v2 = ARGV[1]
v3 = ARGV[2]
infot = Status.new
puts infot.connect(v1,v2,v3)
