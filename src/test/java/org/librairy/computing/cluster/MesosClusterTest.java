/*
 * Copyright (c) 2016. Universidad Politecnica de Madrid
 *
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 *
 */

package org.librairy.computing.cluster;

import es.cbadenes.lab.test.IntegrationTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.librairy.computing.Config;
import org.librairy.computing.helper.SparkHelper;
import org.librairy.computing.tasks.W2VExample;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created on 30/08/16:
 *
 * @author cbadenes
 */
@Category(IntegrationTest.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = Config.class)
@TestPropertySource(properties = {
        "librairy.columndb.host = wiener.dia.fi.upm.es",
//        "librairy.columndb.port = 5011",
        "librairy.documentdb.host = wiener.dia.fi.upm.es",
//        "librairy.documentdb.port = 5021",
        "librairy.graphdb.host = wiener.dia.fi.upm.es",
//        "librairy.graphdb.port = 5030",
        "librairy.eventbus.host = local",
        "librairy.computing.cluster=mesos://zavijava.dia.fi.upm.es:5050",
        "librairy.computing.fs=hdfs://zavijava.dia.fi.upm.es:8020"
//        "librairy.computing.spark.path=/opt/mesos/spark/installation/current"
})
public class MesosClusterTest {

    /**
     * set HADOOP_USER_NAME for testing
     */

    private static final Logger LOG = LoggerFactory.getLogger(MesosClusterTest.class);

    @Autowired
    SparkHelper sparkHelper;

    @Autowired
    Partitioner partitioner;


    @Test
    public void execution(){

        W2VExample task = new W2VExample(sparkHelper, partitioner);
        task.execute();

    }

}
