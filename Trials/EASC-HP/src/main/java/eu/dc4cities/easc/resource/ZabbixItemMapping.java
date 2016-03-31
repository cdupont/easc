package eu.dc4cities.easc.resource;

import javax.measure.quantity.Quantity;
import javax.measure.unit.Unit;

/**
 * Holds the mapping between a given metric for an activity and data center and the corresponding item name in Zabbix.
 */
public class ZabbixItemMapping {

	private String metricName;
	private String activityName;
	private String dataCenterName;
	private String zabbixItemName;
	private Unit<? extends Quantity> unit;
	
	/**
	 * Returns the name of the metric the mapping refers to.
	 * 
	 * @return the metric name
	 */
	public String getMetricName() {
		return metricName;
	}

	public void setMetricName(String metricName) {
		this.metricName = metricName;
	}

	/**
	 * Returns the name of the activity the metric refers to.
	 * 
	 * @return the activity name
	 */
	public String getActivityName() {
		return activityName;
	}

	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}
	
	/**
	 * Returns the name of the data center the metric refers to.
	 * 
	 * @return the data center name
	 */
	public String getDataCenterName() {
		return dataCenterName;
	}

	public void setDataCenterName(String dataCenterName) {
		this.dataCenterName = dataCenterName;
	}

	/**
	 * Returns the name of the Zabbix item that measures the metric for the given data center and activity.
	 * 
	 * @return the zabbix item name
	 */
	public String getZabbixItemName() {
		return zabbixItemName;
	}

	public void setZabbixItemName(String zabbixItemName) {
		this.zabbixItemName = zabbixItemName;
	}
	
	/**
	 * Returns the unit in which the metric is expressed.
	 * 
	 * @return the unit for the metric
	 */
	public Unit<? extends Quantity> getUnit() {
		return unit;
	}

	public void setUnit(Unit<? extends Quantity> unit) {
		this.unit = unit;
	}
	
}
