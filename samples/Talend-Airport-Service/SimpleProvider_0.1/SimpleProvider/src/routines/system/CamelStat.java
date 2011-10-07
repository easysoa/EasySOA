// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package routines.system;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import routines.system.GlobalResource;

public class CamelStat implements Runnable {

    private org.apache.camel.CamelContext theContext;

    private javax.management.MBeanServer mBeanServer;

    private java.util.Set<javax.management.ObjectName> set = null;

    private java.util.List<javax.management.ObjectName> routes;

    private java.util.Set<javax.management.ObjectName> processors;

    public CamelStat(org.apache.camel.CamelContext context) {
        this.theContext = context;
    }

    public void setParams() throws MalformedObjectNameException, NullPointerException {
        this.mBeanServer = theContext.getManagementStrategy().getManagementAgent().getMBeanServer();
        this.set = mBeanServer.queryNames(new javax.management.ObjectName(
                org.apache.camel.management.DefaultManagementAgent.DEFAULT_DOMAIN + ":type=routes,*"), null);
        // this.routes = new java.util.ArrayList<javax.management.ObjectName>();
        this.processors = mBeanServer.queryNames(new javax.management.ObjectName(
                org.apache.camel.management.DefaultManagementAgent.DEFAULT_DOMAIN + ":type=processors,*"), null);
    }

    // public void setRoutes(String routeName) throws
    // MalformedObjectNameException, NullPointerException {
    // this.routes.add(javax.management.ObjectName.getInstance(org.apache.camel.management.DefaultManagementAgent.DEFAULT_DOMAIN+" ,type=routes,name=\""+routeName+"\""));
    // }

    // public void setProcessors(String processorName) throws
    // MalformedObjectNameException, NullPointerException {
    // this.processors.add(mBeanServer.queryNames(new
    // javax.management.ObjectName(org.apache.camel.management.DefaultManagementAgent.DEFAULT_DOMAIN
    // + ":type=processors,name=\""+routeName+"\""), null));
    // }

    private boolean openSocket = true;

    private static boolean debug = false;

    public void openSocket(boolean openSocket) {
        this.openSocket = openSocket;
    }

    public static int BEGIN = 0;

    public static int RUNNING = 1;

    public static int END = 2;

    public static int CLEAR = 3;

    // it is a dummy default value for jobStat field
    public static int JOBDEFAULT = -1;

    public static int JOBSTART = 0;

    public static int JOBEND = 1;

    // this is as an additinal info to test the command type
    public static String TYPE0_JOB = "0";

    public static String TYPE1_CONNECTION = "1";

    private class StatBean {

        private String itemId;

        private String connectionId;

        private int nbLine;

        private int state;

        private long startTime = 0;

        private long endTime = 0;

        private String exec = null;

        // feature:11356---1="Start Job" and 2="End job", default is -1
        private int jobStat = JOBDEFAULT;

        public StatBean(int jobStat, String itemId) {
            this.jobStat = jobStat;
            this.itemId = itemId;
            if (jobStat == JOBSTART) {
                this.startTime = System.currentTimeMillis();
            } else if (jobStat == JOBEND) {
                this.endTime = System.currentTimeMillis();
            }
        }

        public StatBean(String connectionId) {
            this.connectionId = connectionId;
            this.startTime = System.currentTimeMillis();
        }

        public String getConnectionId() {
            return this.connectionId;
        }

        public void setConnectionId(String connectionId) {
            this.connectionId = connectionId;
        }

        public int getNbLine() {
            return this.nbLine;
        }

        public void setNbLine(int nbLine) {
            this.nbLine = nbLine;
        }

        public int getState() {
            return this.state;
        }

        public void setState(int state) {
            this.state = state;
        }

        public long getStartTime() {
            return startTime;
        }

        public void setStartTime(long startTime) {
            this.startTime = startTime;
        }

        public long getEndTime() {
            return endTime;
        }

        public void setEndTime(long endTime) {
            this.endTime = endTime;
        }

        public String getExec() {
            return this.exec;
        }

        public void setExec(String exec) {
            this.exec = exec;
        }

        public int getJobStat() {
            return jobStat;
        }

        public void setJobStat(int jobStat) {
            this.jobStat = jobStat;
        }

        public String getItemId() {
            return itemId;
        }

    }

    private Map<String, StatBean> processStats = new HashMap<String, StatBean>();

    private List<String> keysList = new LinkedList<String>();

    // private java.util.ArrayList<StatBean> processStats = new
    // java.util.ArrayList<StatBean>();

    private java.net.Socket s;

    private java.io.PrintWriter pred;

    private boolean jobIsFinished = false;

    private String str = ""; //$NON-NLS-1$

