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

import eu.dc4cities.easc.activity.Activity;
import eu.dc4cities.easc.configuration.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to store data of applications. Methods get and set
 * are provided so that this object is useful on the application execution
 * parameters are automatically loaded through their names on the AppConfig.yaml
 * file.
 *
 * @see eu.dc4cities.easc.configuration.ConfigReader
 *
 */
public class Application {

	//The name of this application
    String name = Constants.DEFAULT_APP_NAME;
    
    //The port the EASC will be listening on
    Integer appPort = Constants.DEFAULT_APP_PORT;
    
    //The activities managed by this application
    List<Activity> activities = null;

    public Application() {
        this.activities = new ArrayList<Activity>();
    }

    public Application(String name, List<Activity> activities) {
        this.name = name;
        this.activities = activities;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Activity> getActivities() {
        return activities;
    }

    public void setActivities(List<Activity> activities) {
        this.activities = activities;
    }

    public Integer getAppPort() {
        return appPort;
    }

    public void setAppPort(Integer appPort) {
        this.appPort = appPort;
    }

}
