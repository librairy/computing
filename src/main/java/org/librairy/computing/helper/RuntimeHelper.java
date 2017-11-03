/*
 * Copyright (c) 2017. Universidad Politecnica de Madrid
 *
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 *
 */

package org.librairy.computing.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
@Component
public class RuntimeHelper {

    private static final Logger LOG = LoggerFactory.getLogger(RuntimeHelper.class);

    final long mb = 1024 * 1024;

    public long getMaxMemory(){
        return Runtime.getRuntime().maxMemory()/mb;
    }

    public long getAllocatedMemory(){
        return Runtime.getRuntime().totalMemory()/mb;
    }

    public long getFreeMemory(){
        return Runtime.getRuntime().freeMemory()/mb;
    }

    public long getTotalFreeMemory(){
        return getFreeMemory() + (getMaxMemory() - getAllocatedMemory());
    }

    public long getAvailableProcessors(){
        return Runtime.getRuntime().availableProcessors();
    }

}
