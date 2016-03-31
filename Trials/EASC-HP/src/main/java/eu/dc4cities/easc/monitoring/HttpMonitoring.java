package eu.dc4cities.easc.monitoring;

import io.github.hengyunabc.zabbix.api.DefaultZabbixApi;
import io.github.hengyunabc.zabbix.api.Request;
import io.github.hengyunabc.zabbix.api.RequestBuilder;
import io.github.hengyunabc.zabbix.api.ZabbixApi;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;

import eu.dc4cities.easc.Main;
import eu.dc4cities.easc.resource.EascHpConfiguration;

public class HttpMonitoring {
	
    private static Logger log = LoggerFactory.getLogger(Main.class);
	
	private String zabbixIp;
	private String zabbix_api_url;
    private String zabbix_user;
    private String zabbix_password;
    private ZabbixApi zabbixApi;
    
    ObjectMapper mapper = new ObjectMapper();

	public HttpMonitoring(EascHpConfiguration configuration) {
		this.zabbix_user = configuration.getZabbixUser();
		this.zabbix_password = configuration.getZabbixPassword();
		this.zabbixIp = configuration.getZabbixIp();
		this.zabbix_api_url = configuration.getZabbixApiUrl();
		zabbixApi = new DefaultZabbixApi(zabbix_api_url);
		zabbixApi.init();		
	}

	public String getZabbixIp() {
		return zabbixIp;
	}

	public void setZabbixIp(String zabbixIp) {
		this.zabbixIp = zabbixIp;
	}
	
	public boolean login(){
		return this.login(zabbix_user, zabbix_password);
	}
	
	public boolean login(String username, String password){
		try {
			boolean login = zabbixApi.login(zabbix_user, zabbix_password);
			log.debug("login:" + login);		
			return login;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return false;
		}
	}
	
	public String getHostId() {
		return getHostId(zabbixIp);
	}
	
	public String getHostId(String host) {
		try {
			JSONObject filter = new JSONObject();

			filter.put("ip", new String[] { host });
			Request getRequest = RequestBuilder.newBuilder().method("host.get")
					.paramEntry("filter", filter).build();
			JSONObject getResponse = zabbixApi.call(getRequest);
			if(getResponse.containsKey("error")){
				String error = (String)getResponse.getJSONObject("error").get("data");
				if(error.equals("Not authorized")){
				log.error("Error in accessing zabbix: trying to re-login to update authentication token");
					this.login(zabbix_user, zabbix_password);
					getResponse = zabbixApi.call(getRequest);
				}
			}
			String hostid = getResponse.getJSONArray("result").getJSONObject(0)
					.getString("hostid");
			return hostid;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return null;
		}
	}
	
	public String getValue(String hostid, String itemProperty, String itemValue, Map<String, Object> filterItems) {
		JSONObject filter = new JSONObject();
		filter = new JSONObject();
		filter.put(itemProperty, itemValue);
		if(filterItems!=null){
			for(String key : filterItems.keySet()){
				filter.put(key, filterItems.get(key));
			}
		}
		Request getItemRequest = RequestBuilder.newBuilder().method("item.get")
				.paramEntry("filter", filter).paramEntry("hostid", hostid).paramEntry("output", "extend").build();
		JSONObject getItemResponse;
		try {
			getItemResponse = zabbixApi.call(getItemRequest);
			if(getItemResponse.containsKey("error")){
				String error = (String)getItemResponse.getJSONObject("error").get("data");
				if(error.equals("Not authorized")){
				log.error("Error in accessing zabbix: trying to re-login to update authentication token");
					this.login(zabbix_user, zabbix_password);
					getItemResponse = zabbixApi.call(getItemRequest);
				}
			}
			String value = getItemResponse.getJSONArray("result").getJSONObject(0).getString("lastvalue");
			return value;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error(e.getMessage(), e);
			return null;
		}
				
	}
	
}
