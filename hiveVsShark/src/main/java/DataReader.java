import java.io.*;

/**
 * Created by niranda on 8/6/14.
 */
public class DataReader {

    public static void main(String[] args) {

        String inFile = "/media/niranda/data/projects/hiveToShark/data/sorted400M.txt";

        String outDir = "/home/niranda/projects/hiveToShark/data/";

        int lineLimits[] = {100, 1000, 10000, 100000, 1000000};

        createFiles(inFile, outDir, lineLimits);

        System.out.println("DONE!");

    }

    public static void createFiles(String inFile, String outDir, int[] lineLimits) {

        System.out.println("**** CREATING DATA FILES: START ****");
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(inFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {

            for (int j = 0; j < lineLimits.length; j++) {
                String filename = outDir + "doc" + lineLimits[j] + ".txt";
                File file = new File(filename);

                if (file.exists()) {
                    System.out.println("doc with lines " + lineLimits[j] + " EXISTS!");
                    continue;
                }

                BufferedWriter fw = new BufferedWriter(new FileWriter(filename));
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
                System.out.println("doc with lines " + lineLimits[j] + " DONE!");
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("**** CREATING DATA FILES: DONE ****");
    }

}
