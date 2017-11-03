/*
 * Copyright (c) 2016. Universidad Politecnica de Madrid
 *
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 *
 */

package org.librairy.computing.cluster;

import org.apache.commons.lang.StringUtils;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.cassandra.CassandraSQLContext;
import org.librairy.computing.helper.ComputingHelper;
import org.librairy.computing.helper.RuntimeHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

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

    @Autowired
    RuntimeHelper runtimeHelper;

    AtomicInteger concurrentContexts = new AtomicInteger(0);

    Random random = new Random();

    protected abstract String getMaster();

    protected abstract Integer getPartitions();

    protected abstract SparkConf initializeConf(SparkConf conf);

    public synchronized ComputingContext newContext(String id) throws InterruptedException{
        waitForAvailableContexts();

        try{
            LOG.info("Creating a new Spark Context for '" + id + "'");
            ComputingContext computingContext = new ComputingContext();

            JavaSparkContext sc = initializeContext("librairy." + id);
            computingContext.setSparkContext(sc);
            computingContext.setSparkConf(sc.getConf());
            computingContext.setRecommendedPartitions(getPartitions());
            computingContext.setSqlContext(new SQLContext(sc));
            computingContext.setCassandraSQLContext(new CassandraSQLContext(sc.sc()));

            return computingContext;
        }catch (Exception e){
            decrementAvailableContextsCounter();
            throw e;
        }
    }

    protected JavaSparkContext initializeContext(String name){

        // Initialize Spark Context
        LOG.info("Spark configured at: " + getMaster());

        SparkConf auxConf = new SparkConf().
                setMaster(getMaster()).
                setAppName(name)
                .set("spark.app.id", name)

                // Cassandra Connection Parameters
                .set("spark.cassandra.connection.host", cassandraHost)
                .set("spark.cassandra.connection.port", cassandraPort)
                .set("spark.cassandra.connection.keep_alive_ms", "60000") // default: 5000
                .set("spark.cassandra.connection.timeout_ms", "60000") // default: 5000
                .set("spark.cassandra.read.timeout_ms", "120000") // default: 120000

                // Cassandra Read Tuning Parameters
                .set("spark.cassandra.input.consistency.level", "LOCAL_ONE") // default: LOCAL_ONE
                .set("spark.cassandra.input.fetch.size_in_rows", "250") // default: 1000
                .set("spark.cassandra.input.metrics", "false") // default: true

                // Cassandra Write Tuning Parameters
                .set("spark.cassandra.output.batch.grouping.buffer.size", "250") // default: 1000
                .set("spark.cassandra.output.batch.grouping.key", "none") // default: Partition
                .set("spark.cassandra.output.batch.size.bytes", "512") // default: 1024
//                .set("spark.cassandra.output.batch.size.rows", "250") // default: auto
                .set("spark.cassandra.output.concurrent.writes", "5") // default: 5
                .set("spark.cassandra.output.consistency.level", "LOCAL_ONE") // default: LOCAL_QUORUM
                .set("spark.cassandra.output.ignoreNulls", "true") // default: false
                .set("spark.cassandra.output.metrics", "false") // default: true
                .set("spark.cassandra.output.consistency.level", "LOCAL_ONE") // default: LOCAL_QUORUM

                // Spark Contexts
                .set("spark.driver.allowMultipleContexts" ,"true")
                .set("spark.driver.maxResultSize", "0");
//                .set("spark.executor.extraJavaOptions","-Dcom.sun.management.jmxremote -Dcom.sun.management
// .jmxremote.port=8095 -Dcom.sun.management.jmxremote.rmi.port=8096 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false -Djava.rmi.server.hostname=138.100.15.128 -Djava.net.preferIPv4Stack=true");

        Double maxMemory = Double.valueOf(runtimeHelper.getMaxMemory());

        if (!this.sparkMem.equalsIgnoreCase("-1")){
            LOG.info("Setting spark.executor.memory ="+sparkMem);

            if (sparkMem.contains("g")){
                maxMemory = Double.valueOf(StringUtils.substringBefore(sparkMem,"g"))*1024;
            }

            auxConf = auxConf
                    .set("spark.executor.memory", sparkMem);
        }

        Double partitionMemory = (maxMemory/ 2.0) / Double.valueOf(getPartitions());
        auxConf = auxConf.set("spark.cassandra.input.split.size_in_mb", String.valueOf(partitionMemory.intValue())); // default: 64

        String summary = Arrays.stream(auxConf.getAll())
                .map(param -> "Parameter: " + param._1 + " = " + param._2)
                .collect(Collectors.joining("\n"));
        LOG.debug("Spark Context configured by: \n" + summary);

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
            decrementAvailableContextsCounter();
        }
    }

    protected void waitForAvailableContexts() throws InterruptedException {
        while(concurrentContexts.get() > 0){
            int delay = random.nextInt(5)+2;
            LOG.debug("waiting " + delay + "secs for stop active spark contexts [" + concurrentContexts.get() +"]");
            Thread.sleep(delay*1000);
        }

        try{
            concurrentContexts.incrementAndGet();
        }catch (Exception e){
            decrementAvailableContextsCounter();
        }
    }

    private void decrementAvailableContextsCounter(){
        if (concurrentContexts.get()>0) concurrentContexts.decrementAndGet();
    }

}