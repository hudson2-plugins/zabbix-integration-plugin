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
package org.hudsonci.plugins.zabbix.mbeans;

import hudson.model.Hudson;
import hudson.model.Node;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Discovery implements DiscoveryMBean {

    @Override
    public String getJobs() {
        Collection<String> jobNames = Hudson.getInstance().getJobNames();
        return convertToJson("{#JOBNAME}", jobNames);
    }

    @Override
    public String getNodes() {
        List<String> result = new ArrayList<String>(Hudson.getInstance().getNodes().size()+1);
        result.add("Master");
        for (Node n : Hudson.getInstance().getNodes()) {
            result.add(n.getNodeName());
        }
        return convertToJson("{#SLAVENAME}", result);
    }

    private String convertToJson(String macro, Collection<String> values) {
        String quotedMacro = "\"" + macro + "\"";
        StringBuilder sb = new StringBuilder(1024);
        sb.append("{");
        sb.append("\"data\":[");
        boolean first = true;
        for (String entry : values) {
            if (!first) {
                sb.append(", ");
            }
            sb.append("{ ").append(quotedMacro).append(":\"").append(entry).append("\"}");
            first = false;
        }
        sb.append("]");
        sb.append("}");
        return sb.toString();
    }
}
