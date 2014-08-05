
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by niranda on 8/4/14.
 */
public class sharkClientHdfs {

    private static String driverName = "org.apache.hadoop.hive.jdbc.HiveDriver";


    public static void main(String[] args) throws Exception {

        String query;
        long start, end;

        try {
            Class.forName(driverName);
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.exit(1);
        }

        Connection con = DriverManager.getConnection("jdbc:hive://10.100.5.109:10000/default", "", "");
        String hdfsURI1 = "'hdfs://localhost:54310/niranda/DEBSdata'";
        String hdfsURI2 = "'hdfs://localhost:54310/niranda/DEBSdataCopy'";
//        String datafile = "'file:///media/niranda/data/projects/hiveToShark/data/sorted400M.txt'";
        String datafile = "'file:///media/niranda/data/projects/hiveToShark/data/doc1.txt'";


        /**
         * id â€“ a unique identifier of the measurement [64 bit unsigned integer value]
         * timestamp â€“ timestamp of measurement (number of seconds since January 1, 1970, 00:00:00 GMT) [32 bit unsigned integer value]
         * value â€“ the measurement [32 bit floating point]
         * property â€“ type of the measurement: 0 for work or 1 for load [boolean]
         * plug_id â€“ a unique identifier (within a household) of the smart plug [32 bit unsigned integer value]
         * household_id â€“ a unique identifier of a household (within a house) where the plug is located [32 bit unsigned integer value]
         * house_id â€“ a unique identifier of a house where the household with the plug is located [32 bit unsigned integer value]
         */

        String tbl_attrib = "col1 STRING,col2 STRING,col3 STRING,col4 STRING,col5 STRING,col6 STRING, col7 STRING";
       /* query = "CREATE EXTERNAL TABLE IF NOT EXISTS TableData (" + tbl_attrib + ") LOCATION " + hdfsURI1;
        executeQuery(query, con);

        query = "create table if not exists tabledata1 like tabledata location " + hdfsURI2;
        executeQuery(query, con);

        query = "insert overwrite table TableData1 select col1, col2, col3, col4, col5, col6, col7 FROM TableData";
        executeQuery(query, con);*/

        query = "drop table if exists tabledata";
        executeQuery(query, con);
        query = "drop table if exists tabletemp";
        executeQuery(query, con);

        query = "create table if not exists tabledata (col1 string)";
        executeQuery(query, con);

        query = "LOAD DATA local INPATH "+datafile+" overwrite into table tabledata";
        executeQuery(query, con);
//
//        query = "create table if not exists dataFormatted (id bigint, " +
//                "timestamp int, " +
//                "value float, " +
//                "measure_type boolean, " +
//                "plug_id int, " +
//                "household_id int, " +
//                "house_id int)";
//        executeQuery(query, con);
//
//        query = "insert overwrite table dataFormatted " +
//                "SELECT " +
//                "  regexp_extract(col1, '^(?:([^,]*)\\,?){1}', 1) id," +
//                "  regexp_extract(col1, '^(?:([^,]*)\\,?){2}', 1) timestamp," +
//                "  regexp_extract(col1, '^(?:([^,]*)\\,?){3}', 1) value," +
//                "  regexp_extract(col1, '^(?:([^,]*)\\,?){4}', 1) measure_type," +
//                "  regexp_extract(col1, '^(?:([^,]*)\\,?){5}', 1) plug_id," +
//                "  regexp_extract(col1, '^(?:([^,]*)\\,?){6}', 1) household_id," +
//                "  regexp_extract(col1, '^(?:([^,]*)\\,?){7}', 1) house_id " +
//                "from tabledata " ;
//        executeQuery(query, con);

        query = "create table if not exists tabletemp (col1 string)";
        executeQuery(query, con);

        query = "insert overwrite table tabletemp " +
                "SELECT " +
                "col1 "+
                "from tabledata " ;
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
//        System.out.println(result.toString() + " Elapsed time: " + dur + "ms");
        System.out.println(query.substring(0,20) + " Elapsed time: " + dur + "ms");
    }
}
