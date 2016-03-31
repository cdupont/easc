package eu.dc4cities.easc.monitoring.zabbix;

import org.apache.http.entity.StringEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLContext;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustStrategy;
import java.io.IOException;
import java.security.cert.CertificateException;
import org.apache.http.config.Registry;
import org.apache.http.impl.client.HttpClientBuilder;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.time.LocalDateTime;

import eu.dc4cities.easc.monitoring.zabbix.MonitorInterface;

//This class should read informations from Zabbix
public class MonitorZabbix extends Thread implements MonitorInterface {

    private String AUTH_VALUE;
    private Map<String,Integer> serverPower=new HashMap<String,Integer>();
    private LocalDateTime last_reading=LocalDateTime.now();
        public MonitorZabbix(){
                ConstantsZabbix.init();
                authenticateUser();
        }

    //this method receives a jsonobject and send it to zabbix in order to retrieve some data
    private String executeRpcMethod(JSONObject jsonObj) {
        String Response = "";
        try {

                ////Only for development, just to avoid mess with https


            CloseableHttpClient httpClient = HttpClientBuilder.create().build();
            HttpPost request = new HttpPost(ConstantsZabbix.ZABBIX_API_URL);
            StringEntity params = new StringEntity(jsonObj.toString());
            request.addHeader("Content-Type", "application/json-rpc");
            request.setEntity(params);
            CloseableHttpResponse response = httpClient.execute(request);
            Response = EntityUtils.toString(response.getEntity());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Response;
    }

	//user authentication with user and password parameters
	//AUTH_VALUE will be set with the token password which can be used later for another calls
    private void authenticateUser() {
        try {
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("jsonrpc", "2.0");
            jsonObj.put("method", "user.login");
            jsonObj.put("params", (new JSONObject().put("user", ConstantsZabbix.zabbix_user).put("password", ConstantsZabbix.zabbix_password)));
            jsonObj.put("id", 0);

            String resultStr = executeRpcMethod(jsonObj);
            this.AUTH_VALUE = (String) (new JSONObject(resultStr)).get("result");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //it returns the hostid of the host that has the ip passed by parameter
    public Integer getHostId(String hostname){
        try{
        ConstantsZabbix.init();
        JSONObject getHostReqObj = new JSONObject();
        getHostReqObj.put("jsonrpc", "2.0");
        getHostReqObj.put("method", "host.get");
        getHostReqObj.put("id", 1);
        getHostReqObj.put("params", (new JSONObject().put("filter", (new JSONObject()).putOpt("host", hostname)).put("output", "extend")));

        getHostReqObj.put("auth", this.AUTH_VALUE);

        String responseStr = executeRpcMethod(getHostReqObj);
        JSONObject resObj = new JSONObject(responseStr);
        Object value = resObj.opt("result");

        if (value != null) {
            if (value instanceof JSONArray) {
                JSONArray hostsArray = (JSONArray) value;

                if (hostsArray.length() == 1) {
                    JSONObject hostObj = (JSONObject) hostsArray.opt(0);
                    return hostObj.getInt("hostid");
                }
            }
        }
        } catch (JSONException e){
                e.printStackTrace();
        }
        return null;
    }

    public int getValuefromKey(String hostname, String keyzabbix, long from, long till, boolean mitjana){

        int itemvalue = 0;
        try{
        String key = ConstantsZabbix.zabbixkeys.get(keyzabbix);
        JSONObject getHostReqObj = new JSONObject();
        getHostReqObj.put("jsonrpc", "2.0");
        getHostReqObj.put("method", "item.get");
        getHostReqObj.put("id", 2);

        getHostReqObj.put("params", (new JSONObject().put("filter", (new JSONObject()).putOpt("hostid", getHostId(hostname)).putOpt("key_", key)).put("output", "extend")));
        getHostReqObj.put("auth", this.AUTH_VALUE);

        String responseStr = executeRpcMethod(getHostReqObj);
        JSONObject resObj = new JSONObject(responseStr);

        Object value = resObj.opt("result");
        JSONArray itemsArray;
        int i=0;
        boolean found=false;
        JSONObject itemObj;
        String getValue;

        String itemid = getJSONValue(resObj, "itemid");
        String valueType = getJSONValue(resObj, "value_type");

        //pillem 10 minuts abans i agafarem l'últim valor que sigui més gran que 10

        if (itemid.length()>0){
                Collection colIt = new ArrayList();
                colIt.add(itemid);
                getHostReqObj = new JSONObject();
                getHostReqObj.put("jsonrpc", "2.0");
                getHostReqObj.put("method", "history.get");
                getHostReqObj.put("id", 2);
                getHostReqObj.put("params", (new JSONObject().put("history", valueType).put("time_from", from).put("time_till", till).put("itemids", colIt).put("output", "extend")));
                getHostReqObj.put("auth", this.AUTH_VALUE);
                responseStr = executeRpcMethod(getHostReqObj);
                resObj = new JSONObject(responseStr);
                value = resObj.opt("result");
                if (value != null) {
                        if (value instanceof JSONArray) {
                                itemsArray = (JSONArray) value;
                                i = 0;
                                found = false;
                                int quant=0;
                                int suma=0;
                                while (i < itemsArray.length() && !found) {
                                        itemObj = (JSONObject) itemsArray.opt(itemsArray.length()-i-1);
                                        getValue=(String)itemObj.opt("value");
                                        if ((new Float(getValue)).intValue()>0){
                                                itemvalue=(new Float(getValue)).intValue();
                                                if (mitjana){
                                                        quant++;
                                                        suma+=itemvalue;
                                                }else found=true;
                                        }
                                        i++;
                                }
                                if (quant>0) itemvalue=suma/quant;
                        }
                }
        }

        } catch (Exception e){
                e.printStackTrace();
        } finally {
                return itemvalue;
        }
}


private String getJSONValue(JSONObject resObj, String what){

	String itemvalue="";
	try{

        Object value = resObj.opt("result");

        if (value != null) {
            if (value instanceof JSONArray) {
                JSONArray itemsArray = (JSONArray) value;

		String jsonstr = itemsArray.toString();
		Pattern pattern = Pattern.compile("\""+what+"\":\"([^\"]+)\"");
		Matcher matcher = pattern.matcher(jsonstr);
		if (matcher.find()) itemvalue=matcher.group(1);

            }
        }

	} catch (JSONException e){
		e.printStackTrace();
	} finally {
		return itemvalue;
	}
    }

public int getTotalPower(String host, long from, long till){
        return getValuefromKey(host, "totalpower", from, till, true);
}

public int getServerPower(String host, long from, long till){
        if (ConstantsZabbix.machinenames.get(host)!=null) host=ConstantsZabbix.machinenames.get(host);
        if (serverPower.get(host)==null){
                serverPower.put(host, new Integer(getValuefromKey(host, "hostpower", from, till, true)));
        }
        return (serverPower.get(host).intValue());
}

}