    public void startThreadStat(String clientHost, int portStats) throws IOException {
        if (!openSocket) {
            // if go here, it means it is a childJob, it should share the socket
            // opened in parentJob.
            Socket s = null;
            Object object = GlobalResource.resourceMap.get(portStats);

            if (object == null || !(object instanceof Socket)) {
                // Here throw an Exception directly, because the ServerSocket
                // only support one client to connect it.
                String lastCallerJobName = new Exception().getStackTrace()[1].getClassName();
                throw new RuntimeException(
                        "The socket for statistics function is unavailable in job "
                                + lastCallerJobName
                                + "."
                                + "\nUsually, please check the tRunJob, it should uncheck the option \"Use an independent process to run child job\".");
                // todo: if here open another new Socket in childJob, need to
                // close it in the API: stopThreadStat()
                // s = new Socket(clientHost, portStats);
            } else {
                s = (Socket) object;
            }

            OutputStream output = s.getOutputStream();
            if (debug) {
                output = System.out;
            }
            pred = new java.io.PrintWriter(new java.io.BufferedWriter(new java.io.OutputStreamWriter(output)), true);
            Thread t = new Thread(this);
            t.start();

            return;
        }

        System.out.println("[statistics] connecting to socket on port " + portStats); //$NON-NLS-1$
        s = new Socket(clientHost, portStats);
        GlobalResource.resourceMap.put(portStats, s);
        OutputStream output = s.getOutputStream();
        if (debug) {
            output = System.out;
        }
        pred = new java.io.PrintWriter(new java.io.BufferedWriter(new java.io.OutputStreamWriter(output)), true);
        System.out.println("[statistics] connected"); //$NON-NLS-1$
        Thread t = new Thread(this);
        t.start();
    }

    public void run() {
        if (!debug) {
            synchronized (this) {
                try {
                    while (!jobIsFinished) {
                        sendMessages();
                        wait(1000);
                    }
                } catch (InterruptedException e) {
                    System.out.println("[statistics] interrupted"); //$NON-NLS-1$
                }
            }
        }
    }

    public void stopThreadStat() {
        if (!openSocket) {
            return;
        }
        jobIsFinished = true;
        try {
            sendMessages();
            if (pred != null) {
                pred.close();
            }
            if (s != null && !s.isClosed()) {
                s.close();
            }
            System.out.println("[statistics] disconnected"); //$NON-NLS-1$
        } catch (java.io.IOException ie) {
        }
    }

