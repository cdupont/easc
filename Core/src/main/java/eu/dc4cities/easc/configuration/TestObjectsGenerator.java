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

import eu.dc4cities.controlsystem.model.easc.*;
import eu.dc4cities.easc.Application;
import eu.dc4cities.easc.activity.DataCenterWorkingModes;
import eu.dc4cities.easc.resource.CFApplication;
import eu.dc4cities.easc.resource.Resource;
import eu.dc4cities.easc.resource.Units;
import eu.dc4cities.easc.workingmode.WorkingMode;
import eu.dc4cities.easc.workingmode.WorkingModeActuator;
import org.joda.time.DateTime;
import org.jscience.physics.amount.Amount;

import javax.measure.quantity.DataAmount;
import javax.measure.unit.SI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static javax.measure.unit.SI.WATT;
import static javax.measure.unit.Unit.ONE;

/**
 * This class generates various objects for testing
 */
public class TestObjectsGenerator {
    private static DateFormat df = new SimpleDateFormat("d/MM/YYYY HH:mm:ss");

	public static EascActivityPlan newActivityPlan(Work ... works) {
		Activity a = new Activity("test");
		List<ActivityDataCenter> DCs = new ArrayList<>();
		ActivityDataCenter dc = new ActivityDataCenter("dctest1");
		
		DCs.add(dc);
		dc.setWorks(Arrays.asList(works));
		a.setDataCenters(DCs);
		
		EascActivityPlan ap = new EascActivityPlan("test");
		ap.setTimeSlotDuration(Amount.valueOf(15, SI.SECOND.times(60)));
		ap.setDateFrom(new DateTime());
		ap.setDateTo(new DateTime().plusDays(1));
		ap.setActivities(Arrays.asList(a));
		return ap;
	}

	public static Application createAppForLazyExecutorTest(final List<String> order) {
        Application appl = new Application();
        List<eu.dc4cities.easc.activity.Activity> activities = new ArrayList<eu.dc4cities.easc.activity.Activity>();

        appl.setName("testApp");

        //a
        eu.dc4cities.easc.activity.Activity a = new eu.dc4cities.easc.activity.Activity("test");
        a.setBusinessUnit("Page");
        Amount<?> migrationPerformanceCost = Amount.valueOf(3, ONE.alternate("Page"));
        a.setMigrationPerformanceCost(migrationPerformanceCost);
        a.setRelocability(Relocability.SPREADABLE.value());
        eu.dc4cities.easc.sla.ServiceLevelObjective serviceLevelObjective = new eu.dc4cities.easc.sla.ServiceLevelObjective();
        //serviceLevelObjective.setBasePrice(priceAmount.valueOf(100, ONE.alternate("EUR")));
        serviceLevelObjective.setCumulativeBusinessObjective(Amount.valueOf(833, ONE.alternate("Page")));
        List<eu.dc4cities.easc.sla.ServiceLevelObjective> serviceLevelObjectives = new ArrayList<>();
        serviceLevelObjectives.add(serviceLevelObjective);
		//			serviceLevelObjective.setPriceModifiers(priceModifiers);
        a.setServiceLevelObjectives(serviceLevelObjectives );

        List<DataCenterWorkingModes> activityDCs = new ArrayList<DataCenterWorkingModes>();
        //dc
        DataCenterWorkingModes dc = new DataCenterWorkingModes();
        dc.setDataCenterName("dctest1");
        List<WorkingMode> works = new ArrayList<WorkingMode>();
        works.add(createWorkingModeForLazyExecutorTest("a", 0, 0, 0, order));
        works.add(createWorkingModeForLazyExecutorTest("b", 1, 10, 10, order));
        works.add(createWorkingModeForLazyExecutorTest("c", 2, 20, 20, order));

        dc.setWorkingModes(works);
        dc.setDefaultWorkingMode("a");
        activityDCs.add(dc);

        a.setDataCenters(activityDCs);
        activities.add(a);
		appl.setActivities(activities);

		return appl;
	}

	private static WorkingMode createWorkingModeForLazyExecutorTest(final String name, int value, int bizPerf, int power, final List<String> order) {	
		WorkingMode work = new WorkingMode(name, value);
		
		WorkingModeActuator actuator = new WorkingModeActuator() {
			@Override
			public boolean activateWorkingMode(DataCenterWorkingModes dcwms, WorkingMode wm) {
				System.out.println(df.format(new Date()) + " " + name);
				order.add(name);
				return true;
			}
		};
		
		work.setActuator(actuator);
		List<PerformanceLevel> performanceLevels = new ArrayList<>();
		PerformanceLevel perfL = new PerformanceLevel();
		perfL.setBusinessPerformance(Amount.valueOf(bizPerf, ONE.alternate("Page")));
		perfL.setPower(Amount.valueOf(power, WATT));
		performanceLevels.add(perfL);
		work.setPerformanceLevels(performanceLevels);
		return work;
	}

