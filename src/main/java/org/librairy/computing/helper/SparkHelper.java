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

}