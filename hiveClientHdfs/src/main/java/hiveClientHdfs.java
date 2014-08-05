/**
 * Created by niranda on 8/5/14.
 */

import java.sql.SQLException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.DriverManager;

public class hiveClientHdfs {
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
        Connection con = DriverManager.getConnection("jdbc:hive://0.0.0.0:10001/default", "", "");
//        Statement stmt = con.createStatement();

        String datafile = "'file:///media/niranda/data/projects/hiveToShark/data/doc1.txt'";

        String tbl_attrib = "col1 STRING,col2 STRING,col3 STRING,col4 STRING,col5 STRING,col6 STRING, col7 STRING";

        query = "drop table if exists tabledatahive";
        executeQuery(query, con);
        query = "drop table if exists tabletemphive";
        executeQuery(query, con);

        query = "create table if not exists tabledatahive (col1 string)";
        executeQuery(query, con);

        query = "LOAD DATA local INPATH "+datafile+" overwrite into table tabledatahive";
        executeQuery(query, con);

        query = "create table if not exists tabletemphive (col1 string)";
        executeQuery(query, con);

        query = "insert overwrite table tabletemphive " +
                "SELECT " +
                "col1 "+
                "from tabledatahive " ;
        executeQuery(query, con);


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
        System.out.println(query.substring(0,20) + " Elapsed time: " + dur + "ms");
    }
}
