#!/usr/bin/ruby


##############################################################################
# Environment Configuration
##############################################################################
$: << "lib/ruby/oca/opennebula-4.4.1/"

##############################################################################
# Required libraries
##############################################################################
load 'scripts/One.rb'

class Status 
   def connect(hostid, datacenter)


		begin

		one = One.new(datacenter)

		host  =  OpenNebula::Host.new_with_id(hostid, one.client)
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

host = ARGV[0]
datacenter = ARGV[1]
infot = Status.new
infot.connect(host, datacenter)
