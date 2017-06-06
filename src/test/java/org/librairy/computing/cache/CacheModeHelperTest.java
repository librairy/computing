/*
 * Copyright (c) 2017. Universidad Politecnica de Madrid
 *
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 *
 */

package org.librairy.computing.cache;

import org.apache.spark.storage.StorageLevel;
import org.junit.Test;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
public class CacheModeHelperTest {

    @Test
    public void storageLevelTest(){

        String cacheMode = "memory_only";

        System.out.println(StorageLevel.fromString(cacheMode.toUpperCase()));


    }
}
