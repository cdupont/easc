package eu.dc4cities.easc.resource;

/**
 * A key to identify a Zabbix item mapping.
 * 
 * @see ZabbixItemMapping
 */
public class ZabbixItemMappingKey {

	private String metricName;
	private String activityName;
	private String dataCenterName;
	
	public ZabbixItemMappingKey(String metricName, String activityName, String dataCenterName) {
		this.metricName = metricName;
		this.activityName = activityName;
		this.dataCenterName = dataCenterName;
	}

	public ZabbixItemMappingKey(ZabbixItemMapping mapping) {
		this(mapping.getMetricName(), mapping.getActivityName(), mapping.getDataCenterName());
	}
	
	public String getMetricName() {
		return metricName;
	}
	
	public String getActivityName() {
		return activityName;
	}
	
	public String getDataCenterName() {
		return dataCenterName;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ZabbixItemMappingKey)) {
			return false;
		}
		ZabbixItemMappingKey other = (ZabbixItemMappingKey) obj;
		return metricName.equals(other.metricName)
				&& activityName.equals(other.activityName)
				&& dataCenterName.equals(other.dataCenterName);
	}
	
	@Override
	public int hashCode() {
		return metricName.hashCode() + activityName.hashCode() + dataCenterName.hashCode();
	}
	
}
