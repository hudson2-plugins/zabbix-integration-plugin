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
package org.hudsonci.plugins.zabbix.sender;

import hudson.model.TaskListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Map;

public class Sender {
        
    public static void sendMetric(TaskListener listener,String server, String hostname, Map<String, String> values) {
        try {
            String jsonData = convertToJson(hostname,values);
            PrintStream logger = listener.getLogger();
            logger.println("Zabbix plugin: Sending to server at '"+ server + "' : " + jsonData);
            Socket socket = new Socket(server, 10051);
            OutputStream os = socket.getOutputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));           
            writeMessage(os, jsonData.getBytes());
            String line;
            logger.println("Zabbix Plugin: Response");
            while ((line = in.readLine()) != null) {
                logger.println(line); 
            }
            in.close();
            os.close();
            socket.close();
        } catch (Exception ex) {
            listener.getLogger().println("Failed to send to zabbix: " +ex);
        }


    }
   
    private static String convertToJson(String hostname, Map<String,String> values) {
        StringBuilder sb = new StringBuilder(1024);
        sb.append("{\n");
        
        valuePair(sb, "request", "sender data");
        sb.append(", \n");
        quote(sb, "data").append(": [\n");
      
        boolean first = true;
        for (Map.Entry<String, String> entry : values.entrySet()) {
            if (!first) {
                sb.append(", \n");
            }
            sb.append("{");
            valuePair(sb, "host", hostname);            
            sb.append(',');
            valuePair(sb, "key", entry.getKey());
            sb.append(',');
            valuePair(sb, "value", entry.getValue());
            sb.append("}");
            first = false;
        }
        sb.append("]\n");
        sb.append("}");
        return sb.toString();
    }
    
    private static void valuePair(StringBuilder sb, String key, String value) {
        sb.append('"').append(key).append("\": \"").append(value).append("\"");        
    }
    private static StringBuilder quote(StringBuilder sb,String text) {
        return sb.append('"').append(text).append('"');
    }

    protected static void writeMessage(OutputStream out, byte[] data) throws IOException {
        int length = data.length;

        out.write(new byte[]{
                    'Z', 'B', 'X', 'D',
                    '\1',
                    (byte) (length & 0xFF),
                    (byte) ((length & 0x00FF) >> 8),
                    (byte) ((length & 0x0000FF) >> 16),
                    (byte) ((length & 0x000000FF) >> 24),
                    '\0', '\0', '\0', '\0'});
        out.write(data);
    }        
}
