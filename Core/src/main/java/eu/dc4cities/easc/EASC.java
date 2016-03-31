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

package eu.dc4cities.easc;

import eu.dc4cities.controlsystem.model.unit.Units;
import eu.dc4cities.easc.activity.Activity;
import eu.dc4cities.easc.activity.DataCenterWorkingModes;
import eu.dc4cities.easc.activityplan.ActivityPlanExecutor;
import eu.dc4cities.easc.activityplan.LazyActivityPlanExecutor;
import eu.dc4cities.easc.com.CtrlCom;
import eu.dc4cities.easc.configuration.ConfigReader;
import eu.dc4cities.easc.configuration.Configuration;
import eu.dc4cities.easc.energyservice.DefaultEnergyService;
import eu.dc4cities.easc.energyservice.EnergyService;
import eu.dc4cities.easc.monitoring.Monitor;
import eu.dc4cities.easc.workingmode.DefaultWorkingModeManager;
import eu.dc4cities.easc.workingmode.WorkingModeManager;
import org.jscience.physics.amount.Amount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.measure.quantity.Energy;
import javax.measure.unit.SI;
import javax.measure.unit.UnitFormat;
import java.util.LinkedList;
import java.util.List;

import static javax.measure.unit.SI.MEGA;
import static javax.measure.unit.Unit.ONE;

/**
 * Main EASC class. This should be instanciated by each EASC, providing the specific config parameters.
 */
public class EASC {

    private Logger logger = LoggerFactory.getLogger(EASC.class);
    private EnergyService es;
    private WorkingModeManager wmm;
    private CtrlCom ctrlCom;
    private ActivityPlanExecutor ape;
    private Configuration config;
    private Application appConfig;
    private Monitor monitor;

    //for Unit testing
    public EASC(Application app, EnergyService es, Configuration conf) {
        this.setWorkingModeManager(new DefaultWorkingModeManager(app));
        this.setEnergyService(es);
        this.setConfig(conf);
        this.appConfig = app;
    }

    public EASC(String configDirectory, Monitor monitor, EnergyService energyService) {
        Units.init();
        UnitFormat.getInstance().label(ONE.alternate("Page"), "Page");
        UnitFormat.getInstance().label(ONE.alternate("Req"), "Req");
        UnitFormat.getInstance().label(ONE.alternate("Exam"), "Exam");


        Amount<Energy> e = Amount.valueOf(10, SI.JOULE);
        e.doubleValue(MEGA(SI.JOULE));
        //read general configuration
        //this.config = ConfigReader.readConfiguration(Constants.CONFIG_FOLDER).get();
        this.config = ConfigReader.readConfiguration(configDirectory).get();
        this.appConfig = ConfigReader.readAppConfig(configDirectory).get();
        //read server config

        //register the energy service
        this.setEnergyService(energyService);

        this.monitor = monitor;
        this.monitor.setEnergyService(energyService);
       
    }

    public EASC(String configDirectory, Monitor monitor) {

        this(configDirectory, monitor, (EnergyService) new DefaultEnergyService());
    }

    public void init(boolean defaultWMM) {
        if (defaultWMM) {
            this.setWorkingModeManager(new DefaultWorkingModeManager(appConfig));
        }
        getEnergyService().addEasc(this);

        //Initialize workDone data structure
        for (Activity a : this.appConfig.getActivities()) {
            for (DataCenterWorkingModes dc : a.getDataCenters()) {
                monitor.initWorkDone(a.getName() + "." + dc.getDataCenterName());
                dc.initCurrentWorkingMode();
            }
        }
        ape = new LazyActivityPlanExecutor(wmm, appConfig);
        ctrlCom = new CtrlCom(ape, config, appConfig, wmm, monitor, getEnergyService());
    }

    public void start() {
        ctrlCom.start();
    }

    public CtrlCom getCtrlCom() {
        return ctrlCom;
    }

    public void setCtrlCom(CtrlCom ctrlCom) {
        this.ctrlCom = ctrlCom;
    }

    public ActivityPlanExecutor getActivityPlanExecutor() {
        return ape;
    }

    public void setActivityPlanExecutor(ActivityPlanExecutor ape) {
        this.ape = ape;
    }

    public Configuration getConfig() {
        return config;
    }

    public void setConfig(Configuration config) {
        this.config = config;
    }

    public Application getAppConfig() {
        return appConfig;
    }

    public void setAppConfig(Application appConfig) {
        this.appConfig = appConfig;
    }

    public WorkingModeManager getWorkingModeManager() {
        return wmm;
    }

    /**
     * Gives a list of all datacenter, discover that exploring the list per activity.
     *
     * @return combined list of all datacenters for this EASC.
     */
    public List<DataCenterWorkingModes> getDataCenterList() {
        List<DataCenterWorkingModes> combinedList = new LinkedList<>();
        for (Activity act : appConfig.getActivities()) {
            combinedList.addAll(act.getDataCenters());
        }
        return combinedList;
    }

    public void setWorkingModeManager(WorkingModeManager wmm) {
        this.wmm = wmm;
    }

    public void setDefaultWorkingModeManager() {
        this.wmm = new DefaultWorkingModeManager(appConfig);
    }

    public Monitor getMonitor() {
        return monitor;
    }

    public void setMonitor(Monitor monitor) {
        this.monitor = monitor;
    }

    public EnergyService getEnergyService() {
        return es;
    }

    public void setEnergyService(EnergyService es) {
        this.es = es;
    }

}
