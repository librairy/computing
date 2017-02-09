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

    protected abstract String getMaster();

    protected abstract Integer getPartitions();

    protected abstract SparkConf initializeConf(SparkConf conf);

    public abstract void close(ComputingContext context);

    public abstract ComputingContext newContext(String id);

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

}