package com.ch13;

import java.net.*;
import java.util.*;
import java.io.*;
import javax.net.ssl.HttpsURLConnection;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class BingWebSearch {

    //f9bae53b874140cc9b8bbf297613c5d8
    static String subscriptionKey = "f9bae53b874140cc9b8bbf297613c5d8";

    /*
     * If you encounter unexpected authorization errors, double-check these values
     * against the endpoint for your Bing Web search instance in your Azure
     * dashboard.
     */
    static String host = "https://api.bing.microsoft.com";
    static String path = "/v7.0/search";

    public Collection<URL> search(String name) {

        String searchTerm = name;

        // Confirm the subscriptionKey is valid.
        if (subscriptionKey.length() != 32) {
            System.out.println("Invalid Bing Search API subscription key!");
            System.out.println("Please paste yours into the source code.");
            System.exit(1);
        }

        // Call the SearchWeb method and print the response.
        try {

            Collection<URL> urls = new ArrayList<>();

            //System.out.println("Searching the Web for: " + searchTerm);
            SearchResults result = SearchWeb(searchTerm);
            /*System.out.println("\nRelevant HTTP Headers:\n");
            for (String header : result.relevantHeaders.keySet())
                System.out.println(header + ": " + result.relevantHeaders.get(header));
            System.out.println("\nJSON Response:\n");
            */
            Gson gson = new Gson();

            Paginas prueba = gson.fromJson(result.jsonResponse, Paginas.class);

            System.out.println(name);

            for (value item: prueba.webPages.value) {

                urls.add(new URL(item.url));
                System.out.println(item.getUrl());

            }

            //System.out.println(prueba.toString());

            return urls;

            //System.out.println(prettify(result.jsonResponse));
        } catch (Exception e) {
            e.printStackTrace(System.out);

        }
        return null;
    }


    /*public static void main (String[] args) {

    }*/

    public static SearchResults SearchWeb (String searchQuery) throws Exception {
        // Construct the URL.
        URL url = new URL(host + path
                + "?q=" +  URLEncoder.encode(searchQuery, "UTF-8")
                + "&count=30"
                + "&responseFilter=webpages"
                + "&mkt=en-US"
        );

        // Open the connection.
        HttpsURLConnection connection = (HttpsURLConnection)url.openConnection();
        connection.setRequestProperty("Ocp-Apim-Subscription-Key", subscriptionKey);

        // Receive the JSON response body.
        InputStream stream = connection.getInputStream();
        String response = new Scanner(stream).useDelimiter("\\A").next();

        // Construct the result object.
        SearchResults results = new SearchResults(new HashMap<String, String>(), response);

        // Extract Bing-related HTTP headers.
        Map<String, List<String>> headers = connection.getHeaderFields();
        for (String header : headers.keySet()) {
            if (header == null) continue;      // may have null key
            if (header.startsWith("BingAPIs-") || header.startsWith("X-MSEdge-")){
                results.relevantHeaders.put(header, headers.get(header).get(0));
            }
        }
        stream.close();
        return results;
    }

    public static String prettify(String json_text) {
        JsonParser parser = new JsonParser();
        JsonObject json = parser.parse(json_text).getAsJsonObject();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(json);
    }

}
