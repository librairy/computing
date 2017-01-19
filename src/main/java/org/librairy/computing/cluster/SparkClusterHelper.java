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

    @Value("${librairy.computing.spark.package}")
    private String sparkPackage;

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

        String sparkPath    = storageHelper.absolutePath(homePath+"lib/"+sparkPackage+".tgz");
        LOG.info("loading spark binary from: " + sparkPath);

        String libPath      = storageHelper.absolutePath(homePath+"lib/librairy-dependencies.jar");
        LOG.info("loading librairy dependencies from: " + libPath);


        SparkConf auxConf = conf
                .set("spark.executor.uri", sparkPath)
                .setJars(new String[]{libPath})
                ;


        return auxConf;
    }

}
