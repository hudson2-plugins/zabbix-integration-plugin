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

import java.lang.management.ManagementFactory;
import javax.inject.Named;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import org.hudsonci.plugins.zabbix.mbeans.Discovery;


/**
 *
 * @author henrik
 */
@Named
public class Plugin extends hudson.Plugin {
  
    
    @Override
    public void postInitialize() throws Exception {
        super.postInitialize();
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        ObjectName name = new ObjectName("org.hudsonci.plugin.zabbix:type=Discovery");
        Discovery mbean = new Discovery();
        mbs.registerMBean(mbean, name);
        
    }
}
