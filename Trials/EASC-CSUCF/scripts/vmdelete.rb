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
   def connect(vmid, recreate, datacenter)

		servidorsactius=0


		begin

		one = One.new(datacenter)

		vm  =  OpenNebula::VirtualMachine.new_with_id(vmid, one.client)
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

vm = ARGV[0]
recreate = ARGV[1]
datacenter = ARGV[2]
infot = Status.new
infot.connect(vm, recreate, datacenter)
