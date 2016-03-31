#!/usr/bin/ruby


##############################################################################
# Environment Configuration
##############################################################################
$: << "lib/ruby/oca/opennebula-4.4.1"

##############################################################################
# Required libraries
##############################################################################
load 'scripts/One.rb'

class Status 
   def connect(vnid,operation,ip,datacenter)

		one = One.new(datacenter)

		vn  =  OpenNebula::VirtualNetwork.new_with_id(vnid, one.client)
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
v4 = ARGV[3]
infot = Status.new
puts infot.connect(v1,v2,v3,v4)
