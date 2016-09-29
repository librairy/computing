/*
 * Copyright (c) 2016. Universidad Politecnica de Madrid
 *
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 *
 */

package org.librairy.computing.storage;

import com.google.common.base.Strings;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * Created on 30/08/16:
 *
 * @author cbadenes
 */
public class HDFSCondition implements Condition {

    @Override
    public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
        String envVar  = System.getenv("LIBRAIRY_COMPUTING_FS");
        return (Strings.isNullOrEmpty(envVar)
                && conditionContext.getEnvironment().getProperty("librairy.computing.fs").startsWith("hdfs"))
                ||
                (!Strings.isNullOrEmpty(envVar) && envVar.startsWith("hdfs"));
    }
}