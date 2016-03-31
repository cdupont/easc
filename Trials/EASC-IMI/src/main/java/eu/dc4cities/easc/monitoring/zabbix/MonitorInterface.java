package eu.dc4cities.easc.monitoring.zabbix;

//inteface that defines the functions of the monitor zabbix class

//Example of use:
//1.- Authenticate the user in zabbix
//	Monitor mon = new Monitor(); mon.authenticateUser();
//2.- Ask the hostid from ip of the host that we are going to monitor
//	int hostid = mon.getHostId("172.25.4.19").intValue();
//3.- Ask the last value registered of a concrete item from a host (defined in the class ConstantsZabbix.java)
//	System.out.println(mon.getValuefromKey(hostid, ConstantsZabbix.hostpower));

public interface MonitorInterface {

    //it returns the hostid of the host that has the ip passed by parameter
    public Integer getHostId(String hostname);

    //gets the lastvalue that has stored zabbix from an item of a host which is defined with a key
    //parameters:
    //@hostid = hostid of the asked host
    //@key = key that defines the correspondent item of that host asked
    public int getValuefromKey(String hostname, String keyzabbix, long from, long till, boolean mitjana);

}
