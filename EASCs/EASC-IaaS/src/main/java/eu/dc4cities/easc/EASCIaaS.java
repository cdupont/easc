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

import eu.dc4cities.easc.monitoring.Monitor;

/**
 * EASC-IaaS uses inversion of control and has no knowledge of the application at all.
 * This easc receives from the control system a query for a given timeframe.
 * The EASC hence send back a detailed list of activities.
 * One activity per VM template running on openstack. This EASC also has an SLA
 * description, see SLA.yaml, that has a list of cost and thresholds payback based on
 * the available providers.
 *
 */
public class EASCIaaS extends EASC {

    /**
     * Simple constructor following the new interface.
     */
    public EASCIaaS(
            String pConfigDirectory,
            Monitor pMonitorFactory) {
        //Read AppConfig.yaml to get option plans available and working mode list for
        //each activity (i.e. each VM).
        super(pConfigDirectory, pMonitorFactory);

    }

    /**
     * EASC main loop, so this is the init.
     */
    public void init() {
        //Read VM list from Plug4Green.
        //TODO

        //Convert it to json so that CTRL can understand.
        //TODO

        //Send the json to central system.
        //TODO
    }
}
