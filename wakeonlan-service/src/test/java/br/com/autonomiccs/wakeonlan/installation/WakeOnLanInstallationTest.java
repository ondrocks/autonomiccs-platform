/*
 * This program is part of Autonomiccs "autonomic-platform",
 * an open source autonomic cloud computing management platform.
 * Copyright (C) 2016 Autonomiccs, Inc.
 *
 * Licensed to the Autonomiccs, Inc. under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The Autonomiccs, Inc. licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package br.com.autonomiccs.wakeonlan.installation;

import java.io.File;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;

import br.com.autonomiccs.autonomic.plugin.common.utils.ShellCommandUtils;

@RunWith(PowerMockRunner.class)
public class WakeOnLanInstallationTest {

    private WakeOnLanInstallation wakeOnLanInstallation;
    private String wakeOnLanProgramPath = "/usr/bin/wakeonlan";

    @Before
    public void beforeTest() {
        wakeOnLanInstallation = Mockito.spy(new WakeOnLanInstallation());
        wakeOnLanInstallation.shellCommandUtils = Mockito.mock(ShellCommandUtils.class);
        wakeOnLanInstallation.logger = Mockito.mock(Logger.class);
    }

    @Test
    @PrepareForTest(WakeOnLanInstallation.class)
    public void installWakeOnLanTestIfNotInstalled() throws Exception {
        prepareMockedFileThatRepresentsTheWakeOnLanCommand(false);

        String command = "aptitude -y install wakeonlan";
        final String returnOfInstallationCommand = "return of command";
        Mockito.when(wakeOnLanInstallation.shellCommandUtils.executeCommand(command)).thenReturn(returnOfInstallationCommand);

        wakeOnLanInstallation.installWakeOnLan();
        InOrder inOrder = Mockito.inOrder(wakeOnLanInstallation.shellCommandUtils, wakeOnLanInstallation.logger);
        inOrder.verify(wakeOnLanInstallation.logger).info("Checking if wakeonlan is installed.");
        inOrder.verify(wakeOnLanInstallation.logger).info("Wakeonlan is not installed.");
        inOrder.verify(wakeOnLanInstallation.logger).info("Installing wakeonlan.");
        inOrder.verify(wakeOnLanInstallation.shellCommandUtils).executeCommand(command);
        inOrder.verify(wakeOnLanInstallation.logger).info(returnOfInstallationCommand);
        inOrder.verify(wakeOnLanInstallation.logger).info("Installation finished.");

        Mockito.verify(wakeOnLanInstallation.logger, Mockito.times(0)).info("Wakeonlan is already installed.");
    }

    @Test
    @PrepareForTest(WakeOnLanInstallation.class)
    public void installWakeOnLanTestWakeOnLanAlreadyInstalled() throws Exception {
        prepareMockedFileThatRepresentsTheWakeOnLanCommand(true);

        wakeOnLanInstallation.installWakeOnLan();
        Mockito.verify(wakeOnLanInstallation.logger).info("Checking if wakeonlan is installed.");
        Mockito.verify(wakeOnLanInstallation.logger).info("Wakeonlan is already installed.");
        Mockito.verify(wakeOnLanInstallation.shellCommandUtils, Mockito.times(0)).executeCommand(Mockito.anyString());

    }

    private void prepareMockedFileThatRepresentsTheWakeOnLanCommand(boolean isIsInstalled) throws Exception {
        File mockedFile = Mockito.mock(File.class);
        Mockito.when(mockedFile.exists()).thenReturn(isIsInstalled);
        PowerMockito.whenNew(File.class).withArguments(wakeOnLanProgramPath).thenReturn(mockedFile);
    }

    @Test
    public void afterPropertiesSetTestinstallWakeOnLanBeingUsed() throws Exception {
        Mockito.doNothing().when(wakeOnLanInstallation).installWakeOnLan();
        wakeOnLanInstallation.afterPropertiesSet();

        Mockito.verify(wakeOnLanInstallation).installWakeOnLan();
    }
}