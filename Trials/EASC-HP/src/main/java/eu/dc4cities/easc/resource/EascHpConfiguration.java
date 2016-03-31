package eu.dc4cities.easc.resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.measure.unit.Unit;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.AbstractConstruct;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.Tag;

public class EascHpConfiguration {

	private String zabbixIp;
	private String zabbixApiUrl;
	private String zabbixUser;
	private String zabbixPassword;
	private List<ZabbixItemMapping> zabbixItemMappings;

	public String getZabbixIp() {
		return zabbixIp;
	}
	
	public void setZabbixIp(String zabbixIp) {
		this.zabbixIp = zabbixIp;
	}
	
	public String getZabbixApiUrl() {
		return zabbixApiUrl;
	}
	
	public void setZabbixApiUrl(String zabbixApiUrl) {
		this.zabbixApiUrl = zabbixApiUrl;
	}
	
	public String getZabbixUser() {
		return zabbixUser;
	}
	
	public void setZabbixUser(String zabbixUser) {
		this.zabbixUser = zabbixUser;
	}
	
	public String getZabbixPassword() {
		return zabbixPassword;
	}
	
	public void setZabbixPassword(String zabbixPassword) {
		this.zabbixPassword = zabbixPassword;
	}

	public List<ZabbixItemMapping> getZabbixItemMappings() {
		return zabbixItemMappings;
	}

	public void setZabbixItemMappings(List<ZabbixItemMapping> zabbixItemMappings) {
		this.zabbixItemMappings = zabbixItemMappings;
	}
	
	public static EascHpConfiguration from(String configPath) {
		eu.dc4cities.controlsystem.model.unit.Units.init();
		try (InputStream input = new FileInputStream(new File(configPath))) {
	        Yaml yaml = new Yaml(new UnitConstructor());
	        return (EascHpConfiguration) yaml.load(input);
	    } catch (IOException e) {
	    	throw new RuntimeException(e);
		}
	}
	
	private static class UnitConstructor extends Constructor {
		
	    public UnitConstructor() {
	        this.yamlConstructors.put(new Tag("!unit"), new ConstructUnit());
	    }

	    private class ConstructUnit extends AbstractConstruct {
	    	
	        public Object construct(Node node) {
	            String value = (String) constructScalar((ScalarNode) node);
	            return Unit.valueOf(value);
	        }
	        
	    }
	    
	}
	
}
