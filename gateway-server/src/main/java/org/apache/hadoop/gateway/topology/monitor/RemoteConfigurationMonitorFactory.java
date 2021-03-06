/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.hadoop.gateway.topology.monitor;

import org.apache.hadoop.gateway.GatewayMessages;
import org.apache.hadoop.gateway.GatewayServer;
import org.apache.hadoop.gateway.config.GatewayConfig;
import org.apache.hadoop.gateway.i18n.messages.MessagesFactory;
import org.apache.hadoop.gateway.services.GatewayServices;
import org.apache.hadoop.gateway.services.config.client.RemoteConfigurationRegistryClientService;

import java.util.ServiceLoader;

public class RemoteConfigurationMonitorFactory {

    private static final GatewayMessages log = MessagesFactory.get(GatewayMessages.class);

    private static RemoteConfigurationRegistryClientService remoteConfigRegistryClientService = null;

    public static void setClientService(RemoteConfigurationRegistryClientService clientService) {
        remoteConfigRegistryClientService = clientService;
    }

    private static RemoteConfigurationRegistryClientService getClientService() {
        if (remoteConfigRegistryClientService == null) {
            GatewayServices services = GatewayServer.getGatewayServices();
            if (services != null) {
                remoteConfigRegistryClientService = services.getService(GatewayServices.REMOTE_REGISTRY_CLIENT_SERVICE);
            }
        }

        return remoteConfigRegistryClientService;
    }

    /**
     *
     * @param config The GatewayConfig
     *
     * @return The first RemoteConfigurationMonitor extension that is found.
     */
    public static RemoteConfigurationMonitor get(GatewayConfig config) {
        RemoteConfigurationMonitor rcm = null;

        ServiceLoader<RemoteConfigurationMonitorProvider> providers =
                                                 ServiceLoader.load(RemoteConfigurationMonitorProvider.class);
        for (RemoteConfigurationMonitorProvider provider : providers) {
            try {
                rcm = provider.newInstance(config, getClientService());
                if (rcm != null) {
                    break;
                }
            } catch (Exception e) {
                log.remoteConfigurationMonitorInitFailure(e.getLocalizedMessage(), e);
            }
        }

        return rcm;
    }

}
