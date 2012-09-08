/*
 * Copyright (c) 2012 Henrik Lynggaard Hansen
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Henrik Lynggaard Hansen- initial API and implementation and/or initial documentation
 */
package org.hudsonci.plugins.zabbix;

import hudson.model.Descriptor.FormException;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.servlet.ServletException;
import net.sf.json.JSONObject;
import org.hudsonci.plugins.zabbix.mbeans.Discovery;
import org.kohsuke.stapler.StaplerRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 *
 * @author henrik
 */
@Named
@Singleton
public class Plugin extends hudson.Plugin {

    private String server;
    private String hostname;

    private static final Logger log = LoggerFactory.getLogger(Plugin.class);    

    
    
    
    @Override
    public void configure(StaplerRequest req, JSONObject formData) throws IOException, ServletException, FormException {
        server = formData.getString("server");
        hostname = formData.getString("hostname");
        save();
    }

    @Override
    public void start() throws Exception {
        load();
        getServer();
        getHostname();
    }
    

    @Override
    public void postInitialize() throws Exception {
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        ObjectName name = new ObjectName("org.hudsonci.plugin.zabbix:type=Discovery");
        Discovery mbean = new Discovery();
        mbs.registerMBean(mbean, name);

    }

    public String getServer() {
        return server;
    }

    public String getHostname() {
        return hostname;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }
    
    
}