	public static Work W(String id, int value, int startAt, int endAt) {
		return new Work(startAt, endAt, id, value, Amount.valueOf(1, WATT));
	}

	public static EascActivityPlan createEascActivityPlan() {
		EascActivityPlan eap = new EascActivityPlan("testApp");
		List<Activity> activities = new ArrayList<Activity>();
		DateTime dateFrom = DateTime.now();

		eap.setDateFrom(dateFrom);
		eap.setDateTo(dateFrom.plusMinutes(15));
		eap.setTimeSlotDuration(Amount.valueOf(15, SI.SECOND.times(60)));

		for(int i = 1; i <= 3; i++) {
			Activity a = new Activity("test" + i);
			List<ActivityDataCenter> activityDCs = new ArrayList<ActivityDataCenter>();

			for(int dcId = 1; dcId <= 2; dcId++) {
				ActivityDataCenter ctrlDC = new ActivityDataCenter("dctest" + dcId);
				List<Work> works = new ArrayList<Work>();
				Work work = new Work(0, 1, "wmtest2", 2, Amount.valueOf(20, SI.WATT));
				works.add(work);
				work = new Work(1, 5, "wmtest1", 1, Amount.valueOf(10, SI.WATT));
				works.add(work);

				ctrlDC.setWorks(works);
				activityDCs.add(ctrlDC);
			}

			a.setDataCenters(activityDCs);

			activities.add(a);
		}

		eap.setActivities(activities);
		return eap;
	}
	
	public static Application createApp() {
        Application appl = new Application();
        List<eu.dc4cities.easc.activity.Activity> activities = new ArrayList<>();

        appl.setName("testApp");

        //a
        for(int i = 1; i <= 3; i++) {
			eu.dc4cities.easc.activity.Activity a = new eu.dc4cities.easc.activity.Activity("test" + i);
			a.setBusinessUnit("Page");
			Amount<?> migrationPerformanceCost = Amount.valueOf(3, ONE.alternate("Page"));
			a.setMigrationPerformanceCost(migrationPerformanceCost);
			a.setRelocability(Relocability.SPREADABLE.value());
			eu.dc4cities.easc.sla.ServiceLevelObjective serviceLevelObjective = new eu.dc4cities.easc.sla.ServiceLevelObjective();
			//serviceLevelObjective.setBasePrice(priceAmount.valueOf(100, ONE.alternate("EUR")));
			serviceLevelObjective.setCumulativeBusinessObjective(Amount.valueOf(833, ONE.alternate("Page")));
			List<eu.dc4cities.easc.sla.ServiceLevelObjective> serviceLevelObjectives = new ArrayList<>();
			serviceLevelObjectives.add(serviceLevelObjective);
			//			serviceLevelObjective.setPriceModifiers(priceModifiers);
			a.setServiceLevelObjectives(serviceLevelObjectives);

			List<DataCenterWorkingModes> activityDCs = new ArrayList<DataCenterWorkingModes>();
			//dc
			for(int dcId = 1; dcId <= 2; dcId++) {
				DataCenterWorkingModes dc = new DataCenterWorkingModes();
				dc.setDataCenterName("dctest" + dcId);
				List<WorkingMode> works = new ArrayList<>();
				works.add(createWorkingMode("wmtest0", 0, "echo wmtest0", 0, 0));
				works.add(createWorkingMode("wmtest1", 1, "echo wmtest1", 10, 10));
				works.add(createWorkingMode("wmtest2", 2, "echo wmtest2", 20, 20));

				dc.setWorkingModes(works);
				dc.setDefaultWorkingMode("wmtest0");
				dc.setCurrentWorkingMode(works.get(0));
				activityDCs.add(dc);
			}

			a.setDataCenters(activityDCs);
			activities.add(a);
		}
		appl.setActivities(activities);

		return appl;
	}

