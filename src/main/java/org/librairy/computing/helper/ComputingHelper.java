/*
 * Copyright (c) 2016. Universidad Politecnica de Madrid
 *
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 *
 */

package org.librairy.computing.helper;

import org.librairy.computing.cluster.ComputingContext;

/**
 * Created on 30/08/16:
 *
 * @author cbadenes
 */
public interface ComputingHelper {

    ComputingContext newContext(String id);

    Boolean execute (ComputingContext context, Runnable task);

    void close(ComputingContext context);
}