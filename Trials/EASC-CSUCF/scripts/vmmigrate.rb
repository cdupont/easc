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
   def connect(vmid, host, datacenter)

		servidorsactius=0

		begin

		one = One.new(datacenter)

		vm  =  OpenNebula::VirtualMachine.new_with_id(vmid, one.client)
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

vm = ARGV[0]
host = ARGV[1]
datacenter = ARGV[2]
infot = Status.new
puts infot.connect(vm, host, datacenter)