	public static WorkingMode createWorkingMode(String name, int value, String sysCmd, int bizPerf, int power) {
		WorkingMode work = new WorkingMode(name, value);
		WorkingModeActuator actuator = new WorkingModeActuator();
		actuator.setSystemCommand(sysCmd);
		work.setActuator(actuator);
		List<PerformanceLevel> performanceLevels = new ArrayList<>();
		PerformanceLevel perfL = new PerformanceLevel();
		perfL.setBusinessPerformance(Amount.valueOf(bizPerf, ONE.alternate("Page")));
		perfL.setPower(Amount.valueOf(power, WATT));
		performanceLevels.add(perfL);
		work.setPerformanceLevels(performanceLevels);
		
		List<Resource> resources = new ArrayList<>();
		CFApplication CFApp = new CFApplication(Amount.valueOf(100, Units.MB), Amount.valueOf(100, Units.MB), 2, "test container", Amount.valueOf(10, WATT));
		resources.add(CFApp);
		work.setResources(resources);
		
		return work;
	}

	public static WorkingMode createWorkingModeCFAppInstancesEqValue(String name, int value, String sysCmd, int bizPerf, int power) {
		WorkingMode work = new WorkingMode(name, value);
		WorkingModeActuator actuator = new WorkingModeActuator();
		actuator.setSystemCommand(sysCmd);
		work.setActuator(actuator);
		List<PerformanceLevel> performanceLevels = new ArrayList<>();
		PerformanceLevel perfL = new PerformanceLevel();
		perfL.setBusinessPerformance(Amount.valueOf(bizPerf, ONE.alternate("Page")));
		perfL.setPower(Amount.valueOf(power, WATT));
		performanceLevels.add(perfL);
		work.setPerformanceLevels(performanceLevels);
		
		List<Resource> resources = new ArrayList<>();
		Amount<DataAmount> disk = Amount.valueOf(100 * value, Units.GB);
		CFApplication CFApp = new CFApplication(disk, disk, value, "MockCFApp", Amount.valueOf(10 * value, WATT));
		resources.add(CFApp);
		work.setResources(resources);
		
		return work;
	}

	public static Application createNonFederatedApp() {
        Application appl = new Application();
        List<eu.dc4cities.easc.activity.Activity> activities = new ArrayList<>();

        appl.setName("NonFederatedApp");

        //a
        for(int i = 1; i <= 1; i++) {
			eu.dc4cities.easc.activity.Activity a = new eu.dc4cities.easc.activity.Activity("activity" + i);
			a.setBusinessUnit("Page");
			Amount<?> migrationPerformanceCost = Amount.valueOf(3, ONE.alternate("Page"));
			a.setMigrationPerformanceCost(migrationPerformanceCost);
			a.setRelocability(Relocability.NO.value());
			eu.dc4cities.easc.sla.ServiceLevelObjective serviceLevelObjective = new eu.dc4cities.easc.sla.ServiceLevelObjective();
			//serviceLevelObjective.setBasePrice(priceAmount.valueOf(100, ONE.alternate("EUR")));
			serviceLevelObjective.setCumulativeBusinessObjective(Amount.valueOf(800, ONE.alternate("Page")));
			List<eu.dc4cities.easc.sla.ServiceLevelObjective> serviceLevelObjectives = new ArrayList<>();
			serviceLevelObjectives.add(serviceLevelObjective);
			//			serviceLevelObjective.setPriceModifiers(priceModifiers);
			a.setServiceLevelObjectives(serviceLevelObjectives);
			List<DataCenterWorkingModes> activityDCs = new ArrayList<>();
			//dc
			for(int dcId = 1; dcId <= 1; dcId++) {
				DataCenterWorkingModes dc = new DataCenterWorkingModes();
				dc.setDataCenterName("dc" + dcId);
				List<WorkingMode> works = new ArrayList<>();
				works.add(createWorkingMode("wm0", 0, "echo wm0", 0, 0));
				works.add(createWorkingMode("wm1", 1, "echo wm1", 10, 10));
				works.add(createWorkingMode("wm2", 2, "echo wm2", 20, 20));

				dc.setWorkingModes(works);
				dc.setDefaultWorkingMode("wm0");
//				dc.setCurrentWorkingMode(works.get(0));
				activityDCs.add(dc);
			}

			a.setDataCenters(activityDCs);
			activities.add(a);
		}
		appl.setActivities(activities);

		return appl;
	}
	
