package CourseProject;

import org.json.JSONException;
import org.json.JSONObject;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;



public class GetUrlToString {
    public static void main(String[] args) {

        String topic = "project_1" ;

        try
        {
            CheckServer();
        }
        catch (Exception e)
        {

        }

        try {
            String UrlToAsk = "http://data.fixer.io/api/latest?access_key=<KEY>&symbols=USD,AUD,CAD,PLN,MXN&format=1";
            String Symbol = "USD";
            float value = 0;

            JSONObject response = ProduceData(UrlToAsk);
            value = GetFinancialValue(Symbol,response) ;

            System.out.println(value);

            ProducerProject.SendToKafka(topic,String.valueOf(value));

        } catch (Exception e) {
            System.out.println(e.getStackTrace().toString());
        }
    }

    private static JSONObject ProduceData(String UrlToAsk) throws IOException
    {
        JSONObject MonObjetJson = new JSONObject();
        URL myurl = new URL(UrlToAsk);
        String readLine = null;

        // Create the connection
        HttpURLConnection connection = (HttpURLConnection) myurl.openConnection();

        //configure it
        connection.setRequestMethod("GET");

        // Get the answer
        int responseCode = connection.getResponseCode();

        // Check if everything Ok
        if (responseCode == HttpURLConnection.HTTP_OK) {

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            StringBuffer response = new StringBuffer();

            while ((readLine = in.readLine()) != null) {
                response.append(readLine);
            }
            in.close();

            try {
                MonObjetJson = new JSONObject(response.toString());

            } catch (JSONException err) {
                System.out.println(err);
                System.exit(1);
            }
        } else {
            System.out.println("GET NOT WORKED");
            System.exit(2);
        }

        return MonObjetJson;
    }


    private static float GetFinancialValue(String symbole, JSONObject Data)
    {
        float result = 0;
        JSONObject rates = new JSONObject();

        rates = Data.getJSONObject("rates");

        result =  rates.getFloat(symbole);

        return result;

        /* OU

        result = Data.getJSONObject("rates").getFloat("USD");

         */
    }

    private static void CheckServer() throws IOException
    {
        try {
            /* Check if zookeeper is up */
            URL myurl = new URL("http://localhost:2181");


            // Create the connection
            HttpURLConnection connection = (HttpURLConnection) myurl.openConnection();

            // Get the answer
            int responseCode = connection.getResponseCode();

            if (responseCode == 200)
            {
                System.out.println("Serveur Zookeeper ok");
            }
        }
        catch(Exception e)
        {
            System.out.println("Serveur Zookeeper non disponible");
        }
        /*  Check if Kafka is up
        */
    }
}

