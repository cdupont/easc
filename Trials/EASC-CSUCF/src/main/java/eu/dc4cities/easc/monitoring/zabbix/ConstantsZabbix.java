package eu.dc4cities.easc.monitoring.zabbix;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

public final class ConstantsZabbix {

    private ConstantsZabbix() {
    }

    //public static final String ZABBIX_API_URL = "https://monitoratge.xgm.cesca.cat/api_jsonrpc.php";
    public static final String zabbix_user = "user";
    public static final String zabbix_password = "password";
    public static boolean init=false;
    public static Map<String,String> machinenames = new HashMap<String, String>();
    public static List<String> machinesblade = new ArrayList<String>();
    public static Map<String,String> zabbixkeys = new HashMap<String, String>();
    public static Map<String,String> zabbixips = new HashMap<String, String>();
    public static Map<String,String> zabbixuser = new HashMap<String, String>();
    public static Map<String,String> zabbixpassword = new HashMap<String, String>();

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

                    zabbixkeys.put("csuc_barcelona.totalpower","Voltage.xassis");
                    zabbixkeys.put("csuc_barcelona.hostpower","iDrac.systemlevel");

                    zabbixkeys.put("imi_barcelona.totalpower","pReal18");

		    zabbixips.put("csuc_barcelona","http://zabbixipdc1/zabbix/api_jsonrpc.php");
		    zabbixips.put("imi_barcelona","http://zabbixipdc2/zabbix/api_jsonrpc.php");

		    zabbixuser.put("csuc_barcelona","user");
		    zabbixpassword.put("csuc_barcelona","password");

                    zabbixuser.put("imi_barcelona","user");
                    zabbixpassword.put("imi_barcelona","password");

                    init=true;
                }
        }

}
