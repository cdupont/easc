package eu.dc4cities.easc.monitoring.zabbix;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

public final class ConstantsZabbix {

    private ConstantsZabbix() {
    }

    //public static final String ZABBIX_API_URL = "https://monitoratge.xgm.cesca.cat/api_jsonrpc.php";
    public static final String ZABBIX_API_URL = "http://zabbixip/zabbix/api_jsonrpc.php";
    public static final String zabbix_user = "user";
    public static final String zabbix_password = "password";
    public static boolean init=false;
    public static Map<String,String> machinenames = new HashMap<String, String>();
    public static List<String> machinesblade = new ArrayList<String>();
    public static Map<String,String> zabbixkeys = new HashMap<String, String>();

        public static void init(){
                if (!init){
                    machinenames.put("cluster00","Montserrat01");
                    machinenames.put("cluster01","Montserrat04");
                    machinenames.put("cluster02","Montserrat05");
                    machinenames.put("cluster03","Montserrat12");

                    machinesblade.add("Montserrat02");
                    machinesblade.add("Montserrat03");
                    machinesblade.add("Montserrat06");
                    machinesblade.add("Montserrat07");
                    machinesblade.add("Montserrat08");
                    machinesblade.add("Montserrat09");
                    machinesblade.add("Montserrat10");
                    machinesblade.add("Montserrat11");
                    machinesblade.add("Montserrat13");
                    machinesblade.add("Montserrat14");
                    machinesblade.add("Montserrat15");
                    machinesblade.add("Montserrat16");

                    zabbixkeys.put("totalpower","Voltage.xassis");
                    zabbixkeys.put("hostpower","iDrac.systemlevel");
                    zabbixkeys.put("processorload","system.cpu.util[,idle,avg1]");
                    zabbixkeys.put("totalmemory","vm.memory.size[total]");
                    zabbixkeys.put("freememory","vm.memory.size[free]");
                    zabbixkeys.put("cachedmemory","vm.memory.size[cached]");

                    init=true;
                }
        }

}
