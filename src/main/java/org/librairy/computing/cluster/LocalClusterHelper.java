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
import org.librairy.computing.helper.LocalExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Created on 30/08/16:
 *
 * @author cbadenes
 */
@Component
@Conditional(LocalClusterCondition.class)
public class LocalClusterHelper extends AbstractComputingHelper {

    private static final Logger LOG = LoggerFactory.getLogger(LocalClusterHelper.class);

    @Value("#{environment['LIBRAIRY_COMPUTING_CLUSTER']?:'${librairy.computing.cluster}'}")
    private String master;

    @Autowired
    LocalExecutor executor;

    private ComputingContext context;

    @Override
    protected String getMaster() {
        return master;
    }

    @Override
    protected SparkConf initializeConf(SparkConf conf) {
        return conf;
    }

    @PostConstruct
    public void setup(){
        LOG.info("Creating a new Spark Context");
        this.context = new ComputingContext();

        JavaSparkContext sc = initializeContext("librairy.local");
        context.setSparkContext(sc);
        context.setSparkConf(sc.getConf());
        context.setRecommendedPartitions(getPartitions());
        context.setSqlContext(new SQLContext(sc));
        context.setCassandraSQLContext(new CassandraSQLContext(sc.sc()));
    }

    @Override
    public void close(ComputingContext context) {

    }

    @Override
    public ComputingContext newContext(String id) {
        return context;
    }

    @Override
    public Boolean execute(ComputingContext context, Runnable task) {
        try{
            executor.execute(() -> {
                try{
                    task.run();
                }catch (Exception e){
                    LOG.error("Unexpected error during task",e);
                }finally {
                    close(context);
                }
            });
        }catch (Exception e){
            LOG.error("Unexpected error on pool executor",e);
            return false;
        }
        return true;
    }

    @Override
    public Integer getPartitions() {
        return Runtime.getRuntime().availableProcessors();
    }
}