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

package eu.dc4cities.easc.cloudcontrollers;

import com.google.common.base.Optional;
import eu.dc4cities.easc.resource.Server;

import java.util.List;

/**
 * Interface for getting infos from the PaaS
 */
public interface PaaSController {

    enum AppState {
        UPDATING,
        STARTED,
        STOPPED;
    }
    boolean login();

    boolean startApp(String appName);

    boolean stopApp(String appName);

    AppState getAppState(String appName);

    int getNumRunningInstancesApp(String appName);

    List<String> getAppContainersId(String appName);

    boolean scaleApp(String appName, int instances, int disk, int memory);

    double getContainerVCPULoad(String appName, String containerId);

    double getContainerVRAMLoad(String appName, String containerId);

    Optional<String> getContainerIP(String appName, String containerId);

    Optional<Server> getContainerServer(String appName, String containerId);

    List<Server> getServers();

    String getAppEnv(String appName);

    String getAppResources(String appName);

}
