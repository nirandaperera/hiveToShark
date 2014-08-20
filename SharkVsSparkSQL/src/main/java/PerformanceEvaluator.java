/**
 * Created by niranda on 8/11/14.
 */


import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.sql.api.java.JavaSQLContext;
import org.apache.spark.sql.api.java.JavaSchemaRDD;
import org.apache.spark.sql.api.java.Row;
import org.apache.spark.sql.hive.api.java.JavaHiveContext;

import java.util.List;
import java.util.Scanner;

/**
 * id â€“ a unique identifier of the measurement [64 bit unsigned integer value]
 * timestamp â€“ timestamp of measurement (number of seconds since January 1, 1970, 00:00:00 GMT) [32 bit unsigned integer value]
 * value â€“ the measurement [32 bit floating point]
 * property â€“ type of the measurement: 0 for work or 1 for load [boolean]
 * plug_id â€“ a unique identifier (within a household) of the smart plug [32 bit unsigned integer value]
 * household_id â€“ a unique identifier of a household (within a house) where the plug is located [32 bit unsigned integer value]
 * // * house_id â€“ a unique identifier of a house where the household with the plug is located [32 bit unsigned integer value]
 */

public class PerformanceEvaluator {
    public static void main(String[] args) throws Exception {

        long start, end;
        TimingCalc timer = new TimingCalc();

        String textFilePath = "/home/niranda/projects/hiveToShark/data/data/data1000000.txt";
        String textFileDir = "";
        int lineLimits[] = {100, 1000, 10000, 100000, 1000000, 10000000, 100000000};


        SparkConf sparkConf = new SparkConf().setAppName("JavaSparkSQL")
                .setMaster("local[8]")//"spark://niranda-ThinkPad-T540p:7077"
                .setSparkHome("/home/niranda/software/spark-1.0.1-bin-hadoop1");
        JavaSparkContext ctx = new JavaSparkContext(sparkConf);


//        JavaSQLContext sqlCtx = new JavaSQLContext(ctx);
//        QueryExecutor executor = new QueryExecutor(sqlCtx);

        JavaHiveContext hiveCtx = new JavaHiveContext(ctx);
        QueryExecutorHive executorHive = new QueryExecutorHive(hiveCtx);

        System.out.println("=== Data source: RDD === Start");
        start = System.nanoTime();
        JavaRDD<DebsEvent> events;
        events = ctx.textFile(textFilePath).map(
                new Function<String, DebsEvent>() {
                    @Override
                    public DebsEvent call(String s) throws Exception {
                        String[] parts = s.split(",");

                        DebsEvent event = new DebsEvent();

                        event.setId(Long.parseLong(parts[0]));
                        event.setTimeStamp(Integer.parseInt(parts[1]));
                        event.setValue(Float.parseFloat(parts[2]));
                        event.setMeasureType(Boolean.parseBoolean(parts[3]));
                        event.setPlugId(Integer.parseInt(parts[4]));
                        event.setHouseholdId(Integer.parseInt(parts[5]));
                        event.setHouseId(Integer.parseInt(parts[6]));

                        return event;
                    }
                });
        end = System.nanoTime();
        long numberOfEvents = events.count();
        System.out.println("=== Data source: RDD === End" +
                " Events: " + numberOfEvents +
                " Time: " + timer.getTimeInMilli(start, end) + " ms");

//        JavaSchemaRDD schemaEvents = sqlCtx.applySchema(events, DebsEvent.class);
//        schemaEvents.registerAsTable("events" + numberOfEvents);
//        System.out.println(schemaEvents.toString());

        JavaSchemaRDD schemaEventsHive = hiveCtx.applySchema(events, DebsEvent.class);
        schemaEventsHive.registerAsTable("events" + numberOfEvents);
        System.out.println(schemaEventsHive.toString());

//        executor.setSqlCtx(sqlCtx);
        executorHive.setHiveCtx(hiveCtx);

        String query = "SELECT * FROM events" + numberOfEvents + " WHERE houseId > 10";
//        String query = "CREATE DATABASE testdb1234";


//        JavaSchemaRDD resultRDD = executor.executeQuery(query);
        JavaSchemaRDD resultRDD = executorHive.executeQuery(query);
        printRDDString(resultRDD);
        System.out.println(executorHive.getQueryString() + " time: " + executorHive.getExecutionTimeInMilli() + " ms");

        Scanner keyboard = new Scanner(System.in);
        int temp = 1;
        while (temp != 0) {
            System.out.println("press 0 to exit!");
            temp = keyboard.nextInt();
        }
        ctx.stop();
    }

    public static String printRDDString(JavaSchemaRDD resultRDD) {
        List<DebsEvent> resultList = resultRDD.map(new Function<Row, DebsEvent>() {
            @Override
            public DebsEvent call(Row row) throws Exception {
                DebsEvent temp = new DebsEvent();

                temp.setHouseId(row.getInt(0));
                temp.setHouseholdId(row.getInt(1));
                temp.setId(row.getLong(2));
                temp.setMeasureType(row.getBoolean(3));
                temp.setPlugId(row.getInt(4));
                temp.setTimeStamp(row.getInt(5));
                temp.setValue(row.getFloat(6));

                return temp;
            }
        }).collect();
        System.out.println("Result: " + resultList.get(0).convertToString() + " Size: " + resultList.size());
        return resultList.get(0).convertToString();
    }


        /*
        start = System.nanoTime();
        JavaSchemaRDD resultRDD = sqlCtx.sql(query);
        end = System.nanoTime();

        List<String> resultList = resultRDD.map(new Function<Row, String>() {
                                                    @Override
                                                    public String call(Row row) throws Exception {
                                                        return Long.toString(row.getLong(0));
//                                                        return row.getString(0);
                                                    }
                                                }
        ).collect();
        System.out.println("Result: " + resultList.get(0) + " Size: " + resultList.size());
        System.out.println(query + " Time: " + timer.getTimeInMilli(start, end) + " ms");
        */


}

