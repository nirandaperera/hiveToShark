/**
 * Created by niranda on 8/6/14.
 */

import org.jruby.ext.ffi.Struct$i$initialize;
import org.yecht.Data;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class HiveSharkPerformanceEvaluator {

    private static String driverName = "org.apache.hadoop.hive.jdbc.HiveDriver";

    public static void main(String[] args) throws SQLException {

        //Create data files
        String inFile = "/media/niranda/data/projects/hiveToShark/data/sorted400M.txt";
        String outDir = "/home/niranda/projects/hiveToShark/data/";
        int lineLimits[] = {100, 1000, 10000, 100000, 1000000, 10000000, 100000000};
        //int lineLimits[] = {100};

        DataReader.createFiles(inFile, outDir, lineLimits);


        //Run the queries
        String query;

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
            String logPath = "logs/log2.txt";

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
//            System.out.println(lines);

                System.out.println("**** FILE WITH " + lines + " LINES ****");
                writer.print(lines + ", ");


                //DROP DATABASE
                query = "drop database if exists hivedb" + lines;
                executeQuery(query, conHive, writer);

                query = "drop database if exists sharkdb" + lines;
                executeQuery(query, conShark, writer);


                //CREATE DATABASE
                query = "create database if not exists hivedb";
                executeQuery(query, conHive, writer);

                query = "create database if not exists sharkdb";
                executeQuery(query, conShark, writer);


/*
                //DROP TABLES
                query = "drop table if exists hivedb.data" + lines;
                executeQuery(query, conHive, writer);
                query = "drop table if exists hivedb.datatemp" + lines;
                executeQuery(query, conHive, writer);

                query = "drop table if exists sharkdb.data" + lines;
                executeQuery(query, conShark, writer);
                query = "drop table if exists sharkdb.datatemp" + lines;
                executeQuery(query, conShark, writer);
*/


                //CREATE TABLES
                query = "create table if not exists hivedb.data" + lines + " (col1 string)";
                executeQuery(query, conHive, writer);
                query = "create table if not exists hivedb.datatemp" + lines + " (col1 string)";
                executeQuery(query, conHive, writer);

                query = "create table if not exists sharkdb.data" + lines + " (col1 string)";
                executeQuery(query, conShark, writer);
                query = "create table if not exists sharkdb.datatemp" + lines + " (col1 string)";
                executeQuery(query, conShark, writer);


                //LOAD TABLES
                query = "LOAD DATA local INPATH " + filePath + " overwrite into table hivedb.data" + lines;
                executeQuery(query, conHive, writer);

                query = "LOAD DATA local INPATH " + filePath + " overwrite into table sharkdb.data" + lines;
                executeQuery(query, conShark, writer);


                //SELECT AND INSERT
                query = "insert overwrite table hivedb.datatemp" + lines +
                        " SELECT " +
                        "col1 " +
                        "from hivedb.data" + lines;
                executeQuery(query, conHive, writer);

                query = "insert overwrite table sharkdb.datatemp" + lines +
                        " SELECT " +
                        "col1 " +
                        "from sharkdb.data" + lines;
                executeQuery(query, conShark, writer);

                System.out.println("**** FILE WITH " + lines + " LINES: DONE ****");

                writer.println();
            }

            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("DONE!");

    }


    public static void executeQuery(String query, Connection con, PrintWriter writer) {
        long start, end;
        float dur;

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


//        System.out.println(result.toString() + " Elapsed time: " + dur + "ms");
        System.out.println(query.substring(0, 20) + " Elapsed time: " + dur + "ms");
        writer.print(dur + ", ");
    }

}
