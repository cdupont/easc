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

import eu.dc4cities.controlsystem.model.easc.EascActivityPlan;

/**
 * Interface to specify an object handling the execution of an activity plan.
 */
public interface ActivityPlanExecutor {

    /**
     * Execute an activity plan.
     * If another plan is currently running, a transition must be performed
     * @param ap the plan to execute
     * @param wait {@code true} if the call must be blocking
     */
    boolean executeActivityPlan(EascActivityPlan ap, boolean wait);

	EascActivityPlan getActivityPlan();
}