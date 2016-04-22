/*
 * TeleStax, Open Source Cloud Communications
 * Copyright 2011-2016, Telestax Inc and individual contributors
 * by the @authors tag. 
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.mobicents.media.core.configuration;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Configuration parameters of the MGCP Controller.
 * 
 * @author Henrique Rosa (henrique.rosa@telestax.com)
 *
 */
public class MgcpControllerConfiguration {

    private String address;
    private int port;
    private int poolSize;
    private String configuration;
    private final Map<String, MgcpEndpointConfiguration> endpoints;

    public MgcpControllerConfiguration() {
        this.address = "127.0.0.1";
        this.port = 2427;
        this.poolSize = 25;
        this.configuration = "mgcp-conf.xml";
        this.endpoints = new HashMap<>(5);
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        if (address == null || address.isEmpty()) {
            throw new IllegalArgumentException("MGCP address cannot be empty");
        }
        this.address = address;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        if (port <= 0 || port > 65535) {
            throw new IllegalArgumentException("Illegal MGCP port value: 0 < " + port + " < 65536");
        }
        this.port = port;
    }

    public int getPoolSize() {
        return poolSize;
    }

    public void setPoolSize(int poolSize) {
        if (poolSize < 0) {
            throw new IllegalArgumentException("Pool size cannot be negative.");
        }
        this.poolSize = poolSize;
    }

    public String getConfiguration() {
        return configuration;
    }

    public void setConfiguration(String configuration) {
        if (configuration == null || configuration.isEmpty()) {
            throw new IllegalArgumentException("Configuration file path cannot be empty.");
        }
        this.configuration = configuration;
    }

    public Iterator<MgcpEndpointConfiguration> getEndpoints() {
        return this.endpoints.values().iterator();
    }

    public MgcpEndpointConfiguration addEndpoint(MgcpEndpointConfiguration endpoint) {
        return this.endpoints.put(endpoint.getName(), endpoint);
    }

    public MgcpEndpointConfiguration removeEndpoint(String type) {
        return this.endpoints.remove(type);
    }

    public void removeAllEndpoints() {
        this.endpoints.clear();
    }

}
