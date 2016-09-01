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
        return (Strings.isNullOrEmpty(envVar)
                && conditionContext.getEnvironment().getProperty("librairy.computing.cluster").startsWith("spark"))
                ||
                (!Strings.isNullOrEmpty(envVar) && envVar.startsWith("spark"));
    }
}
