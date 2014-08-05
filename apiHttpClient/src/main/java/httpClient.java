/**
 * Created by niranda on 7/23/14.
 * project shark to hive. client for making API requests
 */

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.util.Random;


public class httpClient {

    public static void main(String[] args) throws Exception {

        String accessToken = "9da52523599387bb9294f63be83b35df";
        int requests = 1000;

        for (int i = 0; i < requests; i++) {

            System.out.print("Sending request " + i + " ");
            Random rand = new Random();

            if (rand.nextInt(2) == 0) wikiApiReq(accessToken);
            else youtubeApiReq(accessToken);

            //delay for processing the second request;
            delay(10);

            System.out.print(" DONE!\n");
        }
    }

    private static void delay(int mSec) {
        try {
            Thread.sleep(mSec);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    private static void wikiApiReq(String apiKey) throws IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {

            HttpGet httpGet = new HttpGet(
                    "http://10.100.5.109:8280/wikipedia/1.0.0?format=json&action=query&titles=MainPage&prop=revisions&rvprop=content");
            httpGet.addHeader("Authorization", "Bearer " + apiKey);
            CloseableHttpResponse response1 = httpclient.execute(httpGet);

            try {
                //System.out.println(response1.toString());
                System.out.print("wiki = " + response1.getStatusLine().toString());
//                HttpEntity entity1 = response1.getEntity();
//                EntityUtils.consume(entity1);
            } finally {
                response1.close();
            }

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void youtubeApiReq(String apiKey) throws IOException {

        String opts[] = {"most_viewed", "most_shared", "top_rated", "most_popular"};
        Random randomGenerator = new Random();

        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {

            HttpGet httpGet = new HttpGet(
                    "http://10.100.5.109:8280/youtube/1.0.0/" + opts[randomGenerator.nextInt(4)]);
            httpGet.addHeader("Authorization", "Bearer " + apiKey);
            CloseableHttpResponse response1 = httpclient.execute(httpGet);

            try {
//                System.out.println(response1.toString());
                System.out.print("youtube = " + response1.getStatusLine().toString());
//                System.out.println(EntityUtils.toString(response1.getEntity()));
//                HttpEntity entity1 = response1.getEntity();
//                EntityUtils.consume(entity1);
            } finally {
                response1.close();
            }

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
