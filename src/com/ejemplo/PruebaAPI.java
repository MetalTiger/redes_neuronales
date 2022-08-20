package com.ejemplo;

import com.ch13.Paginas;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class PruebaAPI {

    public static void main(String[] args) throws URISyntaxException, IOException {

        invoke();



    }

    public static void invoke() throws URISyntaxException, IOException {

        HttpClient client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("https://dog.ceo/api/breeds/image/random"))
                .GET()
                .timeout(Duration.ofSeconds(10))
                .build();

        final String[] rawJSON = {""};

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(x -> rawJSON[0] = x)
                .join();

        Gson gson = new Gson();
        response prueba = gson.fromJson(rawJSON[0], response.class);

        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            System.out.println("Abriendo en Navegador");
            Desktop.getDesktop().browse(new URI(prueba.message));
        }else{
            System.out.println("No hay soporte para el navegador.");
        }

        System.out.println(prueba.message);

    }

    class responseAPI {
        protected response response;
    }

    class response {
        protected String message;
        protected String status;

    }

}
