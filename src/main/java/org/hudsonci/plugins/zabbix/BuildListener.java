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

import hudson.model.Result;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.model.listeners.RunListener;
import hudson.tasks.test.AbstractTestResultAction;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Named;
import org.hudsonci.plugins.zabbix.sender.Sender;

/**
 *
 * @author henrik
 */
@Named
public class BuildListener extends RunListener<Run> {

    @Inject
    Plugin plugin;

    @Override
    public void onCompleted(Run r, TaskListener listener) {
        String zabbixServer = plugin.getServer();
        String zabbixHostName = plugin.getHostname();
        
        if (zabbixServer == null || zabbixServer.trim().length()==0 || zabbixHostName == null || zabbixHostName.trim().length()==0) {
            listener.getLogger().println("Zabbix plugin: Zabbix not configured skipping");
            return;
        }
        
        String jobName = r.getParent().getName();

        Map<String, String> metrics = new HashMap<String, String>(10);

        metrics.put(getKey(jobName, "number"), String.valueOf(r.getNumber()));
        metrics.put(getKey(jobName, "duration"), String.valueOf(r.getDuration()/1000));
        metrics.put(getKey(jobName, "result"), String.valueOf(r.getResult().ordinal));

        AbstractTestResultAction<?> tests = r.getAction(AbstractTestResultAction.class);
        if (tests != null) {
            int totalTest = tests.getTotalCount();
            int failedTests = tests.getFailCount();
            int skippedTest = tests.getSkipCount();
            int successTest = totalTest - (failedTests + skippedTest);
            
            metrics.put(getKey(jobName, "tests.total"), String.valueOf(totalTest));
            metrics.put(getKey(jobName, "tests.skip"), String.valueOf(skippedTest));
            metrics.put(getKey(jobName, "tests.fail"), String.valueOf(failedTests));
            metrics.put(getKey(jobName, "tests.success"), String.valueOf(successTest));            
        }        
        metrics.put(getKey(jobName, "timetofix.builds"), getTimeToFixInBuilds(r));
        metrics.put(getKey(jobName, "timetofix.time"), getTimeToFixInMs(r));
        Sender.sendMetric(listener, plugin.getServer(), plugin.getHostname(), metrics);
    }

    private String getKey(String jobName, String metric) {
        return "hudson.job." + metric + "[\\\"" + jobName + "\\\"]";
    }

    private String getTimeToFixInBuilds(Run r) {

        int result = 0;
        // If the previous build was successfull it means that if this build is a failure 
        // the counter starts now.
        if (r.getPreviousCompletedBuild().getResult().isWorseThan(Result.SUCCESS)) {
            // find the first failing build after the last success
            Run firstFail = r.getPreviousSuccessfulBuild().getNextBuild();
            result = r.number - firstFail.number;
        }
        return String.valueOf(result);
    }

    private String getTimeToFixInMs(Run r) {
        long result = 0;
        if (r.getPreviousCompletedBuild().getResult().isWorseThan(Result.SUCCESS)) {
            // find the first failing build after the last success
            Run firstFail = r.getPreviousSuccessfulBuild().getNextBuild();
            long firstFailEndTime = firstFail.getTimeInMillis() +firstFail.getDuration();
            // we use the hudson calculations instead of currentTime in miillies to stay consistent
            long currentEndTime = r.getTimeInMillis() + r.getDuration();
            result = (currentEndTime - firstFailEndTime)/1000;
        }
        return String.valueOf(result);
    }
}
