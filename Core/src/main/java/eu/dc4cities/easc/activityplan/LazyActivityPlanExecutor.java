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

package eu.dc4cities.easc.activityplan;

import eu.dc4cities.controlsystem.model.easc.Activity;
import eu.dc4cities.controlsystem.model.easc.ActivityDataCenter;
import eu.dc4cities.controlsystem.model.easc.EascActivityPlan;
import eu.dc4cities.controlsystem.model.easc.Work;
import eu.dc4cities.easc.Application;
import eu.dc4cities.easc.activity.DataCenterWorkingModes;
import eu.dc4cities.easc.workingmode.WorkingMode;
import eu.dc4cities.easc.workingmode.WorkingModeManager;
import org.joda.time.DateTime;
import org.jscience.physics.amount.Amount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.measure.unit.SI;
import java.util.ArrayList;
import java.util.List;

/**
 * A lazy implementation of the executor.
 * It considers the environment is reliable so there is no need to schedule the future switches.
 * We just need to switch to a new working mode (or the default one) if a new one must be started.
 *
 * This approach makes we don't really care about the real date. We only consider the first timeslot denotes
 * the present moment.
 */

public class LazyActivityPlanExecutor implements ActivityPlanExecutor {

    private Logger logger = LoggerFactory.getLogger(ActivityPlanExecutor.class);

    WorkingModeManager wmm;
	Application app;
	EascActivityPlan currentActivityPlan;
	
    public LazyActivityPlanExecutor(WorkingModeManager m, Application ap) {
        wmm = m;
        app = ap;
    }


    @Override
    public boolean executeActivityPlan(EascActivityPlan as, boolean b) {
    	
        for (Activity a : as.getActivities()) {
            //Get the first work to do (in case they are not sorted chronologically)
        	String activityName = a.getName();
        	for(ActivityDataCenter dc: a.getDataCenters()) {
        		Work toDo = dc.getWorks().get(0);
        		String dcName = dc.getDataCenterName();
        		for (Work w : dc.getWorks()) {
        			if (toDo == null || w.getStartTimeSlot() < toDo.getStartTimeSlot()) {
        				toDo = w;
        			}
        			if (toDo.getStartTimeSlot() == 0) {
        				//shortcut
        				break;
        			}
        		}

        		//Basically, we always consider we are at time 0.
        		//CHANGED this behavior: so we switch if the current working mode differ from the picked one that should start now.
        		if (toDo.getStartTimeSlot() == 0) {
        			//WorkingMode currWM = wmm.getCurrentWorkingMode(activityName, dcName);
        			//if ((currWM == null) ||	!toDo.getWorkingModeName().equals(currWM.getName())) {
					wmm.applyWorkingMode(activityName, dcName, toDo.getWorkingModeName());

        		} else {
        			//There is nothing to do for the moment
					wmm.applyDefaultWorkingMode(activityName, dcName);
        		}
        	}
        }
        
        this.currentActivityPlan = as;
        return true;
    }

	@Override
	public EascActivityPlan getActivityPlan() {
		// or simply return currentActivityPlan
		EascActivityPlan eap = new EascActivityPlan(app.getName());
		List<Activity> activities = new ArrayList<>();

        for(eu.dc4cities.easc.activity.Activity a : app.getActivities()) {
        	String activityName = a.getName();
        	Activity ctrlActivity = new Activity(activityName);
        	List<ActivityDataCenter> activityDCs = new ArrayList<>();
        	
        	for(DataCenterWorkingModes dc: a.getDataCenters()) {
        		WorkingMode currentWorkingMode = dc.getCurrentWorkingMode();
        		if(currentWorkingMode != null) {
            		List<Work> works = new ArrayList<>();
        			ActivityDataCenter ctrlDC = new ActivityDataCenter(dc.getDataCenterName());
        			//TODO to read power from Monitoring. We return only the first timeslot work, since the other works have not been scheduled by LazyScheduler
        			Work work = new Work(0, 1, currentWorkingMode.getName(), currentWorkingMode.getValue(), currentWorkingMode.getPerformanceLevels().get(0).getPower());
        			works.add(work);
        			ctrlDC.setWorks(works);
        			activityDCs.add(ctrlDC);
        		}
        		
        	}
        	
        	ctrlActivity.setDataCenters(activityDCs);
        	activities.add(ctrlActivity);
        }
		
        DateTime dateFrom = DateTime.now();
		eap.setActivities(activities);
		eap.setDateFrom(dateFrom);
		eap.setDateTo(dateFrom.plusMinutes(15));
		eap.setTimeSlotDuration(Amount.valueOf(15, SI.SECOND.times(60)));
		
		return eap;
	}

}
