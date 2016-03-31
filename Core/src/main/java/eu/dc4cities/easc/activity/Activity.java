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

package eu.dc4cities.easc.activity;

import eu.dc4cities.controlsystem.model.easc.ForbiddenState;
import eu.dc4cities.easc.sla.ServiceLevelObjective;
import org.jscience.physics.amount.Amount;

import java.util.List;

/**
 * An activity is a unit of software controlled by the EASC.
 * Its performance is measurable with a KPI (only one).
 */
public class Activity {
    private String name;
    private String businessUnit;
	private List<String> precedences;
	private String relocability;
	private String businessBucketStream;
	private Amount<?> migrationPerformanceCost;
	private List<ServiceLevelObjective> serviceLevelObjectives;
	private List<DataCenterWorkingModes> dataCenters;
	private List<ForbiddenState> forbiddenStates;

    public Activity() {
    	this.businessBucketStream = "no";
    }

    public Activity(String name) {
        this.name = name;
        this.businessBucketStream = "no";
    }

	public List<String> getPrecedences() {
		return precedences;
	}

	public void setPrecedences(List<String> precedences) {
		this.precedences = precedences;
	}

	public String getRelocability() {
		return relocability;
	}

	public void setRelocability(String relocability) {
		this.relocability = relocability;
	}

	public Amount<?> getMigrationPerformanceCost() {
		return migrationPerformanceCost;
	}

	public void setMigrationPerformanceCost(Amount<?> migrationPerformanceCost) {
		this.migrationPerformanceCost = migrationPerformanceCost;
	}

	public List<ServiceLevelObjective> getServiceLevelObjectives() {
		return serviceLevelObjectives;
	}

	public void setServiceLevelObjectives(List<ServiceLevelObjective> serviceLevelObjectives) {
		this.serviceLevelObjectives = serviceLevelObjectives;
	}

	public List<DataCenterWorkingModes> getDataCenters() {
		return dataCenters;
	}

	public void setDataCenters(List<DataCenterWorkingModes> dataCenters) {
		this.dataCenters = dataCenters;
	}

	public List<ForbiddenState> getForbiddenStates() {
		return forbiddenStates;
	}

	public void setForbiddenStates(List<ForbiddenState> forbiddenStates) {
		this.forbiddenStates = forbiddenStates;
	}

	public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
  
    public String getBusinessUnit() {
        return businessUnit;
    }

    public void setBusinessUnit(String businessUnit) {
        this.businessUnit = businessUnit;
    }

	public String getBusinessBucketStream() {
		return businessBucketStream;
	}

	public void setBusinessBucketStream(String businessBucketStream) {
		this.businessBucketStream = businessBucketStream;
	}

}