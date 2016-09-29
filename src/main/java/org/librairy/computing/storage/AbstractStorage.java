/*
 * Copyright (c) 2016. Universidad Politecnica de Madrid
 *
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 *
 */

package org.librairy.computing.storage;

import org.librairy.computing.helper.StorageHelper;
import org.springframework.beans.factory.annotation.Value;

/**
 * Created on 30/08/16:
 *
 * @author cbadenes
 */
public abstract class AbstractStorage implements StorageHelper {

    // hdfs://zavijava.dia.fi.upm.es:8020
    @Value("#{environment['LIBRAIRY_COMPUTING_FS']?:'${librairy.computing.fs}'}")
    protected String fileSystem;

    @Value("#{environment['LIBRAIRY_HOME']?:'${librairy.computing.home}'}")
    protected String homeFolder;

    protected String normalizedHome(String separator){
        if (homeFolder.startsWith(separator) && homeFolder.endsWith(separator)){
            return homeFolder;
        }else if (homeFolder.startsWith(separator)){
            return homeFolder + separator;
        }else if (homeFolder.endsWith(separator)){
            return separator + homeFolder;
        }else{
            return separator + homeFolder + separator;
        }
    }

}
