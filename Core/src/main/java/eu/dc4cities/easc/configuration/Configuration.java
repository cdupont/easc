/*
 * Copyright 2016 The DC4Cities author.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.dc4cities.easc.configuration;

import org.jscience.physics.amount.Amount;

import javax.measure.quantity.Duration;

/**
 * Configuration class is used to parse the configuration of the files into memory.
 * The parse is doing reading the Config.yaml file.
 *
 */
public class Configuration {
	

	private String siteCode = Constants.DEFAULT_SITE_CODE;

    private String EASCName = Constants.DEFAULT_APP_NAME;
    private Integer port = Constants.DEFAULT_APP_PORT;

    private String energisURL = Constants.DEFAULT_ENERGIS_URL;
	private Integer energisPort = Constants.DEFAULT_ENERGIS_PORT;
	private Amount<Duration> monitoringSamplingTime = Constants.DEFAULT_MONIT_SAMPLING_TIME;

    private String energisExecuteURL = Constants.DEFAULT_ENERGIS_EXECUTE_URL;
	private Integer energisExecutePort = Constants.DEFAULT_ENERGIS_EXECUTE_PORT;
	private String energyService = Constants.DEFAULT_ENERGY_SERVICE;


	public Configuration() {
    }

	public Configuration(
			String EASCName,
			Integer port,
			String energisURL,
			Integer energisPort,
			String energisExecuteURL,
			Integer energisExecutePort) {
		this.EASCName = EASCName;
		this.port = port;
		this.energisURL = energisURL;
        this.energisPort = energisPort;
        this.energisExecuteURL = energisExecuteURL;
        this.energisExecutePort = energisExecutePort;
        
    }
	
    public Integer getPort() { return port; }

    public void setPort(Integer port) { this.port = port; }

    public String getEASCName() { return EASCName; }

	public void setEASCName(String EASCName) { this.EASCName = EASCName; }

	public String getEnergisURL() {
		return energisURL;
	}

	public void setEnergisURL(String energisURL) {
		this.energisURL = energisURL;
	}

    public Integer getEnergisPort() {
		return energisPort;
	}

	public void setEnergisPort(Integer energisPort) {
		this.energisPort = energisPort;
	}
	
	public String getEnergisExecuteURL() {
		return energisExecuteURL;
	}

	public void setEnergisExecuteURL(String energisExecuteURL) {
		this.energisExecuteURL = energisExecuteURL;
	}
	 
    public Integer getEnergisExecutePort() {
		return energisExecutePort;
	}

	public void setEnergisExecutePort(Integer energisExecutePort) {
		this.energisExecutePort = energisExecutePort;
	}

    public String getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(String siteCode) {
        this.siteCode = siteCode;
    }

    public Amount<Duration> getMonitoringSamplingTime() {
        return monitoringSamplingTime;
    }

    public void setMonitoringSamplingTime(Amount<Duration> monitoringSamplingTime) {
        this.monitoringSamplingTime = monitoringSamplingTime;
    }

	public String getEnergyService() {
		return energyService;
	}

	public void setEnergyService(String energyService) {
		this.energyService = energyService;
	}

}
