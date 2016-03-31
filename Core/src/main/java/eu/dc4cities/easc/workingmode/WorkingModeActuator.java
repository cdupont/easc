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

import eu.dc4cities.easc.activity.DataCenterWorkingModes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Shell command actuator.
 */
public class WorkingModeActuator {

    String systemCommand;
    Logger logger = LoggerFactory.getLogger(WorkingModeActuator.class);

    public WorkingModeActuator() {
    }

    public WorkingModeActuator(String systemCommand) {
        this.systemCommand = systemCommand;
    }

    public boolean activateWorkingMode(DataCenterWorkingModes dcwms, WorkingMode wm) {

        try {
            logger.debug("Switching working mode with command: " + systemCommand);
            Process proc = Runtime.getRuntime().exec(systemCommand);

            //starts a new thread waiting for the command result and finally change the WM in the EASC
            new Thread(new WaitProc(proc, dcwms, wm)).start();

            return true;

        } catch (IOException e) {
            logger.debug("Switch working mode command not found: " + systemCommand);
            return false;
        }

    }

    public String getSystemCommand() {
        return systemCommand;
    }

    public void setSystemCommand(String systemCommand) {
        this.systemCommand = systemCommand;
    }

    class WaitProc implements Runnable {

        DataCenterWorkingModes dcwms;
        WorkingMode wm;
        Process proc;

        public WaitProc(Process proc, DataCenterWorkingModes dcwms, WorkingMode wm) {

            this.proc = proc;
            this.dcwms = dcwms;
            this.wm = wm;
        }

        @Override
        public void run() {

            try {
                proc.waitFor();
                proc.getOutputStream().flush();

                if(proc.exitValue() == 0) {
                    dcwms.setCurrentWorkingMode(wm);
                    logger.debug(dcwms.getDataCenterName() + ": switched to working mode " + wm.getName());

                } else {
                    logger.debug(dcwms.getDataCenterName() + ": unable to switch to working mode " + wm.getName());
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
