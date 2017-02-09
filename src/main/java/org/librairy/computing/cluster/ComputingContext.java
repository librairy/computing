/*
 * Copyright (c) 2017. Universidad Politecnica de Madrid
 *
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 *
 */

package org.librairy.computing.cluster;

import lombok.Data;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.cassandra.CassandraSQLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
@Data
public class ComputingContext {

    private static final Logger LOG = LoggerFactory.getLogger(ComputingContext.class);

    SparkConf sparkConf;

    JavaSparkContext sparkContext;

    Integer recommendedPartitions;

    SQLContext sqlContext;

    CassandraSQLContext cassandraSQLContext;

}
