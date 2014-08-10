import java.io.*;
import java.util.UUID;

/**
 * Created by niranda on 8/6/14.
 */
public class DataReader {

    public static void main(String[] args) {

        String inFile = "/media/niranda/data/projects/hiveToShark/data/sorted400M.txt";

        String outDirData = "/home/niranda/projects/hiveToShark/data/data/";
        String outDirAdd = "/home/niranda/projects/hiveToShark/data/additional/";


        int lineLimits[] = {100, 1000, 10000, 100000, 1000000, 10000000, 100000000};

//        createFiles(inFile, outDirData, lineLimits);
        createAdditionalFiles(inFile, outDirAdd, lineLimits);

        System.out.println("DONE!");

    }

    public static void createFiles(String inFile, String outDir, int[] lineLimits) {

        System.out.println("**** CREATING DATA FILES: START ****");


        try {

            for (int j = 0; j < lineLimits.length; j++) {

                BufferedReader br = null;
                try {
                    br = new BufferedReader(new FileReader(inFile));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }


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

                br.close();

                System.out.println("doc with lines " + lineLimits[j] + " DONE!");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("**** CREATING DATA FILES: DONE ****");
    }

    public static void createAdditionalFiles(String inFile, String outDir, int[] lineLimits) {

        System.out.println("**** CREATING ADDITIONAL DATA FILES: START ****");


        try {

            for (int j = 0; j < lineLimits.length; j++) {


                String fileName = outDir + "house" + lineLimits[j] + ".txt";
                File file = new File(fileName);

                if (file.exists()) {
                    System.out.println("additional doc " + lineLimits[j] + " EXISTS!");
                    continue;
                }


                BufferedReader br = null;
                try {
                    br = new BufferedReader(new FileReader(inFile));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }


                String line;
                int maxValue = 0;
                int i = 0;

                //find the max value
                try {
                    while ((line = br.readLine()) != null && i < lineLimits[j]) {

                        String str[] = line.split(",");
                        int temp = Integer.parseInt(str[6]);
                        if (temp > maxValue) maxValue = temp;
                        i++;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // create the doc
                try {
                    BufferedWriter fw = new BufferedWriter(new FileWriter(fileName));

                    for (int k = 0; k < maxValue; k++) {
                        fw.write((k + 1) + ", " + UUID.randomUUID().toString() + "\n");
                    }
                    fw.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }

                br.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("**** CREATING ADDITIONAL DATA FILES: DONE ****");

    }

}
