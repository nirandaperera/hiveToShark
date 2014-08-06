import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by niranda on 8/6/14.
 */
public class dataReader {

    public static void main(String[] args) {

        String inFile = "/media/niranda/data/projects/hiveToShark/data/sorted400M.txt";

        String outDir = "/home/niranda/projects/hiveToShark/data/";

        int lineLimits[] = {100, 1000, 10000, 100000, 1000000};

        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(inFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {

            for (int j = 0; j < lineLimits.length; j++) {
                BufferedWriter fw = new BufferedWriter(new FileWriter(outDir + "doc" + lineLimits[j]+ ".txt"));
                String line;
                int i = 0;
                try {
                    while ((line = br.readLine()) != null && i < lineLimits[j]) {
                        fw.write(line);
                        fw.newLine();
                        i++;
                    }
                    fw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("doc with lines "+ lineLimits[j]+" DONE!");
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        System.out.println("DONE!");

    }
}
