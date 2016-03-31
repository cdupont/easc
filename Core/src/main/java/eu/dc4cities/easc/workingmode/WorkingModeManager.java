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

import java.util.List;

/**
 * Interface for controlling the current WM.
 */
public interface WorkingModeManager {

    boolean applyWorkingMode(String activityName, String dcName, String wmn);

    List<WorkingMode> getWorkingModes(String activityName, String dcName);

    /**
     * Get the current working mode.
     *
     * @return the working mode identifier, {@code null} if no working mode is currently running
     */
    WorkingMode getCurrentWorkingMode(String activityName, String dcName);

    WorkingMode getDefaultWorkingMode(String activityName, String dcName);
    
	void applyDefaultWorkingMode(String activityName, String dcName);

    //Returns the current working mode for all activities and all datacentres
    List<WorkingMode> getAllCurrentWorkingMode();
}