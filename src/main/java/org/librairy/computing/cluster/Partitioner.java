/*
 * Copyright (c) 2016. Universidad Politecnica de Madrid
 *
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 *
 */

package org.librairy.computing.cluster;

import org.apache.spark.util.SizeEstimator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created on 30/08/16:
 *
 * @author cbadenes
 */
@Component
public class Partitioner {

    private static final Logger LOG = LoggerFactory.getLogger(Partitioner.class);

    @Value("#{environment['LIBRAIRY_COMPUTING_SIZE_PER_TASKS']?:'${librairy.computing.task.size}'}")
    private Integer sizePerTask;

    public int estimatedFor(Object rdd){

        long estimatedBytes = SizeEstimator.estimate(rdd);


        long bytesPerTask   = sizePerTask * 1000; //KB per task

        int estimatedPartitions = Long.valueOf(estimatedBytes / bytesPerTask).intValue();

        LOG.info("Estimated partitions: " + estimatedPartitions);
        return estimatedPartitions;
    }
}
