zabbix-integration
==================

Provided integration with Zabbix (http://www.zabbix.com/). It consist of 3 parts

org.hudsonci.plugin.zabbix:type=Discovery
-----------------------------------------
This JMX MBean has two attributes which exposes the job names and slave names in the json format 
which is used by Zabbix' low level discovery mechanism. This allows Zabbix to create items, triggers etc. 
based on templates instead of users having to manually configuring them. The attributes are:
* Jobs
* Nodes

Zabbix Trapper events
---------------------
Not all of Hudson's metrics a are suited for polling as they are more event based. Therefor the zabbix 
integration also sends "trapper metrics" to zabbix when a build is completed. The followin metrics are sent 
on job completion:
* hudson.job.tests.total["{#JOBNAME}"]
* hudson.job.tests.success["{#JOBNAME}"]
* hudson.job.tests.fail["{#JOBNAME}"]
* hudson.job.tests.skip["{#JOBNAME}"]
* hudson.job.number["{#JOBNAME}"]
* hudson.job.result["{#JOBNAME}"]
* hudson.job.duration["{#JOBNAME}"]

Zabbix template
---------------
In order to make zabbix configuration easier there is a template sample included in src/main/zabbix

