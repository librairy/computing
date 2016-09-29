/*
 * Copyright (c) 2016. Universidad Politecnica de Madrid
 *
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 *
 */

package org.librairy.computing.cluster;

import org.apache.spark.SparkConf;
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

    @Override
    protected SparkConf initializeConf(SparkConf conf) {

        return conf;
    }

}
