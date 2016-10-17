/*
 * Copyright (c) 2016. Universidad Politecnica de Madrid
 *
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 *
 */

package org.librairy.computing.storage;

import es.cbadenes.lab.test.IntegrationTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.librairy.computing.Config;
import org.librairy.computing.helper.StorageHelper;
import org.librairy.storage.generator.URIGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created on 30/08/16:
 *
 * @author cbadenes
 */
@Category(IntegrationTest.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = Config.class)
@TestPropertySource(properties = {
        "librairy.computing.fs = hdfs://zavijava.dia.fi.upm.es:8020"
})
public class HDFSStorageTest {

    /**
     * set HADOOP_USER_NAME for testing
     */

    private static final Logger LOG = LoggerFactory.getLogger(HDFSStorageTest.class);

    @Autowired
    StorageHelper storageHelper;

    @Autowired
    URIGenerator uriGenerator;

    @Test
    public void save(){

//        String input = "/Users/cbadenes/Projects/librairy/distribution/target/librairy-dependencies.jar";
//        String output = "/librairy/lib/librairy-dependencies.jar";

//        String input = "/Users/cbadenes/Downloads/mesos-0.28.1/build/src/.libs/libmesos-0.28.1.dylib";
//        String output = "/librairy/lib/libmesos.dylib";

        String input = "/Users/cbadenes/Downloads/spark-1.6.2-bin-hadoop2.6.tgz";
        String output = "/librairy/lib/spark-1.6.2-bin-hadoop2.6.tgz";


        File file = new File(input);
        System.out.println(file.exists());
        storageHelper.save(output, file);

    }


    @Test
    public void deleteIfExists(){

        //storageHelper.deleteIfExists("/librairy/lib/librairy-dependencies.jar");
        //storageHelper.deleteIfExists("/librairy/lib/libmesos.dylib");
        storageHelper.deleteIfExists("/librairy/lib/spark-1.5.2-bin-hadoop2.6.tgz");

    }

    @Test
    public void read() throws IOException, URISyntaxException {

        //File file = storageHelper.read("/librairy/domains/4f56ab24bb6d815a48b8968a3b157470/stopwords.txt");
        File file = storageHelper.read("/librairy/lib/libmesos.dylib");

        LOG.info("File Path: " + file);

//        String content = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
//        System.out.println(content);

    }


}
