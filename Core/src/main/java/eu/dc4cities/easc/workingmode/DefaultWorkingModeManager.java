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

import eu.dc4cities.easc.Application;
import eu.dc4cities.easc.activity.Activity;
import eu.dc4cities.easc.activity.DataCenterWorkingModes;

import java.util.List;
import java.util.stream.Collectors;

/**
 * default implem for WMM
 */
public class DefaultWorkingModeManager implements WorkingModeManager {
    Application app;

    public DefaultWorkingModeManager(Application app) {
        this.app = app;
    }
    
    public DataCenterWorkingModes getDataCenter(String activityName, String dcName) {
    	for(Activity act: app.getActivities())
    		if(act.getName().equalsIgnoreCase(activityName))
    			for(DataCenterWorkingModes d: act.getDataCenters())
    				if(d.getDataCenterName().equalsIgnoreCase(dcName))
    					return d;
    	
    	return null;
    }
    
    public WorkingMode getWorkingMode(DataCenterWorkingModes dc, String wmn) {
    	for(WorkingMode w: dc.getWorkingModes())
    		if(w.getName().equalsIgnoreCase(wmn))
    			return w;
    	return null;
    }
    
    @Override
    public boolean applyWorkingMode(String activityName, String dcName, String wmn) {

		DataCenterWorkingModes dc = getDataCenter(activityName, dcName);
		WorkingMode wm = getWorkingMode(dc, wmn);
		boolean ret = wm.getActuator().activateWorkingMode(dc, wm);

        return ret;
    }

	@Override
	public WorkingMode getCurrentWorkingMode(String activityName, String dcName) {
        DataCenterWorkingModes dc = getDataCenter(activityName, dcName);
 		return dc.getCurrentWorkingMode();
	}

	@Override
	public WorkingMode getDefaultWorkingMode(String activityName, String dcName) {
        DataCenterWorkingModes dc;
		
 		dc = getDataCenter(activityName, dcName);
 		return getWorkingMode(dc, dc.getDefaultWorkingMode());
	}

	@Override
	public void applyDefaultWorkingMode(String activityName, String dcName) {

		DataCenterWorkingModes dc = getDataCenter(activityName, dcName);
		WorkingMode wm = getWorkingMode(dc, dc.getDefaultWorkingMode());
	    wm.getActuator().activateWorkingMode(dc, wm);

	}

	@Override
	public List<WorkingMode> getAllCurrentWorkingMode() {
		return app.getActivities().stream().flatMap(a -> a.getDataCenters().stream()).map(DataCenterWorkingModes::getCurrentWorkingMode).collect(Collectors.toList());
	}

	@Override
	public List<WorkingMode> getWorkingModes(String activityName, String dcName) {
		DataCenterWorkingModes dc;
		
		dc = getDataCenter(activityName, dcName);
		return dc.getWorkingModes();
	}

}
