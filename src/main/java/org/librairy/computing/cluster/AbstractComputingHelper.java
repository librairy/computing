/*
 * Copyright (c) 2016. Universidad Politecnica de Madrid
 *
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 *
 */

package org.librairy.computing.cluster;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.cassandra.CassandraSQLContext;
import org.librairy.computing.helper.ComputingHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created on 30/08/16:
 *
 * @author cbadenes
 */
public abstract class AbstractComputingHelper implements ComputingHelper {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractComputingHelper.class);

    @Value("#{environment['LIBRAIRY_COMPUTING_MEMORY']?:'${librairy.computing.memory}'}")
    private String sparkMem;

    @Value("#{environment['LIBRAIRY_COLUMNDB_HOST']?:'${librairy.columndb.host}'}")
    private String cassandraHost;

    @Value("#{environment['LIBRAIRY_COLUMNDB_PORT']?:${librairy.columndb.port}}")
    private String cassandraPort;

    @Value("#{environment['LIBRAIRY_COMPUTING_CORES']?:${librairy.computing.cores}}")
    protected Integer cores;


    AtomicInteger concurrentContexts = new AtomicInteger(0);

    Random random = new Random();

    protected abstract String getMaster();

    protected abstract Integer getPartitions();

    protected abstract SparkConf initializeConf(SparkConf conf);

    public synchronized ComputingContext newContext(String id) throws InterruptedException{
        waitForAvailableContexts();

        LOG.info("Creating a new Spark Context for '" + id + "'");
        ComputingContext computingContext = new ComputingContext();

        JavaSparkContext sc = initializeContext("librairy." + id);
        computingContext.setSparkContext(sc);
        computingContext.setSparkConf(sc.getConf());
        computingContext.setRecommendedPartitions(getPartitions());
        computingContext.setSqlContext(new SQLContext(sc));
        computingContext.setCassandraSQLContext(new CassandraSQLContext(sc.sc()));

        return computingContext;
    }

    protected JavaSparkContext initializeContext(String name){

        // Initialize Spark Context
        LOG.info("Spark configured at: " + getMaster());

        SparkConf auxConf = new SparkConf().
                setMaster(getMaster()).
                setAppName(name)
                .set("spark.app.id", name)
                .set("spark.cassandra.connection.host", cassandraHost)
                .set("spark.cassandra.connection.port", cassandraPort)
                .set("spark.driver.allowMultipleContexts" ,"true")
                .set("spark.driver.maxResultSize", "0");
//                .set("spark.executor.extraJavaOptions","-Dcom.sun.management.jmxremote -Dcom.sun.management
// .jmxremote.port=8095 -Dcom.sun.management.jmxremote.rmi.port=8096 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false -Djava.rmi.server.hostname=138.100.15.128 -Djava.net.preferIPv4Stack=true");

        if (!this.sparkMem.equalsIgnoreCase("-1")){
            LOG.info("Setting spark.executor.memory ="+sparkMem);
            auxConf = auxConf.set("spark.executor.memory", sparkMem);
        }


        SparkConf conf = initializeConf(auxConf);
        JavaSparkContext sc = new JavaSparkContext(conf);
        return sc;
    }

    @Override
    public Boolean execute(ComputingContext context , Runnable task) {
        try{
            task.run();
        }catch (Exception e){
            LOG.error("Unexpected error executing task",e);
            return false;
        }finally {
            close(context);
        }
        return true;
    }

    public void close(ComputingContext context){
        try{
            LOG.info("Stopping spark context '" + context.getSparkConf().getAppId() + "' ..");
            context.getSparkContext().stop();
            context.getSparkContext().close();
            LOG.info("Spark context '" + context.getSparkConf().getAppId() + "' closed");
        }catch (Exception e){
            LOG.warn("Error stopping spark context", e);
        }finally {
            int currentValue = concurrentContexts.decrementAndGet();
            if (currentValue > 0){
                LOG.warn("Current spark contexts greater than 1: '"+ currentValue + "'");
                concurrentContexts.set(0);
            }
        }
    }

    protected void waitForAvailableContexts() throws InterruptedException {
        while(concurrentContexts.get() > 0){
            int delay = random.nextInt(5)+2;
            LOG.debug("waiting " + delay + "secs for stop an active spark context");
            Thread.sleep(delay*1000);
        }

        concurrentContexts.incrementAndGet();
    }

}