/*
 * Copyright (c) 2016. Universidad Politecnica de Madrid
 *
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 *
 */

package org.librairy.computing.cluster;

import org.apache.mesos.MesosNativeLibrary;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.cassandra.CassandraSQLContext;
import org.librairy.computing.helper.OSHelper;
import org.librairy.computing.helper.StorageHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created on 30/08/16:
 *
 * @author cbadenes
 */
@Component
@Conditional(MesosClusterCondition.class)
public class MesosClusterHelper extends AbstractComputingHelper {

    private static final Logger LOG = LoggerFactory.getLogger(MesosClusterHelper.class);

    @Value("#{environment['LIBRAIRY_COMPUTING_CLUSTER']?:'${librairy.computing.cluster}'}")
    private String master;

    @Value("#{environment['MESOS_SPARK_HOME']?:'${librairy.computing.spark.path}'}")
    private String mesosHome;

    @Value("#{environment['SPARK_MEMORY']?:'-1'}")
    private String mesosMem;

    @Value("#{environment['MESOS_USER_NAME']?:'-1'}")
    private String mesosUserName;

    @Value("#{environment['MESOS_USER_PWD']?:'-1'}")
    private String mesosUserPwd;

    @Value("#{environment['MESOS_USER_ROLE']?:'-1'}")
    private String mesosUserRole;

    @Value("${librairy.computing.spark.package}")
    private String sparkPackage;

    @Override
    protected String getMaster() {
        return master;
    }

    @Autowired
    StorageHelper storageHelper;

    AtomicInteger concurrentContexts = new AtomicInteger(0);

    Random random = new Random();


    @Override
    protected SparkConf initializeConf(SparkConf conf) {

        String homePath     = storageHelper.getHome();
        LOG.info("librairy home=" + homePath);

        try {
            String extension = OSHelper.isMac()? "dylib" : "so";
            String nativeLibPath = homePath + "lib/libmesos." + extension;
            LOG.info("loading MESOS_NATIVE_LIB from: " + storageHelper.absolutePath(nativeLibPath));
            File nativeLib = storageHelper.read(nativeLibPath);
            LOG.debug("Native lib: " + nativeLib.getAbsolutePath());

            MesosNativeLibrary.load(nativeLib.getAbsolutePath());
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }


        String sparkPath    = storageHelper.absolutePath(homePath+"lib/"+sparkPackage+".tgz");
        LOG.info("loading spark binary from: " + sparkPath);

        String libPath      = storageHelper.absolutePath(homePath+"lib/librairy-dependencies.jar");
        LOG.info("loading librairy dependencies from: " + libPath);

        LOG.info("setting MESOS_SPARK_HOME to: " + mesosHome);

        SparkConf auxConf = conf
                .set("spark.executor.uri", sparkPath)
                .set("spark.mesos.executor.home", mesosHome)
                .setJars(new String[]{libPath})
                ;

        if (!mesosMem.equalsIgnoreCase("-1")) {
            LOG.info("setting 'spark.executor.memory="+mesosMem+"'");
            auxConf = auxConf.set("spark.executor.memory", mesosMem);
        }

        if (!mesosUserName.equalsIgnoreCase("-1")){
            LOG.info("setting 'spark.mesos.principal="+mesosUserName+"'");
            auxConf = auxConf.set("spark.mesos.principal", mesosUserName);
        }

        if (!mesosUserPwd.equalsIgnoreCase("-1")){
            LOG.info("setting 'spark.mesos.secret="+mesosUserPwd+"'");
            auxConf = auxConf.set("spark.mesos.secret", mesosUserPwd);
        }

        if (!mesosUserRole.equalsIgnoreCase("-1")){
            LOG.info("setting 'spark.mesos.role="+mesosUserRole+"'");
            auxConf = auxConf.set("spark.mesos.role", mesosUserRole);
        }

        return auxConf;
    }


    @Override
    public Integer getPartitions() {
        return cores;
    }
}