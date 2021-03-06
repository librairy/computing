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
import org.librairy.computing.helper.ComputingHelper;
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
    ComputingHelper computingHelper;

    @Autowired
    Partitioner partitioner;


    @Test
    public void execution(){
        ComputingContext computingContext = null;
        try {
            computingContext = computingHelper.newContext("w2v.sample");
            W2VExample task = new W2VExample(computingContext, partitioner);
            computingHelper.execute(computingContext, task);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

}
