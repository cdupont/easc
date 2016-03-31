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
   def connect(vmid, datacenter)

		servidorsactius=0

		begin

		one = One.new(datacenter)

		vm  =  OpenNebula::VirtualMachine.new_with_id(vmid, one.client)
		exit -1 if OpenNebula.is_error?(vm)
		vm.info
		status = vm.state_str

		rescue
			return -1
		ensure
			return status
		end

	end
end

vm = ARGV[0]
datacenter = ARGV[1]
infot = Status.new
puts infot.connect(vm, datacenter)
