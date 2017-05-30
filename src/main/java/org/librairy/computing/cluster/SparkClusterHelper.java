/*
 * Copyright (c) 2016. Universidad Politecnica de Madrid
 *
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 *
 */

package org.librairy.computing.cluster;

import org.apache.spark.SparkConf;
import org.librairy.computing.helper.StorageHelper;
import org.librairy.computing.storage.HDFSStorage;
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
public class SparkClusterHelper extends AbstractComputingHelper {

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

        String libPath      = storageHelper.absolutePath(homePath+"lib/librairy-dependencies.jar");
        LOG.info("loading librairy dependencies from: " + libPath);

        String cloneConf =  (storageHelper instanceof HDFSStorage)? "true" : "false";

        SparkConf auxConf = conf
                .set("spark.cores.max", String.valueOf(cores))
                .set("spark.driver.cores", "2")
//                .set("spark.reducer.maxSizeInFlight", "512m")
//                .set("spark.shuffle.file.buffer", "48m")
//                .set("spark.shuffle.compress", "false")
//                .set("spark.shuffle.spill.compress", "false")
//                .set("spark.broadcast.compress","false")
//                .set("spark.default.parallelism",String.valueOf(cores) )
                .set("spark.files.overwrite", "true")
                .set("spark.hadoop.cloneConf", cloneConf)
                .setJars(new String[]{libPath})
                ;
        return auxConf;
    }

    @Override
    public Integer getPartitions() {
        return cores;
    }
}
