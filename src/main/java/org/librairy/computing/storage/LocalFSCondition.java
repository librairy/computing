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
public class LocalFSCondition implements Condition {

    @Override
    public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
        String envVar  = System.getenv("LIBRAIRY_COMPUTING_FS");
        return (Strings.isNullOrEmpty(envVar)
                && conditionContext.getEnvironment().getProperty("librairy.computing.fs").equalsIgnoreCase("local"))
                ||
                (!Strings.isNullOrEmpty(envVar) && envVar.equalsIgnoreCase("local"));
    }
}
