package com.ejemplo;

import com.google.gson.internal.bind.util.ISO8601Utils;

import java.io.IOException;
import java.lang.invoke.CallSite;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Pruebas {

    public static void main(String[] args) throws IOException {

        IntStream
                .range(1, 10)
                .skip(5)
                .forEach(x -> System.out.println(x));

        System.out.println();

        System.out.println(
                IntStream
                        .range(1, 5)
                        .sum()
        );
        System.out.println();

        Stream.of("Kevin", "Karen", "Juan")
                .sorted()
                .findFirst()
                .ifPresent(System.out::println);
        System.out.println();

        // Filter
        String[] nombres = {"Kevin", "Karen", "Juan", "Carlos", "Pedro", "Pito", "Laura"};
        Arrays.stream(nombres)
                .filter(nombre -> nombre.startsWith("P"))
                .sorted()
                .forEach(x -> System.out.println(x.toLowerCase()));
        System.out.println();

        // Map
        Arrays.stream(new int[] {2, 4, 6, 8, 10})
                .map(x -> x + 5)
                .forEach(System.out::println);
        System.out.println();

        // Stream desde una listam filtrar e imprimir
        List<String> personas = Arrays.asList("Kevin", "Karen", "Juan", "Carlos", "Pedro", "Pito", "Laura");
        personas
                .stream()
                .map(String::toLowerCase)
                .filter(x -> x.startsWith("k"))
                .forEach(System.out::println);
        System.out.println();

        // Crear Stream a partir de un txt, ordenarlo, hacer split, filtrarlo e imprimirlo
        Stream<String> famosos = Files.lines(Paths.get("./famous.csv"));
        famosos
                .sorted()
                .map(x -> x.split(","))
                .filter(x -> x[0].length() > 20)
                .forEach(x -> System.out.println(x[0]));
        famosos.close();
        System.out.println();

        int anio = 1970;

        // Leer datos de un txt, crear un objeto de cada renglon, filtarlos y guardarlos en una lista
        List<Persona> famosos2 = Files.lines(Paths.get("./famous.csv"))
                .skip(1)
                .map(x -> {
                    String[] dato = x.split(",");
                    return new Persona(dato[0], Integer.parseInt(dato[1]));
                })
                .filter(x -> x.year > anio)
                .collect(Collectors.toList());

        if (famosos2.isEmpty()){
            System.out.println("No hay famosos nacidos despues de " + anio);
        }else {
            famosos2.forEach(x -> System.out.println(x.name));
        }

        // Reduce como sum
        double total = Stream.of(7.3, 1.5, 4.8)
                .reduce(0.0, (Double a ,Double b) -> a + b);
        System.out.println("Total = " + total);

        String palabra = Stream.of("a", "b", "c")
                .reduce("", String::concat);
        System.out.println(palabra);

        IntSummaryStatistics summary = IntStream.of(7, 2, 19, 88, 73, 4, 10)
                .summaryStatistics();

        System.out.println(summary);


    }


    static class Persona{
        protected String name;
        protected int year;

        Persona(String nombre, int anio){
            this.name = nombre;
            this.year = anio;
        }

    }

}
