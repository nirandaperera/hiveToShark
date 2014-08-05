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

        query = "create table if not exists tabledatahive (col1 string)";
        executeQuery(query, con);


        System.out.println("DONE!");

    }

    public static void executeQuery(String query, Connection con) {
        long start, end;
        ResultSet result = null;
        Statement stmt = null;
        try {
            stmt = con.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        System.out.println(query);
        start = System.nanoTime();
        try {
            result = stmt.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        end = System.nanoTime();
        System.out.println(result.toString() + " Elapsed time: " + (end - start));
    }
}
