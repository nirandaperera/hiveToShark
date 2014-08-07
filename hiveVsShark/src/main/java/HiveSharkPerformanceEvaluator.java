/**
 * Created by niranda on 8/6/14.
 */

/**
 * id â€“ a unique identifier of the measurement [64 bit unsigned integer value]
 * timestamp â€“ timestamp of measurement (number of seconds since January 1, 1970, 00:00:00 GMT) [32 bit unsigned integer value]
 * value â€“ the measurement [32 bit floating point]
 * property â€“ type of the measurement: 0 for work or 1 for load [boolean]
 * plug_id â€“ a unique identifier (within a household) of the smart plug [32 bit unsigned integer value]
 * household_id â€“ a unique identifier of a household (within a house) where the plug is located [32 bit unsigned integer value]
 * house_id â€“ a unique identifier of a house where the household with the plug is located [32 bit unsigned integer value]
 */

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class HiveSharkPerformanceEvaluator {

    private static String driverName = "org.apache.hadoop.hive.jdbc.HiveDriver";
    private static ArrayList<String> queries = new ArrayList<String>();

    public static void main(String[] args) throws SQLException {

        //Create data files
        String inFile = "/media/niranda/data/projects/hiveToShark/data/sorted400M.txt";
        String outDir = "/home/niranda/projects/hiveToShark/data/";
        int lineLimits[] = {100, 1000, 10000, 100000, 1000000, 10000000, 100000000};
        //int lineLimits[] = {100};

        DataReader.createFiles(inFile, outDir, lineLimits);

        try {
            Class.forName(driverName);
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.exit(1);
        }

        Connection conShark = DriverManager.getConnection("jdbc:hive://0.0.0.0:10000/default", "", "");
        Connection conHive = DriverManager.getConnection("jdbc:hive://0.0.0.0:10001/default", "", "");

        File folder = new File(outDir);
        File[] listOfFiles = folder.listFiles();

        try {
            String logPath = "logs/log3.txt";

            File f = new File(logPath);
            if (f.exists() && !f.isDirectory()) {
                System.out.println("log file exists! enter new log file name");
                return;
            }

            PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(logPath)));


            for (int i = 0; i < listOfFiles.length; i++) {

//                if (!listOfFiles[i].getName().equalsIgnoreCase("doc100.txt")) continue;

                String filePath = "'file://" + listOfFiles[i].getPath() + "'";
                String fileName = listOfFiles[i].getName();
                String lines = fileName.substring(3, fileName.length() - 4);

                System.out.println("**** FILE WITH " + lines + " LINES ****");
                writer.print(lines + ", ");

                // EXECUTE QUERIES
                String query;

                //**** DDL OPERATIONS **** (Data Definition)
                //DROP DATABASE
                query = "drop database if exists hivedb" + lines + " cascade";
                executeQuery(query, conHive, writer);

                query = "drop database if exists sharkdb" + lines + " cascade";
                executeQuery(query, conShark, writer);


                //CREATE DATABASE
                query = "create database if not exists hivedb";
                executeQuery(query, conHive, writer);

                query = "create database if not exists sharkdb";
                executeQuery(query, conShark, writer);


                //CREATE TABLES
                String tbl_props = "id bigint, " +
                        "time_stamp int, " +
                        "value float, " +
                        "measure_type boolean, " +
                        "plug_id int, " +
                        "household_id int, " +
                        "house_id int ";

                query = "create table if not exists hivedb.data" + lines + " (" + tbl_props + ") " +
                        "row format delimited FIELDS TERMINATED BY ','";
                executeQuery(query, conHive, writer);
                query = "create table if not exists hivedb.data0_" + lines + "(id bigint)";
                executeQuery(query, conHive, writer);

                query = "create table if not exists sharkdb.data" + lines + " (" + tbl_props + ") " +
                        "row format delimited FIELDS TERMINATED BY ','";
                executeQuery(query, conShark, writer);
                query = "create table if not exists sharkdb.data0_" + lines + "(id bigint)";
                executeQuery(query, conShark, writer);


                // **** DML OPERATIONS**** (Data Manupulation)
                //LOAD TABLES
                query = "LOAD DATA local INPATH " + filePath + " overwrite into table hivedb.data" + lines;
                executeQuery(query, conHive, writer);

                query = "LOAD DATA local INPATH " + filePath + " overwrite into table sharkdb.data" + lines;
                executeQuery(query, conShark, writer);


                // **** SQL OPERATIONS ****
                //SELECT AND INSERT
                query = "INSERT overwrite table hivedb.data0_" + lines +
                        " SELECT" +
                        " id" +
                        " from hivedb.data" + lines;
                executeQuery(query, conHive, writer);

                query = "INSERT overwrite table sharkdb.data0_" + lines +
                        " SELECT" +
                        " id" +
                        " from sharkdb.data" + lines;
                executeQuery(query, conShark, writer);


                //INSERT AND SELECT WITH split
                //additional table creation


                System.out.println("**** FILE WITH " + lines + " LINES: DONE ****");

                writer.println();

            }

            writer.println(queries.toString());
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("DONE!");

    }


    public static void executeQuery(String query, Connection con, PrintWriter writer) {
        long start, end;
        float dur;

        System.out.print(query.substring(0, 20));

        Statement stmt = null;
        try {
            stmt = con.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        start = System.nanoTime();
        ResultSet result = null;
        try {
            result = stmt.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        end = System.nanoTime();
        dur = (float) (end - start);
        dur = dur / (1000000);

        System.out.print(" Result: " + result.toString().substring(0, 20) + " Elapsed time: " + dur + "ms");
        System.out.println();
        queries.add(query + "n");
        writer.print(dur + ", ");
    }

}
