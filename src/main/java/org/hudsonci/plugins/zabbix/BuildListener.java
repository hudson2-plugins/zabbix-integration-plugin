/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hudsonci.plugins.zabbix;

import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.model.listeners.RunListener;
import hudson.tasks.test.AbstractTestResultAction;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Named;
import org.hudsonci.plugins.zabbix.sender.Sender;

/**
 *
 * @author henrik
 */
@Named
public class BuildListener extends RunListener<Run> {

    @Override
    public void onCompleted(Run r, TaskListener listener) {
        String jobName =r.getParent().getName();
        
        Map<String, String> metrics = new HashMap<String, String>(10);
        
        metrics.put(getKey(jobName,"number"), String.valueOf(r.getNumber()));
        metrics.put(getKey(jobName,"duration"), String.valueOf(r.getDuration()));
        metrics.put(getKey(jobName,"result"), String.valueOf(r.getResult().ordinal));
        
        AbstractTestResultAction<?> tests = r.getAction(AbstractTestResultAction.class);        
        
        int totalTest = 0;
        int skippedTest = 0;
        int failedTests = 0;
        int successTest = 0;
        if (tests != null) {
            totalTest = tests.getTotalCount();
            failedTests = tests.getFailCount();            
            skippedTest = tests.getSkipCount();
            successTest = totalTest - (failedTests + skippedTest);                        
        }        
        metrics.put(getKey(jobName,"tests.total"), String.valueOf(totalTest));
        metrics.put(getKey(jobName,"tests.skip"), String.valueOf(skippedTest));
        metrics.put(getKey(jobName,"tests.fail"), String.valueOf(failedTests));
        metrics.put(getKey(jobName,"tests.success"), String.valueOf(successTest));        
        Sender.sendMetric(listener,"vmhost", metrics);
    }
    
    
    private String getKey(String jobName, String metric) {
        return "hudson.job."+metric+"[\\\"" + jobName + "\\\"]";        
    }
        
}
