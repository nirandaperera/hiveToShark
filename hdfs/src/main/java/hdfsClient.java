

/**
 * Created by niranda on 8/1/14.
 */

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.IOException;


public class hdfsClient {
    private final static String PROP_NAME = "fs.default.name";
    private static String driverName = "org.apache.hadoop.hive.jdbc.HiveDriver";
    private static String HADOOP_HOME = "/home/niranda/software/hadoop-1.2.1";


    public static void main(String[] args) throws Exception {

        Path inPath = new Path("/media/niranda/data/projects/hiveToShark/data/sorted400M.txt");
        Path outPath = new Path("/niranda/DEBSdata");

        System.out.println("Time taken to load data to HDFS: " + LoadDataToHdfs(inPath, outPath));
    }

    public static String LoadDataToHdfs(Path inPath, Path outPath) {

        long start, end, dur;


        Configuration conf = new Configuration();
//        System.out.println("After construction: " + conf.get(PROP_NAME));
        conf.addResource(new Path(HADOOP_HOME + "/conf/core-site.xml"));
        conf.addResource(new Path(HADOOP_HOME + "/conf/hdfs-site.xml"));
        conf.addResource(new Path(HADOOP_HOME + "/conf/mapred-site.xml"));
//        System.out.println("After addResource: " + conf.get(PROP_NAME));

        FileSystem fs = null;
        try {
            fs = FileSystem.get(conf);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(fs.toString());

        start = System.nanoTime();
        try {
            fs.copyFromLocalFile(inPath, outPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        end = System.nanoTime();

        dur = end - start;
//        System.out.println("Time taken: " + (end - start));

        return String.valueOf(dur);

    }

}
