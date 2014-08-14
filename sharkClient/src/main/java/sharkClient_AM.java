import java.sql.*;

public class sharkClient_AM {
    private static String driverName = "org.apache.hadoop.hive.jdbc.HiveDriver";

    public static void main(String[] args) throws SQLException {

        try {
            Class.forName(driverName);
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.exit(1);
        }

        Connection con = DriverManager.getConnection("jdbc:hive://10.100.5.109:10000/default", "", "");
        Statement stmt = con.createStatement();
        String query;
        ResultSet result;

        String cass_ds_props = "\"cassandra.host\"=\"localhost\",\"cassandra.port\"=\"9163\",\"cassandra.ks.name\"=\"EVENT_KS\",\"cassandra.ks.username\"=\"admin\",\"cassandra.ks.password\"=\"admin\"";
        String jdbc_ds_props = "'mapred.jdbc.driver.class' = 'org.h2.Driver', 'mapred.jdbc.url' = 'jdbc:h2:/home/niranda/software/wso2bam-2.4.1/repository/database/APIMGTSTATS_DB', 'mapred.jdbc.username' = 'wso2carbon' , 'mapred.jdbc.password' = 'wso2carbon'";

        String req_tbl_attrib = "key STRING,api STRING,api_version STRING,consumerKey STRING,userId STRING,context STRING,version STRING, request INT, requestTime BIGINT, resourcePath STRING, method STRING, hostName STRING, apiPublisher STRING";
        String cass_sto_hand = "'org.apache.hadoop.hive.cassandra.CassandraStorageHandler'";
        String cass_serde_props = cass_ds_props +
                ", \"cassandra.cf.name\" = \"org_wso2_apimgt_statistics_request\"," +
                "\"cassandra.columns.mapping\" = \":key,payload_api, payload_api_version,payload_consumerKey,payload_userId,payload_context,payload_version, payload_request, payload_requestTime, payload_resourcePath, payload_method,payload_hostName,payload_apiPublisher\"";
//        String cass_ds_config = "\"cassandra.host\"=\"localhost\",\"cassandra.port\"=\"9163\",\"cassandra.ks.name\"=\"EVENT_KS\",\"cassandra.ks.username\"=\"admin\",\"cassandra.ks.password\"=\"admin\"";
//        String cass_cf_map = "\"cassandra.cf.name\" = \"org_wso2_apimgt_statistics_request55\",\"cassandra.columns.mapping\" = \":key,payload_api, payload_api_version,payload_consumerKey,payload_userId,payload_context,payload_version, payload_request, payload_requestTime, payload_resourcePath, payload_method,payload_hostName,payload_apiPublisher\"";

        //CREATE EXTERNAL TABLE IF NOT EXISTS APIRequestData
        query = "CREATE EXTERNAL TABLE IF NOT EXISTS APIRequestData (" + req_tbl_attrib + ") STORED BY " + cass_sto_hand + " WITH SERDEPROPERTIES (" + cass_serde_props + ")";
        System.out.println(query);
        result = stmt.executeQuery(query);
        System.out.println(result.toString());


        String req_sum_tbl_attrib = "api STRING, api_version STRING, version STRING, apiPublisher STRING, consumerKey STRING,userId STRING,context STRING, max_request_time BIGINT, total_request_count INT, hostName STRING,year SMALLINT,month SMALLINT,day SMALLINT, time STRING";
        String jdbc_sto_hand = "'org.wso2.carbon.hadoop.hive.jdbc.storage.JDBCStorageHandler'";
        String jdbc_tbl_props = jdbc_ds_props +
                ", 'hive.jdbc.update.on.duplicate' = 'true', " +
                "'hive.jdbc.primary.key.fields'='api,api_version,version,apiPublisher,consumerKey,userId,context,hostName,time', " +
                "'hive.jdbc.table.create.query' = 'CREATE TABLE API_REQUEST_SUMMARY ( api VARCHAR(100), api_version VARCHAR(100), version VARCHAR(100), apiPublisher VARCHAR(100),consumerKey VARCHAR(100),userId VARCHAR(100), context VARCHAR(100), max_request_time BIGINT, total_request_count INT, hostName VARCHAR(100), year SMALLINT, month SMALLINT, day SMALLINT, time VARCHAR(30),PRIMARY KEY(api,api_version,apiPublisher,consumerKey,userId,context,hostName,time))'";

        query = "CREATE EXTERNAL TABLE IF NOT EXISTS APIRequestSummaryData (" + req_sum_tbl_attrib + ") STORED BY " + jdbc_sto_hand + " TBLPROPERTIES (" + jdbc_tbl_props + ")";
        System.out.println(query);
        long startTime = System.nanoTime();
        result = stmt.executeQuery(query);
        long endTime = System.nanoTime();
        System.out.println(result.toString() + " Elapsed time: " + (endTime - startTime));

        query = "insert overwrite table APIRequestSummaryData " +
                "select api, api_version,version, apiPublisher, COALESCE(consumerKey,''),userId,context," +
                "max(requestTime) as max_request_time, sum(request) as total_request_count, hostName,  " +
                "year(from_unixtime(cast(requestTime/1000 as BIGINT),'yyyy-MM-dd HH:mm:ss.SSS' )) as year, " +
                "month(from_unixtime(cast(requestTime/1000 as BIGINT),'yyyy-MM-dd HH:mm:ss.SSS' )) as month," +
                "day(from_unixtime(cast(requestTime/1000 as BIGINT),'yyyy-MM-dd HH:mm:ss.SSS' )) as day," +
                "concat(substring(from_unixtime(cast(requestTime/1000 as BIGINT), 'yyyy-MM-dd HH:mm:ss'),0,16),':00') as time " +
                "from APIRequestData " +
                "group by " +
                "api,api_version,version,apiPublisher,consumerKey,userId,context,hostName," +
                "year(from_unixtime(cast(requestTime/1000 as BIGINT),'yyyy-MM-dd HH:mm:ss.SSS' )), " +
                "month(from_unixtime(cast(requestTime/1000 as BIGINT),'yyyy-MM-dd HH:mm:ss.SSS' ))," +
                "day(from_unixtime(cast(requestTime/1000 as BIGINT),'yyyy-MM-dd HH:mm:ss.SSS' ))," +
                "hour(from_unixtime(cast(requestTime/1000 as BIGINT),'yyyy-MM-dd HH:mm:ss.SSS' ))," +
                "minute(from_unixtime(cast(requestTime/1000 as BIGINT),'yyyy-MM-dd HH:mm:ss.SSS' ))," +
                "substring(from_unixtime(cast(requestTime/1000 as BIGINT), 'yyyy-MM-dd HH:mm:ss'),0,16)";
        System.out.println(query);
        result = stmt.executeQuery(query);
        System.out.println(result.toString());

        con.close();

        System.out.println("End");
    }
}

