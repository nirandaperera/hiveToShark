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

import org.yecht.Data;

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
        String outDir = "/home/niranda/projects/hiveToShark/data/data/";
        String outDir1 = "/home/niranda/projects/hiveToShark/data/additional/";
        int lineLimits[] = {100, 1000, 10000, 100000, 1000000, 10000000, 100000000};
        //int lineLimits[] = {100};

//        DataReader.createFiles(inFile, outDir, lineLimits);
//        DataReader.createAdditionalFiles(inFile,outDir1,lineLimits);

        try {
            Class.forName(driverName);
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.exit(1);
        }

        Connection conShark = DriverManager.getConnection("jdbc:hive://0.0.0.0:10000/default", "", "");
        Connection conHive = DriverManager.getConnection("jdbc:hive://0.0.0.0:10001/default", "", "");

//        File folder = new File(outDir);
//        File[] listOfFiles = folder.listFiles();
//
//        File folder1 = new File(outDir1);
//        File[] listOfFiles1 = folder1.listFiles();


        try {
            String logPath = "logs/log10.txt";

            File f = new File(logPath);
            if (f.exists() && !f.isDirectory()) {
                System.out.println("log file exists! enter new log file name");
                return;
            }

            PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(logPath)));


            for (int i = 0; i < lineLimits.length; i++) {

                if (lineLimits[i]>=10000000) continue;

                String filePath = "'file://" + outDir+"data"+lineLimits[i] + ".txt'";
//                String fileName = listOfFiles[i].getName();


                String filePath1 = "'file://" + outDir1 +"house"+lineLimits[i]+ ".txt'";
                String lines = Integer.toString(lineLimits[i]);

                // ignore if,
                if (lines.equalsIgnoreCase("100000000")) continue;

                System.out.println("**** FILE WITH " + lines + " LINES ****");
                writer.print(lines + ", ");

                // EXECUTE QUERIES
                String query;

                //**** DDL OPERATIONS **** (Data Definition)
                //DROP DATABASE
                query = "drop database if exists hivedb cascade";
                executeQuery(query, conHive, writer);

                query = "drop database if exists sharkdb cascade";
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

                query = "create table if not exists hivedb.house" + lines + " (house_id int, house_name string) " +
                        "row format delimited FIELDS TERMINATED BY ','";
                executeQuery(query, conHive, writer);

                query = "create table if not exists sharkdb.house" + lines + " (house_id int, house_name string) " +
                        "row format delimited FIELDS TERMINATED BY ','";
                executeQuery(query, conShark, writer);




                // **** DML OPERATIONS**** (Data Manupulation)
                //LOAD TABLES
                query = "LOAD DATA local INPATH " + filePath + " overwrite into table hivedb.data" + lines;
                executeQuery(query, conHive, writer);

                query = "LOAD DATA local INPATH " + filePath + " overwrite into table sharkdb.data" + lines;
                executeQuery(query, conShark, writer);

                //LOAD TABLES additional
                query = "LOAD DATA local INPATH " + filePath1 +" overwrite into table hivedb.house" + lines;
                executeQuery(query, conHive, writer);

                query = "LOAD DATA local INPATH " + filePath1 + " overwrite into table sharkdb.house" + lines;
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


                // ORDER BY
//                query = "SELECT * FROM hivedb.data" + lines + " ORDER BY house_id, household_id, plug_id, value";
                query = "SELECT * FROM hivedb.data" + lines + " ORDER BY house_id";
                executeQuery(query, conHive, writer);

//                query = "SELECT * FROM sharkdb.data" + lines + " ORDER BY house_id, household_id, plug_id, value";
                query = "SELECT * FROM sharkdb.data" + lines + " ORDER BY house_id";
                executeQuery(query, conShark, writer);


                //FILTER
                query = "SELECT * FROM hivedb.data" + lines + " WHERE value >= 5";
                executeQuery(query, conHive, writer);

                query = "SELECT * FROM sharkdb.data" + lines + " WHERE value >= 5";
                executeQuery(query, conShark, writer);

                //COUNT
                query = "SELECT COUNT (*) FROM hivedb.data" + lines;
                executeQuery(query, conHive, writer);

                query = "SELECT COUNT (*) FROM sharkdb.data" + lines;
                executeQuery(query, conShark, writer);

//                //inner queries
//                query = "SELECT * FROM hivedb.data" + lines + " WHERE house_id " +
//                        "IN (SELECT house_id FROM hivedb.data" + lines + " WHERE value > 5)";
//                System.out.println(query);
////                executeQuery(query, conHive, writer);
//
//                query = "SELECT * FROM sharkdb.data" + lines + " WHERE house_id " +
//                        "IN (SELECT house_id FROM sharkdb.data" + lines + " WHERE value>5)";
////                executeQuery(query, conShark, writer);


                //JOIN
                query = "USE hivedb";
                executeQuery(query, conHive, writer);
                query = "SELECT data" + lines + ".*, house" + lines + ".house_name " +
                        "FROM data" + lines + " " +
                        "JOIN house" + lines + " " +
                        "ON ( data" + lines + ".house_id = house" + lines + ".house_id)";
                executeQuery(query, conHive, writer);

                query = "USE sharkdb";
                executeQuery(query, conShark, writer);
                query = "SELECT data" + lines + ".*, house" + lines + ".house_name " +
                        "FROM data" + lines + " " +
                        "JOIN house" + lines + " " +
                        "ON ( data" + lines + ".house_id = house" + lines + ".house_id)";
                executeQuery(query, conShark, writer);



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


    public static ResultSet executeQuery(String query, Connection con, PrintWriter writer) {
        long start, end;
        float dur;

        System.out.print(query);

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

        System.out.print(" Result: " + result.toString() + " Elapsed time: " + dur + "ms");
        System.out.println();
        queries.add(query + "\n");
        writer.print(dur + ", ");

        return result;
    }

}
