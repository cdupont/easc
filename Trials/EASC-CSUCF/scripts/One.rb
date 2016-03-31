#!/usr/bin/ruby
$: << "lib/ruby/oca/opennebula-4.4.1"
require 'opennebula'
require 'yaml'
include OpenNebula

class One
  def initialize(datacenter)  
	require 'yaml'
	config = YAML.load_file('resource/configruby.yaml')
	credentials = config[datacenter]['credentials']
	endpoint = config[datacenter]['endpoint']
        @client = Client.new(credentials, endpoint)
  end
  attr_reader :client
end
