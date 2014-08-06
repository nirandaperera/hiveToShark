/**
 * Created by niranda on 8/6/14.
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class hiveSharkPerformanceEvaluator {

    private static String driverName = "org.apache.hadoop.hive.jdbc.HiveDriver";

    public static void main(String[] args) throws SQLException {

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


        String datafile = "'file:///media/niranda/data/projects/hiveToShark/data/doc1.txt'";

        String tbl_attrib = "col1 STRING,col2 STRING,col3 STRING,col4 STRING,col5 STRING,col6 STRING, col7 STRING";

        query = "drop table if exists tabledatahive";
        executeQuery(query, conHive);
        query = "drop table if exists tabletemphive";
        executeQuery(query, conHive);

        query = "create table if not exists tabledatahive (col1 string)";
        executeQuery(query, conHive);

        query = "LOAD DATA local INPATH "+datafile+" overwrite into table tabledatahive";
        executeQuery(query, conHive);

        query = "create table if not exists tabletemphive (col1 string)";
        executeQuery(query, conHive);

        query = "insert overwrite table tabletemphive " +
                "SELECT " +
                "col1 "+
                "from tabledatahive " ;
        executeQuery(query, conHive);


        System.out.println("DONE!");

    }

    public static void executeQuery(String query, Connection con) {
        long start, end;
        float dur;
        ResultSet result = null;
        Statement stmt = null;
        try {
            stmt = con.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }

//        System.out.println(query);
        start = System.nanoTime();
        try {
            result = stmt.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        end = System.nanoTime();
        dur = (float) (end - start);
        dur = dur / (1000000);
//        System.out.println(result.toString() + " Elapsed time: " + dur + "ms");
        System.out.println(query.substring(0,20) + " Elapsed time: " + dur + "ms");
    }
}
