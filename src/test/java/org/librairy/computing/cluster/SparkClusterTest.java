/*
 * Copyright (c) 2017. Universidad Politecnica de Madrid
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
        "librairy.columndb.host = wiig.dia.fi.upm.es",
        "librairy.documentdb.host = wiig.dia.fi.upm.es",
        "librairy.graphdb.host = wiig.dia.fi.upm.es",
        "librairy.eventbus.host = local",
        "librairy.computing.cluster=spark://minetur.dia.fi.upm.es:7077",
        "librairy.computing.fs=hdfs://minetur.dia.fi.upm.es:9000",
        "librairy.computing.cores=120",
        "librairy.computing.memory=64g"
})
public class SparkClusterTest {

    /**
     * set HADOOP_USER_NAME for testing
     */

    private static final Logger LOG = LoggerFactory.getLogger(SparkClusterTest.class);

    @Autowired
    ComputingHelper computingHelper;

    @Autowired
    Partitioner partitioner;


    @Test
    public void execution(){

        ComputingContext computingContext = computingHelper.newContext("w2v.sample");

        LOG.info("executing w2v");
        W2VExample task = new W2VExample(computingContext, partitioner);
        computingHelper.execute(computingContext, task);

    }

}
