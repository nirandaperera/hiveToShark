import org.apache.spark.sql.api.java.JavaSchemaRDD;
import org.apache.spark.sql.hive.api.java.JavaHiveContext;

/**
 * Created by niranda on 8/19/14.
 */
public class QueryExecutorHive {

    private String query = "";
    private long start, end;
    private JavaHiveContext hiveCtx;
    private JavaSchemaRDD hiveResult;
    private TimingCalc timer = new TimingCalc();

    QueryExecutorHive() {
    }

    QueryExecutorHive(JavaHiveContext hiveCtx) {
        this.hiveCtx = hiveCtx;
    }

    public void setHiveCtx(JavaHiveContext hiveCtx) {
        this.hiveCtx = hiveCtx;
    }

    public JavaSchemaRDD executeQuery(String query) {
        this.query = query;

        start = System.nanoTime();
        hiveResult = hiveCtx.hql(query);
        end = System.nanoTime();
//        System.out.println(start-end);
        return hiveResult;
    }

    public String getQueryString() {
        return query;
    }

    public float getExecutionTimeInMilli() {
        return timer.getTimeInMilli(start, end);
    }

    public float getExecutionTime(int type) {
        float time;
        switch (type) {
            case 1:
                time = timer.getTimeInMilli(start, end);
                break;
            case 2:
                time = timer.getTimeInMicro(start, end);
                break;
            case 3:
                time = timer.getTimeInNano(start, end);
                break;
            default:
                time = timer.getTimeInMilli(start, end);
        }

        return time;
    }

}
