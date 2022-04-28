package com.ch8.incrementalxor;

import neural.feedforward.FeedforwardLayer;
import neural.feedforward.FeedforwardNetwork;
import neural.feedforward.train.Train;
import neural.feedforward.train.backpropagation.Backpropagation;
import neural.matrix.Matrix;

import java.time.Duration;
import java.time.Instant;

public class XOR {

    public static double[][] XOR_INPUT = {
            { 1.0, 1.0 },
            { 1.0, 0.0 },
            { 0.0, 1.0 },
            { 0.0, 0.0 },
    };

    public static double[][] XOR_IDEAL = {
            { 0.0 },
            { 1.0 },
            { 1.0 },
            { 0.0 },
    };

    public static void main(final String[] args) {

        int hiddenNeurons = 5;
        double error = 0.00001;

        Duration[] durations = new Duration[hiddenNeurons];
        double[] epochs = new double[hiddenNeurons];
        double[] errores = new double[hiddenNeurons];

        for (int x = 1; x < hiddenNeurons; x++) {

            final FeedforwardNetwork network = new FeedforwardNetwork();
            network.addLayer(new FeedforwardLayer(2));
            network.addLayer(new FeedforwardLayer(x + 1));
            network.addLayer(new FeedforwardLayer(1));
            network.reset(); // Inicializa los pesos

            // train the neural network
            final Train train = new Backpropagation(network, XOR_INPUT, XOR_IDEAL, 0.7, 0.3);

            Instant inst1 = Instant.now();

            System.out.println("Entrenamiento con: " + (x + 1) + " neuronas.");

            int epoch = 1;

            do {

                train.iteration();

                /*System.out.println("Epoch #" + epoch + " Error:" + train.getError());*/

                epoch++;

            } while (train.getError() > error); // (epoch <= 800) && (train.getError() > 0.00001)

            epochs[x] = epoch;

            Instant inst2 = Instant.now();

            durations[x] = Duration.between(inst1, inst2);

            errores[x] = train.getError();

        }

        System.out.println("Resultados para un error de: " + error);

        System.out.println("# Neuronas\t\tÉpocas\t\tTiempo\t\t\t\tError");

        for (int i = 1; i < hiddenNeurons; i++){

            String segundos = durations[i].getSeconds() + "." + durations[i].getNano() + " segundos. ";

            System.out.println("\t" + (i + 1) + "\t\t" + epochs[i] + "\t\t" + segundos +"\t"+errores[i]);

        }

    }

}
