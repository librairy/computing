/*
 * Copyright (c) 2017. Universidad Politecnica de Madrid
 *
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 *
 */

package org.librairy.computing.cache;

import com.google.common.base.Strings;
import org.apache.spark.storage.StorageLevel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
@Component
public class CacheModeHelper {


    @Value("#{environment['LIBRAIRY_COMPUTING_CACHE']?:'${librairy.computing.cache}'}")
    String cacheMode;

    StorageLevel storageLevel = StorageLevel.MEMORY_ONLY();

    @PostConstruct
    public void setup(){

        if (!Strings.isNullOrEmpty(cacheMode)){
            storageLevel = StorageLevel.fromString(cacheMode.toUpperCase());
        }

    }

    public StorageLevel getLevel(){
        return storageLevel;
    }

}
