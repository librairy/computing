/*
 * Copyright (c) 2016. Universidad Politecnica de Madrid
 *
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 *
 */

package org.librairy.computing.cluster;

import org.apache.spark.SparkConf;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

/**
 * Created on 30/08/16:
 *
 * @author cbadenes
 */
@Component
@Conditional(LocalClusterCondition.class)
public class LocalClusterHelper extends AbstractSparkHelper {

    @Override
    protected String getMaster() {
        return "local[*]";
    }

    @Override
    protected SparkConf initializeConf(SparkConf conf) {
        return conf;
    }

}