	public static Application createEascTwoActivities() {
        Application appl = new Application();
        List<eu.dc4cities.easc.activity.Activity> activities = new ArrayList<>();

        appl.setName("EASC-TwoActivities");

        //a
        for(int i = 1; i <= 2; i++) {
        	String activityName = "activity" + i;
			eu.dc4cities.easc.activity.Activity a = new eu.dc4cities.easc.activity.Activity(activityName);
			a.setBusinessUnit("Page");
			Amount<?> migrationPerformanceCost = Amount.valueOf(0, ONE.alternate("Page"));
			a.setMigrationPerformanceCost(migrationPerformanceCost);
			a.setRelocability(Relocability.NO.value());
			eu.dc4cities.easc.sla.ServiceLevelObjective serviceLevelObjective = new eu.dc4cities.easc.sla.ServiceLevelObjective();
			//serviceLevelObjective.setBasePrice(priceAmount.valueOf(100, ONE.alternate("EUR")));
			serviceLevelObjective.setCumulativeBusinessObjective(Amount.valueOf(800, ONE.alternate("Page")));
			List<eu.dc4cities.easc.sla.ServiceLevelObjective> serviceLevelObjectives = new ArrayList<>();
			serviceLevelObjectives.add(serviceLevelObjective);
			//serviceLevelObjective.setPriceModifiers(priceModifiers);
			a.setServiceLevelObjectives(serviceLevelObjectives);
			List<DataCenterWorkingModes> activityDCs = new ArrayList<>();
			//dc
			DataCenterWorkingModes dc = new DataCenterWorkingModes();
			dc.setDataCenterName("DC");
			List<WorkingMode> works = new ArrayList<>();
			works.add(createWorkingModeCFAppInstancesEqValue(activityName + ".wm0", 0, "echo wm0", 0, 0));
			works.add(createWorkingModeCFAppInstancesEqValue(activityName + ".wm1", 1, "echo wm1", 10, 10));
			works.add(createWorkingModeCFAppInstancesEqValue(activityName + ".wm2", 2, "echo wm2", 20, 20));

			dc.setWorkingModes(works);
			dc.setDefaultWorkingMode(activityName + ".wm0");
			activityDCs.add(dc);
			a.setDataCenters(activityDCs);
			activities.add(a);
		}
		appl.setActivities(activities);

		return appl;
	}
	
	public static EascActivityPlan createEascActivityPlanFromApp(Application app, int []awm) {
		EascActivityPlan eap = new EascActivityPlan(app.getName());
		List<Activity> activities = new ArrayList<Activity>();
		DateTime dateFrom = DateTime.now();

		eap.setDateFrom(dateFrom);
		eap.setDateTo(dateFrom.plusMinutes(15));
		eap.setTimeSlotDuration(Amount.valueOf(15, SI.SECOND.times(60)));

		int anum = 0;
		for(eu.dc4cities.easc.activity.Activity easca: app.getActivities()) {
			Activity a = new Activity(easca.getName());
			List<ActivityDataCenter> activityDCs = new ArrayList<ActivityDataCenter>();

			for(DataCenterWorkingModes dc: easca.getDataCenters()) {
				ActivityDataCenter ctrlDC = new ActivityDataCenter(dc.getDataCenterName());
				WorkingMode wm = dc.getWorkingModes().get(awm[anum]);
				List<Work> works = new ArrayList<Work>();
				Work work = new Work(0, 1, wm.getName(), wm.getValue(), wm.getPerformanceLevels().get(0).getPower());
				works.add(work);
				ctrlDC.setWorks(works);
				activityDCs.add(ctrlDC);
			}

			a.setDataCenters(activityDCs);
			activities.add(a);
			anum++;
		}

		eap.setActivities(activities);
		return eap;
	}
	
	public static EascActivityPlan createEascActivityPlanNonFederatedApp() {
		EascActivityPlan eap = new EascActivityPlan("NonFederatedApp");
		List<Activity> activities = new ArrayList<Activity>();
		DateTime dateFrom = DateTime.now();

		eap.setDateFrom(dateFrom);
		eap.setDateTo(dateFrom.plusMinutes(15));
		eap.setTimeSlotDuration(Amount.valueOf(15, SI.SECOND.times(60)));

		for(int i = 1; i <= 1; i++) {
			Activity a = new Activity("activity" + i);
			List<ActivityDataCenter> activityDCs = new ArrayList<ActivityDataCenter>();

			for(int dcId = 1; dcId <= 1; dcId++) {
				ActivityDataCenter ctrlDC = new ActivityDataCenter("dc" + dcId);
				List<Work> works = new ArrayList<Work>();
				Work work = new Work(0, 1, "wm1", 1, Amount.valueOf(10, SI.WATT));
				works.add(work);
				work = new Work(1, 5, "wm2", 2, Amount.valueOf(20, SI.WATT));
				works.add(work);
				ctrlDC.setWorks(works);
				activityDCs.add(ctrlDC);
			}

			a.setDataCenters(activityDCs);
			activities.add(a);
		}

		eap.setActivities(activities);
		return eap;
	}

	public static Configuration createConfiguration(String eascName) {
		Configuration conf = new Configuration();
		conf.setEASCName(eascName);
		return conf;
	}
	
}