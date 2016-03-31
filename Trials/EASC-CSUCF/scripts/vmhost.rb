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


		begin

		one = One.new(datacenter)

		vm  =  OpenNebula::VirtualMachine.new_with_id(vmid, one.client)
		exit -1 if OpenNebula.is_error?(vm)
		vm.info

                node=vm.retrieve_elements("/VM/HISTORY_RECORDS/HISTORY[last()]/HOSTNAME")

		rescue
			return -1
		ensure
			return node
		end

	end
end

vm = ARGV[0]
datacenter = ARGV[1]
infot = Status.new
puts infot.connect(vm, datacenter)
