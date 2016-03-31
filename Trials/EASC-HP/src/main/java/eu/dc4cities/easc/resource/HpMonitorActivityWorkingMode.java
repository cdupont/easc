package eu.dc4cities.easc.resource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Power;
import javax.measure.quantity.Quantity;

import org.jscience.physics.amount.Amount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.dc4cities.easc.monitoring.HttpMonitoring;
import eu.dc4cities.easc.monitoring.MonitorActivityWorkingMode;
import eu.dc4cities.easc.monitoring.WorkDone;

public class HpMonitorActivityWorkingMode extends WorkDone implements MonitorActivityWorkingMode {
	
	private static final String METRIC_INSTANT_BIZPERF = "instant_bizperf";
	private static final String METRIC_CUMULATIVE_BIZPERF = "cumulative_bizperf";
	private static final String METRIC_POWER = "power";
	
    private static Logger log = LoggerFactory.getLogger(HpMonitorActivityWorkingMode.class);
    
	private String zabbixHostId;
	private HttpMonitoring http;
	private Map<ZabbixItemMappingKey, ZabbixItemMapping> itemMappings;

	public HpMonitorActivityWorkingMode(EascHpConfiguration configuration) {
		super();
		http = new HttpMonitoring(configuration);
		if (!http.login()) {
			throw new RuntimeException("Zabbix authentication failed");
		}
		zabbixHostId = http.getHostId();
		log.debug("zabbixHostId: " + zabbixHostId);
		initMappings(configuration.getZabbixItemMappings());
	}

	private void initMappings(List<ZabbixItemMapping> mappings) {
		itemMappings = new HashMap<>();
		for (ZabbixItemMapping mapping : mappings) {
			itemMappings.put(new ZabbixItemMappingKey(mapping), mapping);
		}
	}
	
	@SuppressWarnings("unchecked")
	private <T extends Quantity> Amount<T> getMetric(String metric, String activity, String datacenter) {
		ZabbixItemMapping mapping = itemMappings.get(new ZabbixItemMappingKey(metric, activity, datacenter));
		if (mapping == null) {
			log.debug("No mapping found, assuming metric '" + metric + "' is not supported for activity '" + activity 
					+ "' in data center '" + datacenter + "'");
			return null;
		}
		String itemName = mapping.getZabbixItemName();
		HashMap<String, Object> filterItems = new HashMap<>();
		filterItems.put("hostid", zabbixHostId);
		String value = http.getValue(zabbixHostId, "name", itemName, filterItems); 
		log.debug(itemName + ": " + value);
		return (Amount<T>) Amount.valueOf(Double.valueOf(value), mapping.getUnit());
	}
	
	private <T extends Quantity> Amount<T> getMetricOrNull(String metric, String activity, String datacenter) {
		try {
			return getMetric(metric, activity, datacenter);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return null;
		}
	}
	
	@Override
	public Amount<?> getInstantBusinessPerformance(String activity,	String datacenter, String wm) {
		return getMetricOrNull(METRIC_INSTANT_BIZPERF, activity, datacenter);
	}

	@Override
	public Amount<Power> getWMPower(String activity, String datacenter, String wm) {
		return getMetricOrNull(METRIC_POWER, activity, datacenter);
	}

	@Override
	public Amount<Dimensionless> getActivityCumulativeBusinessItems(String activity, String datacenter) {
		return getMetricOrNull(METRIC_CUMULATIVE_BIZPERF, activity, datacenter);
	}

}
