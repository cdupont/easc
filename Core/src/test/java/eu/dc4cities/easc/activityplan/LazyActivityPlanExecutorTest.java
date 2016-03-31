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
import eu.dc4cities.controlsystem.model.unit.Units;
import eu.dc4cities.easc.Application;
import eu.dc4cities.easc.configuration.TestObjectsGenerator;
import eu.dc4cities.easc.workingmode.DefaultWorkingModeManager;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;

import javax.measure.unit.UnitFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static javax.measure.unit.Unit.ONE;

/**
 * Unit tests for {@link LazyActivityPlanExecutor}.
 */
public class LazyActivityPlanExecutorTest extends TestCase {

    Application app;

    @Override
	protected void setUp() {
    	Units.init();
    	UnitFormat.getInstance().label(ONE.alternate("Page"), "Page");
    }
    
	@Override
	protected void tearDown() {
    }

    /**
     * Just a switch to operate.
     */
    @Test
    public void testDefault() {
        List<String> order = new ArrayList<>();
		this.app = TestObjectsGenerator.createAppForLazyExecutorTest(order);
		EascActivityPlan a = TestObjectsGenerator.newActivityPlan(TestObjectsGenerator.W("b", 1, 0, 10));
        ActivityPlanExecutor ex = new LazyActivityPlanExecutor(new DefaultWorkingModeManager(app), app);
        ex.executeActivityPlan(a, true);
        Assert.assertEquals(order, Arrays.asList("b"));
    }

    /**
     * Here, b is the next one but it does not start at 1 so
     * it is expected to go back to the default working mode
     */
    @Test
    public void testBackToDefault() {
        List<String> order = new ArrayList<>();
		this.app = TestObjectsGenerator.createAppForLazyExecutorTest(order);
		EascActivityPlan a = TestObjectsGenerator.newActivityPlan(TestObjectsGenerator.W("b", 1, 3, 10));
        ActivityPlanExecutor ex = new LazyActivityPlanExecutor(new DefaultWorkingModeManager(app), app);
        ex.executeActivityPlan(a, true);
        Assert.assertEquals(order, Arrays.asList("a"));
    }

    /**
     * Start an activity with wm b running
     * Later, say now it is "b". Should not observe a switch
     */
    @Test
    public void testNewWithoutChange() {
    	List<String> order = new ArrayList<>();
		this.app = TestObjectsGenerator.createAppForLazyExecutorTest(order);
		EascActivityPlan a = TestObjectsGenerator.newActivityPlan(TestObjectsGenerator.W("b", 1, 0, 5));
        ActivityPlanExecutor ex = new LazyActivityPlanExecutor(new DefaultWorkingModeManager(app), app);    	
        ex.executeActivityPlan(a, true);
        
        a = TestObjectsGenerator.newActivityPlan(TestObjectsGenerator.W("b", 1, 0, 5));
    	ex.executeActivityPlan(a, true);
        Assert.assertEquals(order, Arrays.asList("b", "b"));
    }

    /**
     * Start an activity with wm b running
     * Later, say now it is "c". Should observe a switch
     */
    @Test
    public void testNewWithChange() {
        List<String> order = new ArrayList<>();
		this.app = TestObjectsGenerator.createAppForLazyExecutorTest(order);
		EascActivityPlan a = TestObjectsGenerator.newActivityPlan(TestObjectsGenerator.W("b", 1, 0, 5));
        ActivityPlanExecutor ex = new LazyActivityPlanExecutor(new DefaultWorkingModeManager(app), app);
        ex.executeActivityPlan(a, true);
        
        a = TestObjectsGenerator.newActivityPlan(TestObjectsGenerator.W("c", 2, 0, 5));
        ex.executeActivityPlan(a, true);
        Assert.assertEquals(order, Arrays.asList("b","c"));
    }

    /**
     * Start an activity with wm b running
     * Later, say it will be "c" (but not now). Should observe a switch to the default mode
     */
    @Test
    public void testNewWithHole() {
        List<String> order = new ArrayList<>();
		this.app = TestObjectsGenerator.createAppForLazyExecutorTest(order);
		EascActivityPlan a = TestObjectsGenerator.newActivityPlan(TestObjectsGenerator.W("b", 1, 0, 5));
        ActivityPlanExecutor ex = new LazyActivityPlanExecutor(new DefaultWorkingModeManager(app), app);
        ex.executeActivityPlan(a, true);

        a = TestObjectsGenerator.newActivityPlan(TestObjectsGenerator.W("c", 2, 2, 5));
        ex.executeActivityPlan(a, true);
        Assert.assertEquals(order, Arrays.asList("b","a"));
    }
}