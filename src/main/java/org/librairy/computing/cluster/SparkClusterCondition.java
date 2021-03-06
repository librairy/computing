/*
 * Copyright (c) 2016. Universidad Politecnica de Madrid
 *
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 *
 */

package org.librairy.computing.cluster;

import com.google.common.base.Strings;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * Created on 30/08/16:
 *
 * @author cbadenes
 */
public class SparkClusterCondition implements Condition {

    @Override
    public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
        String envVar  = System.getenv("LIBRAIRY_COMPUTING_CLUSTER");
        String ctxVar = conditionContext.getEnvironment().getProperty("librairy.computing.cluster");
        boolean condition = (Strings.isNullOrEmpty(envVar)
                && ctxVar.startsWith("spark"))
                ||
                (!Strings.isNullOrEmpty(envVar) && envVar.startsWith("spark"));
        return condition;
    }
}
