/*
 * Copyright (c) 2016. Universidad Politecnica de Madrid
 *
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 *
 */

package org.librairy.computing.tasks;

import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.mllib.feature.Word2Vec;
import org.apache.spark.mllib.feature.Word2VecModel;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.cassandra.CassandraSQLContext;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.librairy.boot.model.domain.resources.Resource;
import org.librairy.computing.cluster.ComputingContext;
import org.librairy.computing.cluster.Partitioner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

/**
 * Created on 30/08/16:
 *
 * @author cbadenes
 */
public class W2VExample implements Runnable{

    private static final Logger LOG = LoggerFactory.getLogger(W2VExample.class);

    @Getter
    private final ComputingContext context;
    private final Partitioner partitioner;

    public W2VExample(ComputingContext computingContext, Partitioner partitioner){
        this.context = computingContext;
        this.partitioner = partitioner;
    }


    @Override
    public void run() {
        LocalTime start = LocalTime.now();

        // Create a Data Frame from Cassandra query
        CassandraSQLContext cc = new CassandraSQLContext(this.context.getSparkContext().sc());

        // Define a schema
        StructType schema = DataTypes
                .createStructType(new StructField[] {
                        DataTypes.createStructField("uri", DataTypes.StringType, false),
                        DataTypes.createStructField("content", DataTypes.StringType, false)
                });

        DataFrame df = cc
                .read()
                .format("org.apache.spark.sql.cassandra")
                .schema(schema)
                .option("inferSchema", "false") // Automatically infer data types
                .option("charset", "UTF-8")
                .option("mode","DROPMALFORMED")
                .options(ImmutableMap.of("table", "items", "keyspace", "research"))
                .load()
//                .repartition(500)
                .cache();

        df.take(1);

        JavaRDD<List<String>> input = df
                .toJavaRDD()
                .map(row -> Arrays.asList(row.getString(1).split(" ")))
                .cache();

        input.take(1); //force cache

        LOG.info("Building a new W2V Model ..");
        Word2Vec word2Vec = new Word2Vec();
        word2Vec.setNumPartitions(this.context.getRecommendedPartitions());//1500
        word2Vec.setVectorSize(100);
        word2Vec.setNumIterations(5); //5
        Word2VecModel model = word2Vec.fit(input);

        LocalTime end = LocalTime.now();
        Duration elapsedTime = Duration.between(start, end);

        LOG.info("Elapsed Time: " + elapsedTime.getSeconds() + "secs");

        LOG.info("Synonyms of 'image' are: ");
        Arrays.stream(model.findSynonyms("image", 10)).forEach(tuple ->{
            LOG.info("- " + tuple._1 + "\t:" + tuple._2);
        });
    }
}
