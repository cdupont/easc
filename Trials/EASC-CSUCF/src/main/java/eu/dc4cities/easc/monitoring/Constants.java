package eu.dc4cities.easc.monitoring;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

public final class Constants {

    private Constants() {
    }

    //public static final String ZABBIX_API_URL = "https://monitoratge.xgm.cesca.cat/api_jsonrpc.php";
    public static Map<String,String> dc4citiesip = new HashMap<String, String>();
    public static Map<String,String> unitsInstant = new HashMap<String, String>();
    public static Map<String,String> unitsCumulative = new HashMap<String, String>();
    public static boolean init=false;

        public static void init(){
                if (!init){
			dc4citiesip.put("csuc_barcelona","localhost");
			dc4citiesip.put("imi_barcelona","10.0.200.23");

			unitsInstant.put("VideoTranscoding", "MB/min");
			unitsInstant.put("VideoTranscodingImi", "MB/min");
			unitsInstant.put("WebCrawling", "Webs/min");

			unitsCumulative.put("VideoTranscoding", "MB");
			unitsCumulative.put("VideoTranscodingImi", "MB");
			unitsCumulative.put("WebCrawling", "Webs");
                    init=true;
                }
        }

}
