/*
 * Copyright (c) 2016. Universidad Politecnica de Madrid
 *
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 *
 */

package org.librairy.computing.cluster;

import org.apache.spark.SparkConf;
import org.librairy.computing.helper.StorageHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

/**
 * Created on 30/08/16:
 *
 * @author cbadenes
 */
@Component
@Conditional(SparkClusterCondition.class)
public class SparkClusterHelper extends AbstractSparkHelper {

    @Value("#{environment['LIBRAIRY_COMPUTING_CLUSTER']?:'${librairy.computing.cluster}'}")
    private String master;

    @Override
    protected String getMaster() {
        return master;
    }

    @Autowired
    StorageHelper storageHelper;

    private static final Logger LOG = LoggerFactory.getLogger(SparkClusterHelper.class);

    @Override
    protected SparkConf initializeConf(SparkConf conf) {

        String homePath     = storageHelper.getHome();
        LOG.info("librairy home=" + homePath);

//        String sparkPath    = storageHelper.absolutePath(homePath+"lib/"+sparkPackage+".tgz");
//        LOG.info("loading spark binary from: " + sparkPath);

        String libPath      = storageHelper.absolutePath(homePath+"lib/librairy-dependencies.jar");
        LOG.info("loading librairy dependencies from: " + libPath);


        SparkConf auxConf = conf
//                .set("spark.executor.uri", sparkPath)
//                .set("spark.driver.memory","24576M")
//                .set("spark.cores.max","24")
//                .set("spark.executor.cores","6")
//                .set("spark.executor.memory","82g")
                .setJars(new String[]{libPath})
                ;


        return auxConf;
    }

    @Override
    public Boolean execute(Runnable task) {
        try{
            task.run();
        }catch (Exception e){
            LOG.error("Unexpected error executing task",e);
            return false;
        }
        return true;
    }

    @Override
    public Integer getPartitions() {
        return cores;
    }
}