    public void sendMessages() {
        // if (!openSocket) {
        // return;
        // }

        // SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss.SZ");
        // System.out.println("############ Sending packets " + sdf.format(new
        // Date()) + " ... #################");

        for (String curKey : keysList) {
            StatBean sb = processStats.get(curKey);
            // it is connection
            int jobStat = sb.getJobStat();
            if (jobStat == JOBDEFAULT) {
                str = TYPE1_CONNECTION + "|" + rootPid + "|" + fatherPid + "|" + pid + "|" + sb.getConnectionId();
                // str = sb.getConnectionId();
                if (sb.getState() == CamelStat.CLEAR) {
                    str += "|" + "clear"; //$NON-NLS-1$ //$NON-NLS-2$
                } else {

                    if (sb.getExec() == null) {
                        str += "|" + sb.getNbLine() + "|" + (sb.getEndTime() - sb.getStartTime()); //$NON-NLS-1$ //$NON-NLS-2$
                    } else {
                        str += "|" + sb.getExec(); //$NON-NLS-1$
                    }
                    if (sb.getState() != CamelStat.RUNNING) {
                        str += "|" + ((sb.getState() == CamelStat.BEGIN) ? "start" : "stop"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                    }
                }
            } else {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss.SSSZ");

                // it is job, for feature:11356
                String jobStatStr = "";
                String itemId = sb.getItemId();
                itemId = itemId == null ? "" : itemId;
                if (jobStat == JOBSTART) {
                    jobStatStr = routeName + "|" + "start route" + "|" + itemId + "|"
                            + simpleDateFormat.format(new Date(sb.getStartTime()));
                } else if (jobStat == JOBEND) {
                    jobStatStr = routeName + "|" + "end route" + "|" + itemId + "|"
                            + simpleDateFormat.format(new Date(sb.getEndTime()));
                }

                str = TYPE0_JOB + "|" + rootPid + "|" + fatherPid + "|" + pid + "|" + jobStatStr;
            }
            // System.out.println(str);
            pred.println(str); // envoi d'un message
        }
        keysList.clear();

        // System.out.println("*** data sent ***");
    }

    long lastStatsUpdate = 0;

    public synchronized void updateStatOnConnection(String connectionId, int mode, String nodeName)
            throws AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException,
            MalformedObjectNameException, NullPointerException {

        StatBean bean;
        String key = connectionId;
        javax.management.ObjectName routeMBean = null;

        java.util.Iterator<javax.management.ObjectName> iterator = this.processors.iterator();
        while (iterator.hasNext()) {
            routeMBean = iterator.next();
            if (("\"" + nodeName + "\"").equals(routeMBean.getKeyProperty("name"))) {
                // System.out.println(nodeName);
                break;
            }
        }

        // javax.management.ObjectName routeMBean = iterator.next();
        if (connectionId.contains(".")) {
            String firstKey = null;
            String connectionName = connectionId.split("\\.")[0];
            int nbKeys = 0;
            for (String myKey : keysList) {
                if (myKey.startsWith(connectionName + ".")) {
                    if (firstKey == null) {
                        firstKey = myKey;
                    }
                    nbKeys++;
                    if (nbKeys == 4) {
                        break;
                    }
                }
            }
            if (nbKeys == 4) {
                keysList.remove(firstKey);
            }
        }

        if (keysList.contains(key)) {
            int keyNb = keysList.indexOf(key);
            keysList.remove(key);
            keysList.add(keyNb, key);
        } else {
            keysList.add(key);
        }

        if (processStats.containsKey(key)) {
            bean = processStats.get(key);
        } else {
            bean = new StatBean(connectionId);
        }
        bean.setState(mode);
        bean.setEndTime(System.currentTimeMillis());
        bean.setNbLine(Integer.parseInt(mBeanServer.getAttribute(routeMBean, "ExchangesTotal").toString()));
        processStats.put(key, bean);

        // if tFileList-->tFileInputDelimited-->tFileOuputDelimited, it should
        // clear the data every iterate
        if (mode == BEGIN) {
            bean.setNbLine(0);
            // Set a maximum interval for each update of 250ms.
            // since Iterate can be fast, we try to update the UI often.
            long newStatsUpdate = System.currentTimeMillis();
            if (lastStatsUpdate == 0 || lastStatsUpdate + 250 < newStatsUpdate) {
                sendMessages();
                lastStatsUpdate = newStatsUpdate;
            }
        }

        if (debug) {
            sendMessages();
        }
    }

    // public synchronized void updateStatOnConnection(String connectionId, int
    // mode, String exec) {
    // StatBean bean;
    // String key = connectionId + "|" + mode;
    //
    // if (connectionId.startsWith("iterate")) {
    // key = connectionId + "|" + mode + "|" + exec;
    // } else {
    // if (connectionId.contains(".")) {
    // String firstKey = null;
    // String connectionName = connectionId.split(".")[0];
    // int nbKeys = 0;
    // for (String myKey : keysList) {
    // if (myKey.startsWith(connectionName + ".")) {
    // if (firstKey == null) {
    // firstKey = myKey;
    // }
    // nbKeys++;
    // if (nbKeys == 4) {
    // break;
    // }
    // }
    // }
    // if (nbKeys == 4) {
    // keysList.remove(firstKey);
    // }
    // }
    // }
    // if (keysList.contains(key)) {
    // keysList.remove(key);
    // }
    // keysList.add(key);
    // // System.out.println(connectionId);
    // if (processStats.containsKey(key)) {
    // bean = processStats.get(key);
    // } else {
    // bean = new StatBean(connectionId);
    // }
    // bean.setState(mode);
    // bean.setExec(exec);
    // processStats.put(key, bean);
    //
    // // Set a maximum interval for each update of 250ms.
    // // since Iterate can be fast, we try to update the UI often.
    // long newStatsUpdate = System.currentTimeMillis();
    // if (lastStatsUpdate == 0 || lastStatsUpdate + 250 < newStatsUpdate) {
    // sendMessages();
    // lastStatsUpdate = newStatsUpdate;
    // }
    // }

    public synchronized void updateStatOnJob(int jobStat, String parentNodeName) {
        StatBean bean = new StatBean(jobStat, parentNodeName);
        String key = jobStat + "";
        if (keysList.contains(key)) {
            keysList.remove(key);
        }
        keysList.add(key);
        processStats.put(key, bean);

        sendMessages();
    }

    // for feature:10589
    private String rootPid = null;

    private String fatherPid = null;

    private String pid = "0";

    private String routeName = null;

    // Notice: this API should be invoked after startThreadStat() closely.
    public void setAllPID(String rootPid, String fatherPid, String pid, String routeName) {
        this.rootPid = rootPid;
        this.fatherPid = fatherPid;
        this.pid = pid;
        this.routeName = routeName;
    }
}
