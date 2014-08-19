import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.spark.sql.api.java.JavaSQLContext;
import org.apache.spark.sql.api.java.JavaSchemaRDD;
import scala.util.parsing.combinator.testing.Str;

/**
 * Created by niranda on 8/19/14.
 */
public class QueryExecutor {

    private String query = "";
    private long start, end;
    private JavaSQLContext sqlCtx;
    private JavaSchemaRDD sqlResult;
    private TimingCalc timer = new TimingCalc();

    QueryExecutor() {
    }

    QueryExecutor(JavaSQLContext sqlCtx) {
        this.sqlCtx = sqlCtx;
    }

    public void setSqlCtx(JavaSQLContext sqlCtx) {
        this.sqlCtx = sqlCtx;
    }

    public JavaSchemaRDD executeQuery(String query) {
        this.query = query;

        start = System.nanoTime();
        sqlResult = sqlCtx.sql(query);
        end = System.nanoTime();

        return sqlResult;
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
