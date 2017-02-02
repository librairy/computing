/*
 * Copyright (c) 2016. Universidad Politecnica de Madrid
 *
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 *
 */

package org.librairy.computing.helper;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;

/**
 * Created on 30/08/16:
 *
 * @author cbadenes
 */
public interface SparkHelper {

    SparkConf getConf();

    JavaSparkContext getContext();

    Boolean execute (Runnable task);

    Integer getPartitions();

}