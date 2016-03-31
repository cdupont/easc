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

package eu.dc4cities.easc.workingmode;

import eu.dc4cities.easc.resource.Resource;

import java.util.ArrayList;
import java.util.Collection;

/**
 * WM for EASC.
 * It contains a bit more infos than reported in CTRL.
 */
public class WorkingMode extends eu.dc4cities.controlsystem.model.easc.WorkingMode {

    private WorkingModeActuator actuator;
    private Collection<Resource> resources;

	public WorkingMode() {
		super();
		resources = new ArrayList<>();
	}
	
	public WorkingMode(String name, int value) {
		super(name, value);
		this.resources = new ArrayList<>();
        this.actuator = new WorkingModeActuator();
	}
    
    public WorkingModeActuator getActuator() {
        return actuator;
    }

    public void setActuator(WorkingModeActuator actuator) {
        this.actuator = actuator;
    }


    public Collection<Resource> getResources() {
        return resources;
    }

    public void setResources(Collection<Resource> resources) {
        this.resources = resources;
    }

}