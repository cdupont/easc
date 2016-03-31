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
    public static Map<String,String> zabbixkeys = new HashMap<String, String>();

        public static void init(){
                if (!init){
                    machinenames.put("PDU","PDU_DC4Cities_IMI");

                    zabbixkeys.put("totalpower","pReal18");
                    zabbixkeys.put("processorload","system.cpu.util[,idle,avg1]");
                    zabbixkeys.put("totalmemory","vm.memory.size[total]");
                    zabbixkeys.put("freememory","vm.memory.size[free]");
                    zabbixkeys.put("cachedmemory","vm.memory.size[cached]");

                    init=true;
                }
        }

}
